package com.tuhu.store.saas.marketing.service;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/6/5
 * Time: 10:49
 * Description:小程序相关功能
 */
public interface MiniAppService {
   String getQrCodeUrl(String scene, String path, Long width);
}