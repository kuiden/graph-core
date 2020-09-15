package com.tuhu.store.saas.marketing.service;

import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.marketing.dataobject.Coupon;
import com.tuhu.store.saas.marketing.dataobject.CouponScopeCategory;
import com.tuhu.store.saas.marketing.dataobject.Customer;
import com.tuhu.store.saas.marketing.dataobject.CustomerCoupon;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.request.vo.ServiceOrderCouponUseVO;
import com.tuhu.store.saas.marketing.request.vo.ServiceOrderCouponVO;
import com.tuhu.store.saas.marketing.response.*;
import com.tuhu.store.saas.marketing.response.dto.CustomerCouponDTO;
import com.tuhu.store.saas.marketing.response.dto.ServiceOrderCouponDTO;
import org.springframework.transaction.annotation.Transactional;

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
     * @return
     */
    CouponResp getCouponDetailById(Long couponId);

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

    @Transactional
    void setOccupyNum(Coupon x, int num);

    /**
     * 通用生成客户优惠券-不保存数据库
     *
     * @param coupon
     * @param customer
     * @param sendCouponReq
     * @return
     */
    CommonResp<CustomerCoupon> generateCustomerCoupon(Coupon coupon, Customer customer, SendCouponReq sendCouponReq);

    CommonResp<CustomerCoupon> generateCustomerCoupon(Coupon coupon, CustomerDTO customer, SendCouponReq sendCouponReq);


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
     * 核销客户优惠券
     * @param code
     * @return
     */
    @Transactional
    String  writeOffCustomerCouponV2(String code);

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

    /**
     * 根据优惠券id获取优惠券的可用额度
     * @param id
     * @param storeId
     * @return
     */
    Long getCouponAvailableAccount(Long id, Long storeId);

    /**
     * 分页查询用户领券信息
     * @param couponRequest
     * @return
     */
    PageInfo<CustomerCouponResponse> getCustomerCouponList(CustomerCouponRequest couponRequest);

    /**
     * H5获取客户消费券详情
     * @param id
     * @return
     */
    CustomerCouponDetailResponse getCustomerCouponDetail(Long id);


}
