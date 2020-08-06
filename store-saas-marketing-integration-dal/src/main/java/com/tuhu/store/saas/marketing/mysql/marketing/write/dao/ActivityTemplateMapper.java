package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.tuhu.store.saas.marketing.po.ActivityTemplate;
import com.tuhu.store.saas.marketing.po.ActivityTemplateExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityTemplateMapper extends BaseMapper<ActivityTemplate> {
    int countByExample(ActivityTemplateExample example);

    int deleteByExample(ActivityTemplateExample example);

    int deleteByPrimaryKey(Long id);

    Integer insertEntity(ActivityTemplate record);

    int insertSelective(ActivityTemplate record);

    List<ActivityTemplate> selectByExample(ActivityTemplateExample example);

    ActivityTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ActivityTemplate record, @Param("example") ActivityTemplateExample example);

    int updateByExample(@Param("record") ActivityTemplate record, @Param("example") ActivityTemplateExample example);

    int updateByPrimaryKeySelective(ActivityTemplate record);

    int updateByPrimaryKey(ActivityTemplate record);

    int referById(@Param("id") Long id);
}
