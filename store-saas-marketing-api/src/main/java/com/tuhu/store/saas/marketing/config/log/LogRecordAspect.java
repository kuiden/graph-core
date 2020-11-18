package com.tuhu.store.saas.marketing.config.log;

import com.alibaba.fastjson.JSON;
import com.tuhu.springcloud.common.constant.ApiCommonConstant;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.SysReqLog;
import com.tuhu.store.saas.marketing.remote.CoreUser;
import com.tuhu.store.saas.marketing.sys.SysReqLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * aop 记录请求和返回数据日志
 * @author yangshengyong
 * @since 2020-11-18
 */
@Aspect
@Configuration
@Order(ApiCommonConstant.ORDERED_CUSTOM_HIGHEST - 10)
public class LogRecordAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogRecordAspect.class);
    private static AntPathMatcher matcher = new AntPathMatcher();
    @Value("${sys.req.whitelist.urls:/feign/**}")
    private String SYS_REQ_WHITELIST_URLS;
    @Value("${sys.req.save.switch:true}")
    private Boolean SAVE_SWITCH;

    @Autowired
    private SysReqLogService sysReqLogService;

    @Pointcut("execution(public * com.tuhu.store.saas.marketing.controller..*.*(..))")
    public void excudeService() {
    }

    @Around("excudeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = new Date().getTime();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        List<Object> objects = new ArrayList<>();
        try {
            Object[] args = pjp.getArgs();
            for (int i = 0; i < args.length; i++) {
                objects.add(args[i]);
                logger.info(method + "doAround, url: {}, method: {}, uri: {}, params: {}", url, method, uri, args[i]);
            }
        } catch (Exception e) {
            logger.info(method + "doAround, url: {}, method: {}, uri: {}, params: {}", url, method, uri, queryString);
        }
        Object result = pjp.proceed();
        long endTime = new Date().getTime();
        long time = endTime - startTime;
        if (SAVE_SWITCH) {
            saveReqLog(request, objects, result, time);
        }
        return result;
    }

    /**
     * 保存请求日志信息
     *
     * @param request
     * @param reqObjects
     * @param result
     */
    private void saveReqLog(HttpServletRequest request, List<Object> reqObjects, Object result, long time) {
        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String url = request.getRequestURL().toString();
            String[] whiteListUrls = SYS_REQ_WHITELIST_URLS.split(",");
            for (String pattern : whiteListUrls) {
                if (null != uri && matcher.match(pattern, uri)) {
                    return;
                }
            }
            SysReqLog sysReqLog = new SysReqLog();
            CoreUser customUser = UserContextHolder.getUser();
            if (null != customUser) {
                String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
                BeanUtils.copyProperties(customUser, sysReqLog);
                sysReqLog.setToken(authorization);
                logger.info("marketingsaveReqLog{}", JSON.toJSONString(customUser));
            }
            sysReqLog.setSource("store-saas-marketing");
            sysReqLog.setMethod(method);
            sysReqLog.setReqUrl(url);
            sysReqLog.setReqUri(uri);
            sysReqLog.setReqParams(getValue(JSON.toJSONString(reqObjects)));
            sysReqLog.setResParams(getValue(JSON.toJSONString(result)));
            sysReqLog.setTime(time + "");
            logger.info("marketingsaveReqLog{}", JSON.toJSONString(sysReqLog));
            sysReqLogService.saveReqLog(sysReqLog);
        } catch (Exception e) {
            logger.error("marketingsaveReqLog.error:", e);
        }
    }

    private String getValue(String val) {
        if (null != val && val.length() > 5000) {
            return val.substring(0, 5000);
        }
        return val;
    }
}