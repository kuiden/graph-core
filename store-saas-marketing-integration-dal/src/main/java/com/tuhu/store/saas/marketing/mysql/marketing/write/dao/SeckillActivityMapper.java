package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.base.mapper.BaseWriteMapper;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 秒杀活动表 Mapper 接口
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
public interface SeckillActivityMapper extends BaseWriteMapper<SeckillActivity> {

    int autoUpdateOffShelf();

    List<SeckillActivity> pageList(@Param("tenantId") Long tenantId, @Param("storeId") Long storeId, @Param("activityTitle") String activityTitle, @Param("status") Integer status);

    //查询未开始的活动，按照开始时间排序
    List<SeckillActivity> queryNotStart(@Param("tenantId") Long tenantId, @Param("storeId") Long storeId);

    //查询进行中的活动，按照结束时间排序
    List<SeckillActivity> queryStart(@Param("tenantId") Long tenantId, @Param("storeId") Long storeId);

}
