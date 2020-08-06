package com.tuhu.store.saas.marketing.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.remote.CoreUser;
import com.tuhu.store.saas.marketing.remote.admin.StoreAdminClient;
import com.tuhu.store.saas.marketing.remote.reponse.UserDTO;
import com.tuhu.store.saas.marketing.remote.storeuser.StoreUserClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/10/12
 * Time: 11:40
 * Description:
 */
@Slf4j
@Component
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private StoreAdminClient storeAdminClient;

    @Autowired
    private StoreUserClient storeUserClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        CoreUser coreUser = null;
        if (StringUtils.isBlank(authorization)) {
            BizBaseResponse bizBaseResponse = new BizBaseResponse(BizErrorCodeEnum.PARAM_ERROR, "请传递Authorization信息");
            this.writeResponse(response, bizBaseResponse);
            return false;
        } else {
            try {
                BizBaseResponse<CoreUser> customUserResult = storeAdminClient.getUserByToken();
                log.info("==storeAdminClient.getUserByToken=={}", JSONObject.toJSONString(customUserResult));
                if (null != customUserResult && customUserResult.isSuccess() && null != customUserResult.getData()) {
                    coreUser = customUserResult.getData();
                    makeUpStoreUserInfo(coreUser);
                }
            } catch (Exception e) {
                log.error("获取登录用户信息异常", e);
            }
        }
        if (null != coreUser) {
            UserContextHolder.setUser(coreUser);
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

    /**
     * 补充门店用户信息
     *
     * @param coreUser
     */
    private void makeUpStoreUserInfo(CoreUser coreUser) {
        if (null == coreUser || null == coreUser.getAccountId()) {
            return;
        }
        try {
            BizBaseResponse<UserDTO> userDTOResponse = storeUserClient.getStoreUserInfoBySysUserId(coreUser.getAccountId());
            log.info("==makeUpStoreUserInfo=userDTOResponse:{}", JSONObject.toJSONString(userDTOResponse));
            if (null != userDTOResponse && userDTOResponse.isSuccess() && null != userDTOResponse.getData()) {
                BeanUtils.copyProperties(userDTOResponse.getData(), coreUser);
                coreUser.setStoreUserId(userDTOResponse.getData().getId());
            }
        } catch (Exception e) {
            log.error("根据组织机构用户ID获取门店用户信息异常", e);
        }
        if (StringUtils.isBlank(coreUser.getStoreUserId())) {
            coreUser.setStoreUserId(String.valueOf(coreUser.getId()));
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContextHolder.remove();
    }
}
