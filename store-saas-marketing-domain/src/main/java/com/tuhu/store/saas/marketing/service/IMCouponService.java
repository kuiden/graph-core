package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.response.CouponPageResp;
import com.tuhu.store.saas.marketing.response.CustomerCouponPageResp;

import javax.validation.groups.Default;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/14
 * Time: 9:49
 * Description:
 */
public interface IMCouponService extends Default {
    /**
     * 获取小程序二维码图片
     *
     * @param req
     * @return
     */
    String getQrCodeForCoupon(QrCodeRequest req);

    /**
     * 券整体效果
     *
     * @param req
     * @return
     */
    Map getOveralEffect(CouponRequest req);

    /**
     * 主动领券效果
     *
     * @param req
     * @return
     */
    Map getGettingEffect(CouponRequest req);

    CustomerCouponPageResp getCouponReceiveList(CouponReceiveRecordRequest req);

    Map sendCoupon(SendCouponRequest req);

    CouponPageResp getCouponList(CouponSearchRequest req, String customerId);

    Map getCouponDetailForClient(CouponRequest req, String customerId);

    Map getCouponDetail(CouponRequest req);

    Map getCoupon(CouponRequest req, String customerId);

    CustomerCouponPageResp getMyCouponList(CouponReceiveRecordRequest req, String customerId);
}