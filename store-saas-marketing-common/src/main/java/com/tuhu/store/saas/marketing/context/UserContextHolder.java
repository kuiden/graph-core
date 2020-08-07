package com.tuhu.store.saas.marketing.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.tuhu.store.saas.marketing.remote.CoreUser;

/**
 * 当前登录用户持有者
 */
public class UserContextHolder {
    private static final ThreadLocal<CoreUser> userLocal = new TransmittableThreadLocal<>();

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    public static CoreUser getUser() {
        return userLocal.get();
    }

    /**
     * 获取当前登录用户名称
     *
     * @return
     */
    public static String getUserName() {
        return userLocal.get() != null ? userLocal.get().getUsername() : null;
    }

    /**
     * 获取当前登录用户名称
     *
     * @return
     */
    public static String getName() {
        return userLocal.get() != null ? userLocal.get().getUsername() : null;
    }

    /**
     * 设置当前登录用户信息
     *
     * @param user
     */
    public static void setUser(CoreUser user) {
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
        CoreUser coreUser = UserContextHolder.getUser();
        if (null != coreUser) {
            return coreUser.getTenantId();
        }
        return null;
    }

    /**
     * 获取公司ID
     *
     * @return
     */
    public static Long getCompanyId() {
        CoreUser coreUser = UserContextHolder.getUser();
        if (null != coreUser) {
            return coreUser.getCompanyId();
        }
        return null;
    }


    /**
     * 获取门店ID
     *
     * @return
     */
    public static Long getStoreId() {
        CoreUser coreUser = UserContextHolder.getUser();
        if (null != coreUser) {
            return coreUser.getStoreId();
        }
        return null;
    }

    /**
     * 获取门店用户ID
     *
     * @return
     */
    public static String getStoreUserId() {
        CoreUser coreUser = UserContextHolder.getUser();
        if (null != coreUser) {
            return coreUser.getStoreUserId();
        }
        return null;
    }


    public static Long getUserId() {
        CoreUser coreUser = UserContextHolder.getUser();
        if (null != coreUser) {
            return coreUser.getAccountId();
        }
        return null;
    }

    /**
     * 获取顶级公司ID
     *
     * @return
     */
    public static Long getRootCompanyId() {
        CoreUser coreUser = UserContextHolder.getUser();
        if (null != coreUser) {
            return coreUser.getCompanyId();
        }
        return null;
    }
}
