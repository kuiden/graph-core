package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.tuhu.store.saas.marketing.dataobject.SeckillTemplate;
import com.tuhu.store.saas.marketing.dataobject.SeckillTemplateItem;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillTemplateItemMapper;
import com.tuhu.store.saas.marketing.request.seckill.AddSeckillTempItemReq;
import com.tuhu.store.saas.marketing.request.seckill.EditSeckillTempItemReq;
import com.tuhu.store.saas.marketing.service.seckill.SeckillTemplateItemService;
import com.tuhu.store.saas.marketing.util.IdKeyGen;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class SeckillTemplateItemServiceImpl extends ServiceImpl<SeckillTemplateItemMapper, SeckillTemplateItem> implements SeckillTemplateItemService {

    @Autowired
    private IdKeyGen idKeyGen;

    @Override
    public boolean updateSeckillTempItem(List<EditSeckillTempItemReq> tempItems, String tempId, Long tenantId, String userId) {
//        EntityWrapper<SeckillTemplateItem> queryWrapper = new EntityWrapper<>();
//        queryWrapper.eq(SeckillTemplateItem.SECKILL_TEMPLATE_ID, tempId).eq(SeckillTemplateItem.TENANT_ID, tenantId)
//                .eq(SeckillTemplateItem.IS_DELETE, 0);
//        List<SeckillTemplateItem> queryItems = this.selectList(queryWrapper);
        List<EditSeckillTempItemReq> insertItems = tempItems.parallelStream().filter(t-> StringUtils.isBlank(t.getId())).collect(Collectors.toList());
        List<EditSeckillTempItemReq> updateItems = tempItems.parallelStream().filter(t-> StringUtils.isNotBlank(t.getId())).collect(Collectors.toList());
        //1.添加新商品服务
        if (CollectionUtils.isNotEmpty(insertItems)) {
            SeckillTemplateItem templateItem = new SeckillTemplateItem();
            templateItem.setCreateTime(new Date());
            templateItem.setCreateUser(userId);
            templateItem.setSeckillTemplateId(tempId);
            templateItem.setTenantId(tenantId);
            insertItems.forEach(item->{
                BeanUtils.copyProperties(item, templateItem);
                templateItem.setId(idKeyGen.generateId(tenantId));
                this.insert(templateItem);
            });
        }
        //2.修改商品服务
        if (CollectionUtils.isNotEmpty(updateItems)) {
            SeckillTemplateItem updateItem = new SeckillTemplateItem();
            updateItem.setUpdateTime(new Date());
            updateItem.setUpdateUser(userId);
            updateItems.forEach(item->{
//                EntityWrapper<SeckillTemplateItem> updateWrapper = new EntityWrapper();
                BeanUtils.copyProperties(item, updateItem);
//                updateWrapper.eq(SeckillTemplateItem.ID, item.getId());
                this.updateById(updateItem);
            });
        }
        return true;
    }

    @Override
    public List<SeckillTemplateItem> getSeckillTempItemList(String templateId, Long tenantId) {
        EntityWrapper<SeckillTemplateItem> itemWrapper = new EntityWrapper<>();
        itemWrapper.eq(SeckillTemplateItem.TENANT_ID, tenantId);
        itemWrapper.eq(SeckillTemplateItem.SECKILL_TEMPLATE_ID, templateId);
        itemWrapper.eq(SeckillTemplate.IS_DELETE, 0);
        return this.selectList(itemWrapper);
    }

    @Override
    public boolean addSeckillTempItem(List<AddSeckillTempItemReq> itemsReq, String templateId, Long tenantId, String userId) {
        SeckillTemplateItem item = new SeckillTemplateItem();
        item.setTenantId(tenantId);
        item.setCreateUser(userId);
        item.setSeckillTemplateId(templateId);
        itemsReq.forEach(itemReq->{
            BeanUtils.copyProperties(itemReq, item);
            item.setId(idKeyGen.generateId(tenantId));
            this.insert(item);
        });
        return true;
    }
}
