package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.StoreCustomerGroupRelation;
import com.tuhu.store.saas.marketing.dataobject.StoreCustomerGroupRelationExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StoreCustomerGroupRelationMapper {
    int countByExample(StoreCustomerGroupRelationExample example);

    int deleteByExample(StoreCustomerGroupRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(StoreCustomerGroupRelation record);

    List<StoreCustomerGroupRelation> selectByExample(StoreCustomerGroupRelationExample example);

    StoreCustomerGroupRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StoreCustomerGroupRelation record, @Param("example") StoreCustomerGroupRelationExample example);

    int updateByPrimaryKeySelective(StoreCustomerGroupRelation record);

}