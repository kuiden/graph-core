package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.tuhu.store.saas.marketing.po.ActivityTemplate;
import com.tuhu.store.saas.marketing.po.ActivityTemplateExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityTemplateMapper extends BaseMapper<ActivityTemplate> {

    ActivityTemplate selectByPrimaryKey(Long id);

    int deleteByPrimaryKey(Long id);

    int referById(@Param("id") Long id);
}
