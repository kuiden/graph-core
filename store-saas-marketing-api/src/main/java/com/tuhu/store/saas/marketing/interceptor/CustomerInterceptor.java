package com.tuhu.store.saas.marketing.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.google.common.net.HttpHeaders;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.context.CustomerContextHolder;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.remote.CustomUser;
import com.tuhu.store.saas.marketing.remote.CustomerAuthDto;
import com.tuhu.store.saas.marketing.remote.admin.StoreAdminClient;
import com.tuhu.store.saas.marketing.remote.auth.AuthClient;
import com.tuhu.store.saas.marketing.remote.reponse.UserDTO;
import com.tuhu.store.saas.marketing.remote.storeuser.StoreUserClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User:
 * Date: 2019/10/12
 * Time: 11:40
 * Description:
 */
@Slf4j
@Component
/**
 * 获取C端用户信息拦截逻辑
 */
public class CustomerInterceptor implements HandlerInterceptor {
    @Autowired
    private AuthClient authClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        CustomerAuthDto customUser = null;
        if (StringUtils.isBlank(authorization)) {
            BizBaseResponse bizBaseResponse = new BizBaseResponse(BizErrorCodeEnum.PARAM_ERROR, "请传递Authorization信息");
            this.writeResponse(response, bizBaseResponse);
            return false;
        } else {
            try {
                BizBaseResponse<CustomerAuthDto> customUserResult = authClient.getUserByToken();
                log.info("==storeAdminClient.getUserByToken=={}", JSONObject.toJSONString(customUserResult));
                if (null != customUserResult && customUserResult.isSuccess() && null != customUserResult.getData()) {
                    customUser = customUserResult.getData();

                }
            } catch (Exception e) {
                log.error("获取登录用户信息异常", e);
            }
        }
        if (null != customUser) {
            CustomerContextHolder.setUser(customUser);
            return true;
        }
        BizBaseResponse bizBaseResponse = new BizBaseResponse(BizErrorCodeEnum.INVALID_ACCESS_TOKEN, "您的身份已过期，请重新登录");
        this.writeResponse(response, bizBaseResponse);
        return false;
    }

    private void writeResponse(HttpServletResponse response, BizBaseResponse bizBaseResponse) {
        try {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.getWriter().write(JSONObject.toJSONString(bizBaseResponse));
        } catch (IOException e) {
            log.error("response error", e);
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContextHolder.remove();
    }
}
