package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.request.MiniProgramNotifyReq;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/6/5
 * Time: 10:49
 * Description:小程序相关功能
 */
public interface MiniAppService {
   String getQrCodeUrl(String scene, String path, Long width);


   byte[] getQrCodeByte(String scene, String path, Long width);


   Object miniProgramNotify(MiniProgramNotifyReq miniProgramNotifyReq);
}