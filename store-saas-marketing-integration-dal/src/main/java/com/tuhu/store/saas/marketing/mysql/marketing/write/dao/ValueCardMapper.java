package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.ValueCard;
import com.tuhu.store.saas.marketing.dataobject.ValueCardExample;
import java.util.List;

import com.tuhu.store.saas.marketing.response.CustomerIdMarketInfo;
import org.apache.ibatis.annotations.Param;

public interface ValueCardMapper {
    long countByExample(ValueCardExample example);

    int deleteByExample(ValueCardExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ValueCard record);

    int insertSelective(ValueCard record);

    List<ValueCard> selectByExample(ValueCardExample example);

    ValueCard selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ValueCard record, @Param("example") ValueCardExample example);

    int updateByExample(@Param("record") ValueCard record, @Param("example") ValueCardExample example);

    int updateByPrimaryKeySelective(ValueCard record);

    int updateByPrimaryKey(ValueCard record);

    int addValueCardBatch(@Param("valueCards") List<ValueCard> valueCards);

    int editValueCardBatch(@Param("valueCards") List<ValueCard> valueCards);

    List<CustomerIdMarketInfo> countByCustomerIds(@Param("customerIds")List<String> customerIds);
}