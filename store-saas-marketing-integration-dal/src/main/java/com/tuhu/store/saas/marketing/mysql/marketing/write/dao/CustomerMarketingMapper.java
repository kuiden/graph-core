package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketingExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerMarketingMapper {

    int countByExample(CustomerMarketingExample example);

    int deleteByExample(CustomerMarketingExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CustomerMarketing record);

    int insertSelective(CustomerMarketing record);

    List<CustomerMarketing> selectByExampleWithBLOBs(CustomerMarketingExample example);

    List<CustomerMarketing> selectByExample(CustomerMarketingExample example);

    CustomerMarketing selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CustomerMarketing record,
                                 @Param("example") CustomerMarketingExample example);

    int updateByExampleWithBLOBs(@Param("record") CustomerMarketing record,
                                 @Param("example") CustomerMarketingExample example);

    int updateByExample(@Param("record") CustomerMarketing record, @Param("example") CustomerMarketingExample example);

    int updateByPrimaryKeySelective(CustomerMarketing record);

    int updateByPrimaryKeyWithBLOBs(CustomerMarketing record);

    int updateByPrimaryKey(CustomerMarketing record);
}
