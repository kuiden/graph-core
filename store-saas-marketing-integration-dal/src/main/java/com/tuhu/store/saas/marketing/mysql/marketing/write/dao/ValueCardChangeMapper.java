package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.ValueCardChange;
import com.tuhu.store.saas.marketing.dataobject.ValueCardChangeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ValueCardChangeMapper {
    long countByExample(ValueCardChangeExample example);

    int deleteByExample(ValueCardChangeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ValueCardChange record);

    int insertSelective(ValueCardChange record);

    List<ValueCardChange> selectByExample(ValueCardChangeExample example);

    ValueCardChange selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ValueCardChange record, @Param("example") ValueCardChangeExample example);

    int updateByExample(@Param("record") ValueCardChange record, @Param("example") ValueCardChangeExample example);

    int updateByPrimaryKeySelective(ValueCardChange record);

    int updateByPrimaryKey(ValueCardChange record);
}