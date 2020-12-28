package com.tuhu.store.saas.marketing.config.log;

import com.alibaba.fastjson.JSON;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.springcloud.common.constant.ApiCommonConstant;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.SysReqLog;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.remote.CoreUser;
import com.tuhu.store.saas.marketing.remote.EndUser;
import com.tuhu.store.saas.marketing.sys.SysReqLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * aop 记录请求和返回数据日志
 * @author yangshengyong
 * @since 2020-11-18
 */
@Slf4j
@Aspect
@Component
@Order(ApiCommonConstant.ORDERED_CUSTOM_HIGHEST - 10)
public class LogRecordAspect {
    private final static AntPathMatcher matcher = new AntPathMatcher();
    private final static String REQUEST_ID_KEY = "requestId";
    @Value("${sys.req.whitelist.urls:/feign1/**}")
    private String SYS_REQ_WHITELIST_URLS;
    @Value("${sys.req.save.switch:true}")
    private Boolean SAVE_SWITCH;
    @Value("${sys.req.log.length:5000}")
    private Integer SYS_REQ_LOG_LENGTH;
    @Autowired
    private SysReqLogService sysReqLogService;

    @Pointcut("execution(public * com.tuhu.store.saas.marketing.controller..*.*(..))")
    public void excudeService() {
    }

    @Around("excudeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        String requestId = UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
        MDC.put(REQUEST_ID_KEY, requestId);
        log.info("startlogRequestId=" + requestId);
        long startTime = new Date().getTime();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String method = request.getMethod();
        List<Object> objects = new ArrayList<>();
        try {
            Object[] args = pjp.getArgs();
            for (int i = 0; i < args.length; i++) {
                objects.add(args[i]);
            }
        } catch (Exception e) {
            log.error(method + "doAroundError", e);
        }
        Object result = null;
        try {
            result = pjp.proceed();
        } catch (BizException e){
            log.error(request.getRequestURI(),e);
            BizBaseResponse response = new BizBaseResponse();
            response.setCode(4000);
            response.setMessage(e.getErrorMessage());
            result = response;
        } catch (MarketingException e){
            log.error(request.getRequestURI(), e);
            BizBaseResponse response = new BizBaseResponse();
            response.setCode(5000);
            response.setMessage(e.getMessage());
            result = response;
        } catch (Exception e){
            log.error(request.getRequestURI(),e);
            BizBaseResponse response = new BizBaseResponse();
            response.setCode(5000);
            response.setMessage(e.getMessage());
            result = response;
        }
        long endTime = new Date().getTime();
        long time = endTime - startTime;
        if (SAVE_SWITCH) {
            saveReqLog(request, objects, result, time, requestId);
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
    private void saveReqLog(HttpServletRequest request, List<Object> reqObjects, Object result, long time, String requestId) {
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
            sysReqLog.setRequestId(requestId);
            sysReqLog.setSource("store-saas-marketing");
            //B端用户
            CoreUser customUser = UserContextHolder.getUser();
            if (null != customUser) {
                String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
                BeanUtils.copyProperties(customUser, sysReqLog);
                sysReqLog.setToken(authorization);
            }
            //C端用户
            EndUser endUser = EndUserContextHolder.getUser();
            if (null != endUser) {
                String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
                sysReqLog.setToken(null != authorization ? authorization : "");
                buildSysReqLog(sysReqLog, endUser);
            }
            sysReqLog.setMethod(method);
            sysReqLog.setReqUrl(url);
            sysReqLog.setReqUri(uri);
            try {
                sysReqLog.setReqParams(getValue(JSON.toJSONString(reqObjects)));
                sysReqLog.setResParams(getValue(JSON.toJSONString(result)));
            } catch (Exception e) {
                log.info("logjsontojsonstring", e);
            }
            sysReqLog.setTime(time + "");
            sysReqLogService.saveReqLog(sysReqLog);
            log.info("endlogRequestId=" + requestId);
        } catch (Exception e) {
            log.error("marketingsaveReqLog.error:", e);
        }
    }

    private void buildSysReqLog(SysReqLog sysReqLog, EndUser endUser) {
        sysReqLog.setSource("C端-store-saas-marketing");
        sysReqLog.setStoreId(null != endUser.getStoreId() ? Long.valueOf(endUser.getStoreId()) : 0L);
        sysReqLog.setTenantId(null != endUser.getTenantId() ? Long.valueOf(endUser.getTenantId()) : 0L);
        sysReqLog.setCompanyId(null != endUser.getCompanyId() ? Long.valueOf(endUser.getCompanyId()) : 0L);
        sysReqLog.setOpenId(null != endUser.getOpenId() ? endUser.getOpenId() : "");
        sysReqLog.setNickName(null != endUser.getClientType() ? endUser.getClientType() : "");
        sysReqLog.setAccount(null != endUser.getPhone() ? endUser.getPhone() : "");
        sysReqLog.setUsername(null != endUser.getName() ? endUser.getName() : "");
    }

    private String getValue(String val) {
        if (null != val && val.length() > SYS_REQ_LOG_LENGTH) {
            return val.substring(0, SYS_REQ_LOG_LENGTH);
        }
        return val;
    }
}