package com.tuhu.store.saas.marketing.service.seckill;

import com.baomidou.mybatisplus.service.IService;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivityItem;

import java.util.List;

/**
 * <p>
 * 秒杀活动明细表 服务类
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
public interface SeckillActivityItemService extends IService<SeckillActivityItem> {

    List<SeckillActivityItem> queryItemsByActivityId(String activityId, Long storeId, Long tenantId);

}
