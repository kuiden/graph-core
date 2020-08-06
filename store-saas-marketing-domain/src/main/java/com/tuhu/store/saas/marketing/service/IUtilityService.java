package com.tuhu.store.saas.marketing.service;

/**
 * Utility服务接口
 */
public interface IUtilityService {

    /**
     * 根据长url获取短url
     * 每次调用获取结果都是新的
     * longUrl是完整的url路径
     * @param longUrl
     * @return
     */
    public String getShortUrl(String longUrl);

}
