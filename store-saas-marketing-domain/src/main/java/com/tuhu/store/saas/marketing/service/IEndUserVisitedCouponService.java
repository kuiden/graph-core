package com.tuhu.store.saas.marketing.service;

import com.tuhu.base.service.IBaseQueryService;
import com.tuhu.base.service.IBaseService;
import com.tuhu.base.service.ICrudService;
import com.tuhu.store.saas.marketing.entity.EndUserVisitedCouponEntity;
import com.tuhu.store.saas.marketing.request.EndUserVistiedCouponRequest;

/**
 * <p>
 * 车主端用户访问的优惠券记录 服务类
 * </p>
 *
 * @author someone
 * @since 2020-08-03
 */
public interface IEndUserVisitedCouponService extends IBaseQueryService<EndUserVisitedCouponEntity>, IBaseService<EndUserVisitedCouponEntity>, ICrudService<EndUserVisitedCouponEntity> {


    /**
     * 记录C端用户访问过的优惠券
     *
     * @param endUserVistiedCouponRequest
     */
    void recordEndUserVistiedCoupon(EndUserVistiedCouponRequest endUserVistiedCouponRequest);

    /**
     * 根据openId和couponCode查询用户访问过的优惠券记录
     *
     * @param openId
     * @param couponCode
     * @return
     */
    EndUserVisitedCouponEntity findFirstByOpenIdAndCouponCode(String openId, String couponCode);

    /**
     * 新增用户访问过的优惠券记录
     *
     * @param endUserVisitedCouponEntity
     */
    Integer addNewEndUserVisitedCoupon(EndUserVisitedCouponEntity endUserVisitedCouponEntity);


    /**
     * 记录通过浏览优惠券新增的门口客户信息
     *
     * @param endUserVistiedCouponRequest
     * @param customerId
     */
    void recordNewCustomerByVistiedCoupon(EndUserVistiedCouponRequest endUserVistiedCouponRequest, String customerId);



    /**
     * 记录C端用户访问过的优惠券
     *
     * @param endUserVisitedCouponEntity
     */
    void recordEndUserVistiedCoupon(EndUserVisitedCouponEntity endUserVisitedCouponEntity);

}

