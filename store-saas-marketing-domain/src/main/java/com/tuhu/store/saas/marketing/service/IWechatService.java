package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.request.MiniProgramNotifyReq;

/**
 * 微信核心信息
 * Created by wangxiangyun on 2019/1/11.
 */
public interface IWechatService {
    /**
     * 获取小程序的openId
     *
     * @param appid      小程序的appid
     * @param code       小程序的认证返回码
     * @param merchantNo 小程序的商户号
     * @return
     */
    String getOpenIdFromTuhu(String appid, String code, String merchantNo, String openIdUrl);

    /**
     * 获取小程序的openId
     *
     * @param appid
     * @param appSecret
     * @param code
     * @param openIdUrl
     * @return
     */
    String getOpenId(String appid, String appSecret, String code, String openIdUrl);

    /**
     * 刷新小程序的access token,优先走缓存
     *
     * @param appid
     * @param appSecret
     * @param clinetType
     * @param accessTokenUrl
     * @return
     */
    String refreshAccessToken(String appid, String appSecret, String clinetType, String accessTokenUrl);

    /**
     * 刷新小程序的access token
     *
     * @param appid
     * @param appSecret
     * @param clinetType
     * @param accessTokenUrl
     * @return
     */
    String forceRefreshAccessToken(String appid, String appSecret, String clinetType, String accessTokenUrl);

    /**
     * 发送小程序模板消息通知
     * @param openId
     * @param miniProgramNotifyReq
     * @return
     */
    Object miniProgramNotify(String openId, MiniProgramNotifyReq miniProgramNotifyReq);
}
