package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.tuhu.store.saas.marketing.po.ActivityItem;
import com.tuhu.store.saas.marketing.po.ActivityItemExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ActivityItemMapper extends BaseMapper<ActivityItem> {
    int countByExample(ActivityItemExample example);

    int deleteByExample(ActivityItemExample example);

    int deleteByPrimaryKey(Long id);

    Integer insert(ActivityItem record);

    int insertSelective(ActivityItem record);

    List<ActivityItem> selectByExample(ActivityItemExample example);

    ActivityItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ActivityItem record, @Param("example") ActivityItemExample example);

    int updateByExample(@Param("record") ActivityItem record, @Param("example") ActivityItemExample example);

    int updateByPrimaryKeySelective(ActivityItem record);

    int updateByPrimaryKey(ActivityItem record);

    int insertBatch(List<ActivityItem> activityItemList);
}
