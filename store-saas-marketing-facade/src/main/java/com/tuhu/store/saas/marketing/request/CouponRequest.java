package com.tuhu.store.saas.marketing.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/14
 * Time: 11:35
 * Description:
 */
@Data
public class CouponRequest extends BaseReq implements Serializable {
    private static final long serialVersionUID = -3882881413700823622L;
    private Long couponId;

    private String couponCode;

    /**
     * 加密后的优惠券编码
     */
    private String encryptedCode;

    /**
    领取类型：0：主动在线领取 1：手动发券 2：营销发券
     */
    private Integer receiveType ;


    private  String  customerCouponCode;




}