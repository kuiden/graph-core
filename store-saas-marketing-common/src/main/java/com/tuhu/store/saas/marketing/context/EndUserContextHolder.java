package com.tuhu.store.saas.marketing.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.tuhu.store.saas.marketing.remote.EndUser;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;


public class EndUserContextHolder {

    private static final ThreadLocal<EndUser> userLocal = new TransmittableThreadLocal<>();

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    public static EndUser getUser() {
        return userLocal.get();
    }

    /**
     * 设置当前登录用户信息
     *
     * @param user
     */
    public static void setUser(EndUser user) {
        userLocal.set(user);
    }

    /**
     * 情况当前登录用户信息
     */
    public static void remove() {
        userLocal.remove();
    }

    /**
     * 获取企业ID
     *
     * @return
     */
    public static Long getTenantId() {
        EndUser EndUser = EndUserContextHolder.getUser();
        if (Objects.nonNull(EndUser)&& Objects.nonNull(EndUser.getTenantId())) {
            return Long.valueOf(EndUser.getTenantId());
        }
        return null;
    }


    /**
     * 获取门店ID
     *
     * @return
     */
    public static Long getStoreId() {
        EndUser EndUser = EndUserContextHolder.getUser();
        if (Objects.nonNull(EndUser)&& Objects.nonNull(EndUser.getStoreId())) {
            return Long.valueOf(EndUser.getStoreId());
        }
        return null;
    }
    /**
     * 获取门店用户ID
     *
     * @return
     */
    public static String getCustomerId() {
        EndUser EndUser = EndUserContextHolder.getUser();
        if (Objects.nonNull(EndUser)&& Objects.nonNull(EndUser.getUserId())) {
            return EndUser.getUserId();
        }
        return null;
    }


    public static String getTelephone(){
        EndUser EndUser = EndUserContextHolder.getUser();
        if (Objects.nonNull(EndUser)&& StringUtils.isNotBlank(EndUser.getUserId())) {
            return EndUser.getPhone();
        }
        return null;
    }

    public static String getName(){
        EndUser EndUser = EndUserContextHolder.getUser();
        if (Objects.nonNull(EndUser)&& StringUtils.isNotBlank(EndUser.getName())) {
            return EndUser.getName();
        }
        return null;
    }

    public static String getOpenId(){
        EndUser EndUser = EndUserContextHolder.getUser();
        if (Objects.nonNull(EndUser)&& StringUtils.isNotBlank(EndUser.getOpenId())) {
            return EndUser.getOpenId();
        }
        return null;
    }

}
