package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.dataobject.SeckillClassification;
import com.tuhu.store.saas.marketing.dataobject.SeckillTemplate;
import com.tuhu.store.saas.marketing.dataobject.SeckillTemplateItem;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillTemplateMapper;
import com.tuhu.store.saas.marketing.request.seckill.AddSeckillTempReq;
import com.tuhu.store.saas.marketing.request.seckill.EditSecKillTempReq;
import com.tuhu.store.saas.marketing.request.seckill.QuerySeckillTempListReq;
import com.tuhu.store.saas.marketing.request.seckill.SortSeckillTempReq;
import com.tuhu.store.saas.marketing.response.ClassificationReferNum;
import com.tuhu.store.saas.marketing.response.seckill.SeckillTempDetailResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillTempItemResp;
import com.tuhu.store.saas.marketing.service.seckill.SeckillClassificationService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillTemplateItemService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillTemplateService;
import com.tuhu.store.saas.marketing.util.IdKeyGen;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private SeckillClassificationService seckillClassificationService;

    @Autowired
    private IdKeyGen idKeyGen;

    @Override
    @Transactional
    public Boolean addSeckillTemplate(AddSeckillTempReq req, Long tenantId, String userId) {
        SeckillTemplate seckillTemplate = new SeckillTemplate();
        BeanUtils.copyProperties(req, seckillTemplate);
        seckillTemplate.setTenantId(tenantId);
        seckillTemplate.setCreateUser(userId);
        seckillTemplate.setId(idKeyGen.generateId(tenantId));
        EntityWrapper<SeckillTemplate> wrapper = new EntityWrapper<>();
        wrapper.eq(SeckillTemplate.TENANT_ID, tenantId);
        wrapper.orderBy(SeckillTemplate.SORT, false);
        List<SeckillTemplate> templateList = this.selectList(wrapper);
        if (CollectionUtils.isEmpty(templateList)) {
            seckillTemplate.setSort(1);
        }else {
            seckillTemplate.setSort(templateList.get(0).getSort() + 1);
            if (CollectionUtils.isNotEmpty(templateList.parallelStream().filter(t->t.getActivityTitle().equals(req.getActivityTitle()))
                .collect(Collectors.toList()))) {
                throw new MarketingException("已存在同名称模板");
            }
        }
        this.insert(seckillTemplate);
        seckillTemplateItemService.addSeckillTempItem(req.getAddTempItemList(), seckillTemplate.getId(), tenantId, userId);
        return true;
    }

    @Override
    public List<SeckillTempDetailResp> getSeckillTempList(QuerySeckillTempListReq req, Long tenantId) {
        EntityWrapper<SeckillTemplate> wrapper = new EntityWrapper<>();
        wrapper.eq(SeckillTemplate.TENANT_ID, tenantId);
        wrapper.eq(SeckillTemplate.IS_DELETE, 0);
        if (StringUtils.isNotEmpty(req.getActivityTitle())) {
            wrapper.like(SeckillTemplate.ACTIVITY_TITLE, req.getActivityTitle());
        }
        if (CollectionUtils.isNotEmpty(req.getClassificationIdList())) {
            wrapper.in("classification_id", req.getClassificationIdList());
        }
        if (req.getStatus() != null) {
            wrapper.eq(SeckillTemplate.STATUS, req.getStatus());
        }
        wrapper.orderBy(SeckillTemplate.SORT, true);
        List<SeckillTemplate> tempList = this.selectList(wrapper);
        List<SeckillTempDetailResp> respList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(tempList)) {
            return respList;
        }
        List<Integer> classficationIds = tempList.stream().map(SeckillTemplate::getClassificationId).collect(Collectors.toList());
        List<SeckillClassification> classificationList = seckillClassificationService.getListByIdList(classficationIds, tenantId);
        Map<Integer, SeckillClassification> classficationMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(classificationList)) {
            classficationMap = classificationList.parallelStream().collect(Collectors.toMap(SeckillClassification::getId, SeckillClassification->SeckillClassification, (ov,nv)->nv));
        }
        for (SeckillTemplate temp : tempList) {
            SeckillTempDetailResp detail = new SeckillTempDetailResp();
            BeanUtils.copyProperties(temp, detail);
            SeckillClassification classfication = classficationMap.get(temp.getClassificationId());
            if (classfication != null) {
                detail.setClassificationName(classfication.getName());
            }
            respList.add(detail);
        }
        return respList;
    }

    @Override
    @Transactional
    public boolean updateTemplateSort(List<SortSeckillTempReq> req, Long tenantId, String userId) {
        req.forEach(r->{
            EntityWrapper<SeckillTemplate> wrapper = new EntityWrapper<>();
            wrapper.eq(SeckillTemplate.TENANT_ID, tenantId);
            SeckillTemplate template = new SeckillTemplate();
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
    public boolean editTemplate(EditSecKillTempReq req, Long tenantId, String userId) {
        EntityWrapper<SeckillTemplate> wra = new EntityWrapper<>();
        wra.eq(SeckillTemplate.TENANT_ID, tenantId);
        wra.orderBy(SeckillTemplate.SORT, false);
        List<SeckillTemplate> templateList = this.selectList(wra);
        if (CollectionUtils.isNotEmpty(templateList) && CollectionUtils.isNotEmpty(templateList.parallelStream().filter(t->t.getActivityTitle().equals(req.getActivityTitle()))
                .collect(Collectors.toList()))) {
            throw new MarketingException("已存在同名称模板");
        }
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

    @Override
    public SeckillTempDetailResp getTemplateDetail(String tempId, Long tenantId) {
        EntityWrapper<SeckillTemplate> tempWrapper = new EntityWrapper<>();
        tempWrapper.eq(SeckillTemplate.TENANT_ID, tenantId);
        tempWrapper.eq(SeckillTemplate.ID, tempId);
        tempWrapper.eq(SeckillTemplate.IS_DELETE, 0);
        SeckillTemplate temp = this.selectOne(tempWrapper);
        SeckillTempDetailResp resp = new SeckillTempDetailResp();
        BeanUtils.copyProperties(temp, resp);
        List<SeckillTemplateItem> itemList = seckillTemplateItemService.getSeckillTempItemList(tempId, tenantId);
        if (CollectionUtils.isNotEmpty(itemList)) {
            List<SeckillTempItemResp> itemResps = new ArrayList<>();
            itemList.forEach(item->{
                SeckillTempItemResp itemResp = new SeckillTempItemResp();
                BeanUtils.copyProperties(item, itemResp);
                itemResps.add(itemResp);
            });
            resp.setTempItemList(itemResps);
        }
        return resp;
    }

    /**
     * 累加秒杀活动模板引用次数
     * @param tempId
     * @param tenantId
     * @return
     */
    @Override
    public Boolean increseTemplateRefer(String tempId, Long tenantId) {
        EntityWrapper<SeckillTemplate> tempWrapper = new EntityWrapper<>();
        tempWrapper.eq(SeckillTemplate.TENANT_ID, tenantId);
        tempWrapper.eq(SeckillTemplate.ID, tempId);
        tempWrapper.eq(SeckillTemplate.IS_DELETE, 0);
        SeckillTemplate temp = this.selectOne(tempWrapper);
        if (temp == null) {
            return false;
        }
        Integer referNum = temp.getSort() + 1;
        temp.setSort(referNum);
        this.update(temp, tempWrapper);
        return true;
    }

    @Override
    public List<ClassificationReferNum> getClassificaReferNum(List<String> ids, Long tenantId) {
        return baseMapper.getClassificaReferNum(tenantId, ids);
    }

}
