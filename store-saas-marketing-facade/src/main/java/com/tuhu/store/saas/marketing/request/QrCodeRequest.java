package com.tuhu.store.saas.marketing.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/7
 * Time: 9:16
 * Description:
 */
@Data
public class QrCodeRequest implements Serializable {
    private Long storeId;
    private String couponCode;
    /**
     * 参数"storeId=222"
     */
    private String scene;
    /**
     * 小程序页面路径
     */
    private String path;
    /**
     * 二维码宽度
     */
    private Long width;
}