package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.po.ActivityTemplate;
import com.tuhu.store.saas.marketing.request.ActivityTemplateAdd;
import com.tuhu.store.saas.marketing.request.ActivityTemplateRequest;
import com.tuhu.store.saas.marketing.request.ChangeSortAcTemplateReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillTempPicResp;

import java.util.List;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/13 14:48
 */
public interface IActivityTemplateService {

    Long insert(ActivityTemplateAdd req);

    ActivityTemplate queryDetailById(Long id);

    void updateById(ActivityTemplateAdd req);

    void delete(Long id);

    List<ActivityTemplate> queryList(ActivityTemplateRequest req);

    void changeSort(ChangeSortAcTemplateReq req);

    List<SeckillTempPicResp> getTempPicUrlList();

}
