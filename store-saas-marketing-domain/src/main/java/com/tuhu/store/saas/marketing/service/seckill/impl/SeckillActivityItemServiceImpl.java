package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivityItem;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillActivityItemMapper;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 秒杀活动明细表 服务实现类
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@Service
public class SeckillActivityItemServiceImpl extends ServiceImpl<SeckillActivityItemMapper, SeckillActivityItem> implements SeckillActivityItemService {

    @Override
    public List<SeckillActivityItem> queryItemsByActivityId(String activityId, Long storeId, Long tenantId) {
        return this.baseMapper.selectList(new EntityWrapper<SeckillActivityItem>().eq("seckill_activity_id",activityId)
                .eq("store_id",storeId).eq("tenant_id",tenantId).eq("is_delete",0).orderBy("goods_type"));
    }


}
