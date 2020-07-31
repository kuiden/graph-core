package com.tuhu.store.saas.marketing.service;

import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.dataobject.Coupon;
import com.tuhu.store.saas.marketing.dataobject.CouponScopeCategory;
import com.tuhu.store.saas.marketing.dataobject.Customer;
import com.tuhu.store.saas.marketing.dataobject.CustomerCoupon;
import com.tuhu.store.saas.marketing.request.vo.ServiceOrderCouponUseVO;
import com.tuhu.store.saas.marketing.request.vo.ServiceOrderCouponVO;
import com.tuhu.store.saas.marketing.response.CommonResp;
import com.tuhu.store.saas.marketing.response.CouponStatisticsForCustomerMarketResp;
import com.tuhu.store.saas.marketing.response.dto.CustomerCouponDTO;
import com.tuhu.store.saas.marketing.response.dto.ServiceOrderCouponDTO;
import com.tuhu.store.saas.marketing.request.AddCouponReq;
import com.tuhu.store.saas.marketing.request.CouponListReq;
import com.tuhu.store.saas.marketing.request.EditCouponReq;
import com.tuhu.store.saas.marketing.request.SendCouponReq;
import com.tuhu.store.saas.marketing.response.CouponResp;

import java.util.List;

/**
 * 优惠券相关接口
 */
public interface ICouponService {
    /**
     * 新建优惠券活动
     *
     * @param addCouponReq
     * @return
     */
    AddCouponReq addNewCoupon(AddCouponReq addCouponReq);

    /**
     * 根据优惠券id，获取优惠券活动详情
     *
     * @param couponId
     * @param storeId
     * @return
     */
    CouponResp getCouponDetailById(Long couponId, Long storeId);

    /**
     * 根据优惠券编码，获取优惠券活动详情
     *
     * @param couponCode
     * @param storeId
     * @return
     */
    CouponResp getCouponDetailByCode(String couponCode, Long storeId);

    /**
     * 查询优惠券活动
     *
     * @param couponListReq
     * @return
     */
    PageInfo<CouponResp> listCoupon(CouponListReq couponListReq);

    /**
     * 优惠券活动编辑
     *
     * @param editCouponReq
     * @return
     */
    EditCouponReq editCoupon(EditCouponReq editCouponReq);

    /**
     * 指定优惠券及客户送券
     *
     * @param sendCouponReq
     * @return
     */
    List<CommonResp<CustomerCoupon>> sendCoupon(SendCouponReq sendCouponReq);

    /**
     * 通用生成客户优惠券-不保存数据库
     *
     * @param coupon
     * @param customer
     * @param sendCouponReq
     * @return
     */
    CommonResp<CustomerCoupon> generateCustomerCoupon(Coupon coupon, Customer customer, SendCouponReq sendCouponReq);

    /**
     * 查询指定用户所有未使用的优惠券信息
     *
     * @param customerId
     * @return
     */
    List<CustomerCoupon> findAllUnusedCustomerCouponListByCustomerId(String customerId);

    /**
     * 根据优惠券编码列表批量查询优惠券信息
     *
     * @param couponCodeList
     * @param storeId
     * @return
     */
    List<Coupon> findCouponsByCouponCodeList(List<String> couponCodeList, String storeId);

    /**
     * 根据优惠券编码，查询优惠券的限定分类
     *
     * @param couponCode
     * @return
     */
    List<CouponScopeCategory> findCouponScopeCategoryListByCouponCode(String couponCode);

    /**
     * 各级工单信息查询指定客户可用的优惠券
     *
     * @param serviceOrderCouponVO
     * @return
     */
    ServiceOrderCouponDTO getCouponsForServiceOrder(ServiceOrderCouponVO serviceOrderCouponVO);

    /**
     * 根据工单核销客户优惠券
     *
     * @param serviceOrderCouponUseVO
     * @return
     */
    ServiceOrderCouponDTO writeOffCustomerCouponForServiceOrder(ServiceOrderCouponUseVO serviceOrderCouponUseVO);

    /**
     * 核销优惠券
     *
     * @param customerCoupon
     * @param serviceOrderCouponUseVO
     */
    void writeOffCustomerCoupon(CustomerCoupon customerCoupon, ServiceOrderCouponUseVO serviceOrderCouponUseVO);

    /**
     * 根据工单取消核销客户优惠券
     *
     * @param serviceOrderCouponUseVO
     * @return
     */
    ServiceOrderCouponDTO cancelWriteOffCustomerCouponForServiceOrder(ServiceOrderCouponUseVO serviceOrderCouponUseVO);

    /**
     * 取消核销优惠券
     *
     * @param customerCoupon
     * @param serviceOrderCouponUseVO
     */
    void cancelWriteOffCustomerCoupon(CustomerCoupon customerCoupon, ServiceOrderCouponUseVO serviceOrderCouponUseVO);

    /**
     * 根据客户优惠券ID查询优惠券详情
     *
     * @param customerCouponId
     * @param storeId
     * @return
     */
    CustomerCouponDTO getCouponDetailByCustomerCouponId(String customerCouponId, String storeId);

    /**
     * 根据工单ID查询使用的客户优惠券
     *
     * @param orderId
     * @return
     */
    CustomerCoupon getCustomerCouponByServiceOrderId(String orderId);

    /**
     * 根据客户ID集合及优惠券编码获取用券数据统计
     *
     * @param couponCode
     * @param customerIds
     * @return
     */
    CouponStatisticsForCustomerMarketResp getCouponStatisticsForCustomerMarket(String couponCode, List<String> customerIds);
}
