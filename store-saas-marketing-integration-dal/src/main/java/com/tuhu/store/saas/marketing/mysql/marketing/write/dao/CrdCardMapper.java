package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.CrdCard;
import com.tuhu.store.saas.marketing.dataobject.CrdCardExample;
import java.util.List;

import com.tuhu.store.saas.marketing.response.CustomerIdMarketInfo;
import org.apache.ibatis.annotations.Param;

public interface CrdCardMapper {
    long countByExample(CrdCardExample example);

    int deleteByExample(CrdCardExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CrdCard record);

    int insertSelective(CrdCard record);

    List<CrdCard> selectByExample(CrdCardExample example);

    CrdCard selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CrdCard record, @Param("example") CrdCardExample example);

    int updateByExample(@Param("record") CrdCard record, @Param("example") CrdCardExample example);

    int updateByPrimaryKeySelective(CrdCard record);

    int updateByPrimaryKey(CrdCard record);

    int countByCustomerId(@Param("customerId")String customerId);

    List<CustomerIdMarketInfo> countByCustomerIds(@Param("customerIds")List<String> customerIds);

    List<CrdCard> cardsByCustomerIds(@Param("customerIds")List<String> customerIds);
}