package com.tuhu.store.saas.marketing.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.tuhu.store.saas.marketing.remote.CustomUser;
import com.tuhu.store.saas.marketing.remote.CustomerAuthDto;

/**
 * 当前登录用户持有者
 */
public class CustomerContextHolder {
    private static final ThreadLocal<CustomerAuthDto> userLocal = new TransmittableThreadLocal<>();

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    public static CustomerAuthDto getUser() {
        return userLocal.get();
    }

    /**
     * 设置当前登录用户信息
     *
     * @param user
     */
    public static void setUser(CustomerAuthDto user) {
        userLocal.set(user);
    }

}
