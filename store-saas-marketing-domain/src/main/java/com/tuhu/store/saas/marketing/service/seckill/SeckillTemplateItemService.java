package com.tuhu.store.saas.marketing.service.seckill;

import com.baomidou.mybatisplus.service.IService;
import com.tuhu.store.saas.marketing.dataobject.SeckillTemplateItem;
import com.tuhu.store.saas.marketing.request.seckill.EditSeckillTempItemReq;

import java.util.List;

/**
 * <p>
 * 秒杀活动基础模板表 服务类
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
public interface SeckillTemplateItemService extends IService<SeckillTemplateItem> {

    boolean updateSeckillTempItem(List<EditSeckillTempItemReq> tempItems, String tempId, Long tenantId, String userId);

}
