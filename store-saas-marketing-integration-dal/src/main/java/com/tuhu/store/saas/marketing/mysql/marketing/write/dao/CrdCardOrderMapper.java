package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.CrdCardOrder;
import com.tuhu.store.saas.marketing.dataobject.CrdCardOrderExample;

import java.util.Date;
import java.util.List;

import com.tuhu.store.saas.marketing.dataobject.CustomerCardOrder;
import org.apache.ibatis.annotations.Param;

public interface CrdCardOrderMapper {
    long countByExample(CrdCardOrderExample example);

    int deleteByExample(CrdCardOrderExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CrdCardOrder record);

    int insertSelective(CrdCardOrder record);

    List<CrdCardOrder> selectByExample(CrdCardOrderExample example);

    CrdCardOrder selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CrdCardOrder record, @Param("example") CrdCardOrderExample example);

    int updateByExample(@Param("record") CrdCardOrder record, @Param("example") CrdCardOrderExample example);

    int updateByPrimaryKeySelective(CrdCardOrder record);

    int updateByPrimaryKey(CrdCardOrder record);

    List<CustomerCardOrder> getCustomersForCusGroup(@Param("storeId") Long storeId, @Param("beginTime") Date beginTime);


}