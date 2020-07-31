package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;


import com.tuhu.store.saas.marketing.dataobject.CouponScopeCategoryExample;
import com.tuhu.store.saas.marketing.dataobject.CouponScopeCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface CouponScopeCategoryMapper {
    int countByExample(CouponScopeCategoryExample example);

    int deleteByExample(CouponScopeCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CouponScopeCategory record);

    int insertSelective(CouponScopeCategory record);

    List<CouponScopeCategory> selectByExample(CouponScopeCategoryExample example);

    CouponScopeCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CouponScopeCategory record, @Param("example") CouponScopeCategoryExample example);

    int updateByExample(@Param("record") CouponScopeCategory record, @Param("example") CouponScopeCategoryExample example);

    int updateByPrimaryKeySelective(CouponScopeCategory record);

    int updateByPrimaryKey(CouponScopeCategory record);

    int insertBatch(@Param("list") List<CouponScopeCategory> list);
}
