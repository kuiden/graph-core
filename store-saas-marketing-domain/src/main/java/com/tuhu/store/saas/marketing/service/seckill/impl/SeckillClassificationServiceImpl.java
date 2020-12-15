package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.dataobject.SeckillClassification;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillClassificationMapper;
import com.tuhu.store.saas.marketing.request.seckill.SeckillClassificationModel;
import com.tuhu.store.saas.marketing.response.ClassificationReferNum;
import com.tuhu.store.saas.marketing.service.seckill.SeckillClassificationService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.ref.WeakReference;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 秒杀活动分类表 服务实现类
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@Service
@Slf4j
public class SeckillClassificationServiceImpl extends ServiceImpl<SeckillClassificationMapper, SeckillClassification> implements SeckillClassificationService {

    @Autowired
    private SeckillTemplateService seckillTemplateService;
    /**
     * 缓冲
     */
    private HashMap<Long, WeakReference<ArrayList<SeckillClassificationModel>>> cache = new HashMap<>(2 >> 4);

    private Function<Long, ArrayList<SeckillClassificationModel>> getAndSetCache = (key) -> {
        if (!cache.containsKey(key)) {
            WeakReference<ArrayList<SeckillClassificationModel>> arrayListWeakReference = buildCache(key);
            if (arrayListWeakReference != null) {
                cache.put(key, arrayListWeakReference);
            }
        } else if (cache.get(key) == null || cache.get(key).get() == null) {
            WeakReference<ArrayList<SeckillClassificationModel>> arrayListWeakReference = buildCache(key);
            if (arrayListWeakReference != null) {
                cache.put(key, arrayListWeakReference);
            }
        }
        return cache.get(key) == null ? null : cache.get(key).get();
    };

    private WeakReference<ArrayList<SeckillClassificationModel>> buildCache(Long key) {
        log.info("缓冲池开始构建 -> buildCache->key -> {}", key);
        ArrayList<SeckillClassificationModel> result = null;
        Wrapper<SeckillClassification> wrapper = new EntityWrapper<SeckillClassification>();
        wrapper.eq(SeckillClassification.IS_DELETE, 0);
        wrapper.eq(SeckillClassification.TENANT_ID, key);
        wrapper.orderAsc(Lists.newArrayList(SeckillClassification.PRIORITY));
        List<SeckillClassification> entities = super.selectList(wrapper);
        if (entities != null && entities.size() > 0) {

            List<ClassificationReferNum> classificaReferNum =
                    seckillTemplateService.getClassificaReferNum(entities.stream().map(x -> x.getId().toString()).collect(Collectors.toList()), key);
            Map<Integer, Integer> referMap = CollectionUtils.isNotEmpty(classificaReferNum) ? classificaReferNum.stream()
                    .collect(Collectors.toMap(k -> k.getClassificationId(), v -> v.getNum(), (i, j) -> i)) : new HashMap<>(0);
            result = new ArrayList<>();
            for (SeckillClassification entity : entities) {
                SeckillClassificationModel seckillClassificationModel = entity.toModel();
                if (referMap.containsKey(seckillClassificationModel.getId())) {
                    seckillClassificationModel.setClassificaReferNum(referMap.get(seckillClassificationModel.getId()));
                }
                result.add(seckillClassificationModel);
            }
        }
        return result == null ? null : new WeakReference(result);
    }

    @Override
    @Transactional
    public Integer save(SeckillClassificationModel req) {
        log.info("SeckillClassificationServiceImpl->save-> req -> {}", req);
        Integer result = null;
        ArrayList<SeckillClassificationModel> seckillClassificationModels = getAndSetCache.apply(req.getTenantId());
        boolean isInsert = req.getId() > 0 ? false : true;
        SeckillClassification entity;
        if (seckillClassificationModels != null && seckillClassificationModels.stream()
                .filter(x -> x.getName().trim().toLowerCase().equals(req.getName().trim().toLowerCase())
                        && x.getId() != req.getId()).count() > 0) {
            throw new StoreSaasMarketingException("分类名称重复");
        }
        if (isInsert) {
            entity = new SeckillClassification(req, isInsert);
            entity.setPriority(seckillClassificationModels == null ? 1 : seckillClassificationModels.get(seckillClassificationModels.size() - 1).getPriority() + 1);
            if (super.insert(entity)) {
                result = entity.getId();
                cache.remove(req.getTenantId());
            } else {
                throw new StoreSaasMarketingException("新增数据失败");
            }

        } else {
            Optional<SeckillClassificationModel> first
                    = seckillClassificationModels.stream().filter(x -> x.getId().equals(req.getId())).findFirst();
            first.orElseThrow(() -> new StoreSaasMarketingException("找不到相关实例"));
            entity = new SeckillClassification(first.get(), isInsert);
            entity.setName(req.getName());
            if (super.updateById(entity)) {
                result = entity.getId();
                cache.remove(req.getTenantId());
            } else {
                throw new StoreSaasMarketingException("修改数据失败");
            }
        }
        return result;
    }

    @Override
    public ArrayList<SeckillClassificationModel> getList(Long tenantId) {
        return getAndSetCache.apply(tenantId);
    }

    @Override
    public List<SeckillClassification> getListByIdList(List<Integer> idList, Long tenantId) {
        List<SeckillClassification> result = null;
        if (CollectionUtils.isNotEmpty(idList) && tenantId != null) {
            Wrapper<SeckillClassification> wrapper = new EntityWrapper<SeckillClassification>();
            wrapper.eq(SeckillClassification.IS_DELETE, 0);
            wrapper.eq(SeckillClassification.TENANT_ID, tenantId);
            wrapper.in(SeckillClassification.ID, idList);
            wrapper.orderAsc(Lists.newArrayList(SeckillClassification.PRIORITY));
            result = super.selectList(wrapper);
        }
        return result;
    }

    @Override
    @Transactional
    public Boolean del(Integer id, Long tenantId) {
        log.info("del -> req -> {}", id);
        SeckillClassification seckillClassification = super.selectById(id);

        if (seckillClassification != null) {
            if (seckillClassification.getTenantId().equals(tenantId)) {
                seckillClassification.setIsDelete(1);
                seckillClassification.setUpdateTime(new Date(System.currentTimeMillis()));
                if (!super.updateById(seckillClassification)) {
                    throw new StoreSaasMarketingException("删除失败");
                }
                cache.remove(seckillClassification.getTenantId());
            } else {
                throw new StoreSaasMarketingException("数据越权");
            }

        }
        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public ArrayList<SeckillClassificationModel> swapPriority(Long tenantId, Integer fromId, Integer toId) {
        log.info("swapPriority-> req -> tenantId {} fromId {} toid {}", tenantId, fromId, toId);
        ArrayList<SeckillClassificationModel> result = null;
        SeckillClassification fromEntity = super.selectById(fromId);
        SeckillClassification toEntity = super.selectById(toId);
        if (!fromEntity.getTenantId().equals(tenantId) || !toEntity.getTenantId().equals(tenantId)) {
            throw new StoreSaasMarketingException("数据越权");
        }
        int temp = fromEntity.getPriority();
        fromEntity.setPriority(toEntity.getPriority());
        toEntity.setPriority(temp);
        if (!super.updateBatchById(Lists.newArrayList(fromEntity, toEntity))) {
            throw new StoreSaasMarketingException("更新排序失败");
        }
        this.cache.remove(tenantId);
        result = this.getAndSetCache.apply(tenantId);
        return result;
    }


}
