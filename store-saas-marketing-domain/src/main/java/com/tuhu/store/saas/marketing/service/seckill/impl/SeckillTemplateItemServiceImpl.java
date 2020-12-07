package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.tuhu.store.saas.marketing.dataobject.SeckillTemplateItem;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillTemplateItemMapper;
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
            EntityWrapper<SeckillTemplateItem> updateWrapper = new EntityWrapper();
            updateItems.forEach(item->{
                BeanUtils.copyProperties(item, updateItem);
                updateWrapper.eq(SeckillTemplateItem.ID, item.getId());
                this.update(updateItem, updateWrapper);
            });
        }
        return true;
    }

}
