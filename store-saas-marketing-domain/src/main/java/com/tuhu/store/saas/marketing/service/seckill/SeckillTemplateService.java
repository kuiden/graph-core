package com.tuhu.store.saas.marketing.service.seckill;

import com.baomidou.mybatisplus.service.IService;
import com.tuhu.store.saas.marketing.dataobject.SeckillTemplate;
import com.tuhu.store.saas.marketing.request.seckill.AddSeckillTempReq;
import com.tuhu.store.saas.marketing.request.seckill.EditSecKillTempReq;
import com.tuhu.store.saas.marketing.request.seckill.QuerySeckillTempListReq;
import com.tuhu.store.saas.marketing.request.seckill.SortSeckillTempReq;
import com.tuhu.store.saas.marketing.response.ClassificationReferNum;
import com.tuhu.store.saas.marketing.response.seckill.SeckillTempDetailResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillTempPicResp;

import java.util.List;

/**
 * <p>
 * 秒杀活动基础模板表 服务类
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
public interface SeckillTemplateService extends IService<SeckillTemplate> {

    Boolean addSeckillTemplate(AddSeckillTempReq req, Long tenantId, String userId);

    List<SeckillTempDetailResp> getSeckillTempList(QuerySeckillTempListReq req, Long tenantId);

    boolean updateTemplateSort(List<SortSeckillTempReq> req, Long tenantId, String userId);

    boolean editTemplate(EditSecKillTempReq req, Long tenantId, String userId);

    SeckillTempDetailResp getTemplateDetail(String tempId, Long tenantId);

    Boolean increseTemplateRefer(String tempId, Long tenantId);

    List<ClassificationReferNum> getClassificaReferNum(List<String> ids, Long tenantId);

    List<SeckillTempPicResp> getTempPicUrlList(Long tenantId);
}
