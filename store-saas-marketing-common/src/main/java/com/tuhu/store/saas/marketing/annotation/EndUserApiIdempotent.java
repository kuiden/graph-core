package com.tuhu.store.saas.marketing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wangxiang2
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EndUserApiIdempotent {

    /**
     * 设置请求锁定时间
     *
     * @return
     */
    int lockTime() default 2;
}
