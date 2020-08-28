package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.CrdCardConsumption;
import com.tuhu.store.saas.marketing.dataobject.CrdCardConsumptionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CrdCardConsumptionMapper {
    long countByExample(CrdCardConsumptionExample example);

    int deleteByExample(CrdCardConsumptionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CrdCardConsumption record);

    int insertSelective(CrdCardConsumption record);

    List<CrdCardConsumption> selectByExample(CrdCardConsumptionExample example);

    CrdCardConsumption selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CrdCardConsumption record, @Param("example") CrdCardConsumptionExample example);

    int updateByExample(@Param("record") CrdCardConsumption record, @Param("example") CrdCardConsumptionExample example);

    int updateByPrimaryKeySelective(CrdCardConsumption record);

    int updateByPrimaryKey(CrdCardConsumption record);
}