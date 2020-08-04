package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.po.ActivityCustomer;
import com.tuhu.store.saas.marketing.po.ActivityCustomerExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ActivityCustomerMapper {
    int countByExample(ActivityCustomerExample example);

    int deleteByExample(ActivityCustomerExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ActivityCustomer record);

    int insertSelective(ActivityCustomer record);

    List<ActivityCustomer> selectByExample(ActivityCustomerExample example);

    ActivityCustomer selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ActivityCustomer record, @Param("example") ActivityCustomerExample example);

    int updateByExample(@Param("record") ActivityCustomer record, @Param("example") ActivityCustomerExample example);

    int updateByPrimaryKeySelective(ActivityCustomer record);

    int updateByPrimaryKey(ActivityCustomer record);

    List<Map<String, Object>> countByActivityCodeAndUseStatus(@Param("activityCodeList") List<String> activityCodeList, @Param("useStatusList") List<Integer> useStatusList);
}
