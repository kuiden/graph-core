package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;


import com.tuhu.store.saas.marketing.dataobject.CustomerCouponExample;
import com.tuhu.store.saas.marketing.dataobject.CustomerCoupon;
import com.tuhu.store.saas.marketing.po.CustomerCouponPO;
import com.tuhu.store.saas.marketing.request.CustomerCouponSearch;
import com.tuhu.store.saas.marketing.response.ComputeMarktingCustomerForReportResp;
import com.tuhu.store.saas.marketing.response.CustomerCouponDetailResponse;
import com.tuhu.store.saas.marketing.response.CustomerIdMarketInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CustomerCouponMapper {
    int countByExample(CustomerCouponExample example);

    int deleteByExample(CustomerCouponExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CustomerCoupon record);

    int insertSelective(CustomerCoupon record);

    int insertBySendCoupon(CustomerCoupon record);

    List<CustomerCoupon> selectByExample(CustomerCouponExample example);

    CustomerCoupon selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CustomerCoupon record, @Param("example") CustomerCouponExample example);

    int updateByExample(@Param("record") CustomerCoupon record, @Param("example") CustomerCouponExample example);

    int updateByPrimaryKeySelective(CustomerCoupon record);

    int updateByPrimaryKey(CustomerCoupon record);

    List<CustomerCouponPO> selectRecordList(CustomerCouponSearch record);

    List<CustomerCouponPO> selectMyRecordList(CustomerCouponSearch record);

    Map queryCountForOverViewData(CustomerCoupon record);

    List<Map<String, Object>> countGrantNumberByCouponCodeList(@Param("couponCodeList") List<String> couponCodeList, @Param("useStatus") Integer useStatus);

    int insertBatch(@Param("list") List<CustomerCoupon> list);

    List<Map<String, Object>> getRecievedCountByCouponCode(@Param("couponCodeList") List<String> couponCodeList, @Param("customerId") String customerId);

    List<CustomerCoupon> selectByServiceOrderId(@Param("serviceOrderId") String serviceOrderId);

    int updateoccupyNumByCode(@Param("occupyNum") Long occupyNum, @Param("code") String code);

    List<ComputeMarktingCustomerForReportResp> ComputeMarktingCustomerForReportByCoupon(@Param("storeId") Long storeId, @Param("tenantId") Long tenantId);

    List<ComputeMarktingCustomerForReportResp> ComputeMarktingCustomerForReportByActivity(@Param("storeId") Long storeId, @Param("tenantId") Long tenantId);

    List<ComputeMarktingCustomerForReportResp> ComputeMarktingCustomerForReportByCard(@Param("storeId") Long storeId, @Param("tenantId") Long tenantId);


    List<CustomerCoupon> selectByCustomerId(@Param("customerId") String customerId);

    int countCustomerCoupon(@Param("customerId") String customerId);

    CustomerCouponDetailResponse queryCustomerCouponDetailById(@Param("id")Long id);

    List<CustomerIdMarketInfo> countByCustomerIds(@Param("customerIds")List<String> customerIds);
}
