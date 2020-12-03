package com.tuhu.store.saas.marketing.service.seckill;

import com.baomidou.mybatisplus.service.IService;
import com.tuhu.store.saas.marketing.dataobject.SeckillClassification;
import com.tuhu.store.saas.marketing.request.seckill.SeckillClassificationModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * <p>
 * 秒杀活动分类表 服务类
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
public interface SeckillClassificationService extends IService<SeckillClassification> {

    Integer save(SeckillClassificationModel req);

    ArrayList<SeckillClassificationModel> getList(Long tenantId);

    @Transactional
    Boolean del(Integer id,Long tenantId);

    @Transactional
    ArrayList<SeckillClassificationModel> swapPriority(Long tenantId, Integer fromId, Integer toId);
}
