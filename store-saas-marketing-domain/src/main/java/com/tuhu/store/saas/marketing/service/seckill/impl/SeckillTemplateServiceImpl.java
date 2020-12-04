package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.dataobject.SeckillTemplate;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillTemplateMapper;
import com.tuhu.store.saas.marketing.request.seckill.EditSecKillTempReq;
import com.tuhu.store.saas.marketing.request.seckill.QuerySeckillTempListReq;
import com.tuhu.store.saas.marketing.request.seckill.SortSeckillTempReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillTempDetailResp;
import com.tuhu.store.saas.marketing.service.seckill.SeckillTemplateItemService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillTemplateService;
import com.tuhu.store.saas.marketing.util.IdKeyGen;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 秒杀活动基础模板表 服务实现类
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@Service
public class SeckillTemplateServiceImpl extends ServiceImpl<SeckillTemplateMapper, SeckillTemplate> implements SeckillTemplateService {

    @Autowired
    private SeckillTemplateItemService seckillTemplateItemService;

    @Autowired
    private IdKeyGen idKeyGen;

    @Override
    public List<SeckillTempDetailResp> getSeckillTempList(QuerySeckillTempListReq req, Long tenantId) {
        EntityWrapper<SeckillTemplate> wrapper = new EntityWrapper<>();
        wrapper.eq(SeckillTemplate.TENANT_ID, tenantId);
        if (StringUtils.isNotEmpty(req.getActivityTitle())) {
            wrapper.like(SeckillTemplate.ACTIVITY_TITLE, req.getActivityTitle());
        }
        if (req.getStatus() != null) {
            wrapper.eq(SeckillTemplate.STATUS, req.getStatus());
        }
        List<SeckillTemplate> tempList = this.selectList(wrapper);
        List<SeckillTempDetailResp> respList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(tempList)) {
            return respList;
        }
        List<String> classficationIds = tempList.parallelStream().map(SeckillTemplate::getClassificationId).collect(Collectors.toList());
        //todo 获取活动分类名称
        tempList.forEach(temp->{
            SeckillTempDetailResp detail = new SeckillTempDetailResp();
            BeanUtils.copyProperties(temp, detail);
            respList.add(detail);
        });
        return respList;
    }

    @Override
    public boolean updateTemplateSort(List<SortSeckillTempReq> req, Long tenantId, String userId) {
        EntityWrapper<SeckillTemplate> wrapper = new EntityWrapper<>();
        wrapper.eq(SeckillTemplate.TENANT_ID, tenantId);
        SeckillTemplate template = new SeckillTemplate();
        req.forEach(r->{
            wrapper.eq(SeckillTemplate.ID, r.getTempId());
            template.setUpdateUser(userId);
            template.setUpdateTime(new Date());
            template.setSort(r.getSort());
            this.update(template, wrapper);
        });
        return true;
    }

    @Override
    @Transactional
    public boolean updateTemplate(EditSecKillTempReq req, Long tenantId, String userId) {
        EntityWrapper<SeckillTemplate> wrapper = new EntityWrapper<>();
        wrapper.eq(SeckillTemplate.TENANT_ID, tenantId);
        wrapper.eq(SeckillTemplate.ID, req.getId());
        SeckillTemplate template = new SeckillTemplate();
        BeanUtils.copyProperties(req, template);
        template.setUpdateUser(userId);
        template.setUpdateTime(new Date());
        this.update(template, wrapper);
        if (req.getIsDelete() == 0) {
            seckillTemplateItemService.updateSeckillTempItem(req.getEditTempItemList(), req.getId(), tenantId, userId);
        }
        return true;
    }

}
