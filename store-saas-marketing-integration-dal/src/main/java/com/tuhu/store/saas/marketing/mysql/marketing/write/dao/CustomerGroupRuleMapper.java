package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerGroupRuleMapper {
    int countByExample(CustomerGroupRuleExample example);

    int deleteByExample(CustomerGroupRuleExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(CustomerGroupRule record);

    List<CustomerGroupRule> selectByExample(CustomerGroupRuleExample example);

    CustomerGroupRule selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CustomerGroupRule record, @Param("example") CustomerGroupRuleExample example);

    int updateByPrimaryKeySelective(CustomerGroupRule record);

    int insertBatch(@Param("list") List<CustomerGroupRule> list);

}