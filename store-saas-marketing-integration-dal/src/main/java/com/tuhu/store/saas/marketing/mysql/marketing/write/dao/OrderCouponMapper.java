package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.OrderCoupon;
import com.tuhu.store.saas.marketing.dataobject.OrderCouponExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface OrderCouponMapper {
    int countByExample(OrderCouponExample example);

    int deleteByExample(OrderCouponExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OrderCoupon record);

    int insertSelective(OrderCoupon record);

    List<OrderCoupon> selectByExample(OrderCouponExample example);

    OrderCoupon selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OrderCoupon record, @Param("example") OrderCouponExample example);

    int updateByExample(@Param("record") OrderCoupon record, @Param("example") OrderCouponExample example);

    int updateByPrimaryKeySelective(OrderCoupon record);

    int updateByPrimaryKey(OrderCoupon record);
}