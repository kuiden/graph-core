package com.tuhu.store.saas.marketing.constant;

/**
 * Created by wangxiangyun on 2018/10/11.
 */
public class AuthConstant {
    public static final Integer succesCode = 1000;
    public static final Integer serviceCode = 4000;
    public static final Integer exceptionCode = 5000;
    public static final Integer validCode = 6000;
    public static final String saltKey = "tuhu";
    public static final String orginPassword = "123456";
    public static final String SYSTEM_TYPE = "systemType";
    /**
     * 登陆用户的短信key
     */
    public static final String loginRedisUserHeader = "USER:SMS:CODE:";
    /**
     * 重置用户的短信key
     */
    public static final String resetRedisUserHeader = "USER:SMS:RESET:CODE:";

    // 云通信短信
    public static final String authCodeTemplate = "355704";
    public static final String authCodeUseLife = "5分钟";

    /**
     * 微信登陆用户的信息key
     */
    public static final String loginWechatUserHeader = "USER:WECHAT:CODE:";

    /**
     * 微信Access Token信息key
     */
    public static final String wechatAccessTokenHeader = "USER:WECHAT:ACCESS:TOKEN:";

    /**
     * 微信Access Token默认过期时间，单位秒
     */
    public static final int wechatAccessTokenExpiresIn = 7200;
}
