package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.ValueCardRule;
import com.tuhu.store.saas.marketing.dataobject.ValueCardRuleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ValueCardRuleMapper {
    long countByExample(ValueCardRuleExample example);

    int deleteByExample(ValueCardRuleExample example);

    int deleteByPrimaryKey(Long id);

    int deleteByStoreIdAndTenantId(@Param("storeId") Long storeId,@Param("tenantId") Long tenantId);

    int insert(ValueCardRule record);

    int insertSelective(ValueCardRule record);

    List<ValueCardRule> selectByExample(ValueCardRuleExample example);

    ValueCardRule selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ValueCardRule record, @Param("example") ValueCardRuleExample example);

    int updateByExample(@Param("record") ValueCardRule record, @Param("example") ValueCardRuleExample example);

    int updateByPrimaryKeySelective(ValueCardRule record);

    int updateByPrimaryKey(ValueCardRule record);
}