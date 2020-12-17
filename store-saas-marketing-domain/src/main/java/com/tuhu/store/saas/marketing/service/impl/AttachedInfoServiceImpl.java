package com.tuhu.store.saas.marketing.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.dataobject.AttachedInfo;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.AttachedInfoMapper;
import com.tuhu.store.saas.marketing.request.AttachedInfoAddReq;
import com.tuhu.store.saas.marketing.request.AttachedInfoPageReq;
import com.tuhu.store.saas.marketing.request.seckill.AttachedInfoTypeEnum;
import com.tuhu.store.saas.marketing.response.AttachedInfoResp;
import com.tuhu.store.saas.marketing.service.AttachedInfoService;
import com.tuhu.store.saas.marketing.util.IdKeyGen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 附属信息表 服务实现类
 * </p>
 *
 * @author chaoyuan
 * @since 2020-12-04
 */
@Service
@Slf4j
public class AttachedInfoServiceImpl extends ServiceImpl<AttachedInfoMapper, AttachedInfo> implements AttachedInfoService {

    @Autowired
    IdKeyGen idKeyGen;
    @Override
    public String add(AttachedInfoAddReq req, Long storeId, Long tenantId, String userId) {
        log.info("AttachedInfoServiceImpl-> add -> req -> {}", req);
        AttachedInfo enetity = new AttachedInfo();
        enetity.setTenantId(tenantId);
        enetity.setStoreId(storeId);
        enetity.setTitle(req.getTitle());
        enetity.setUpdateTime(new Date());
        enetity.setUpdateUser(userId);
        enetity.setCreateUser(userId);
        enetity.setContent(req.getContent());
        enetity.setId(idKeyGen.generateId(tenantId));
        enetity.setIsDelete(Integer.valueOf(0));
        enetity.setType(req.getType().getEnumCode());
        if (!super.insert(enetity)) {
            throw new StoreSaasMarketingException("添加失败");
        }
        return enetity.getId();
    }

    @Override
    public PageInfo<AttachedInfoResp> getListByQuery(AttachedInfoPageReq req) {
        log.info("getListByQuery-> req -> {}", req);
        PageInfo<AttachedInfoResp> result = new PageInfo<>();
        Wrapper<AttachedInfo> wrapper = new EntityWrapper<AttachedInfo>().eq(AttachedInfo.STORE_ID, req.getStoreId())
                .eq(AttachedInfo.TENANT_ID, req.getTenantId())
                .eq(AttachedInfo.ISDELETE,Integer.valueOf(0));
        if (req.getType() != null) {
            wrapper.eq(AttachedInfo.TYPE, req.getType().getEnumCode());
        }
        if (StringUtils.isNotBlank(req.getForeignKey())) {
            wrapper.eq(AttachedInfo.FOREIGN_KEY, req.getForeignKey());
        }
        if(StringUtils.isNotBlank(req.getQuery())){
            wrapper.andNew().like(AttachedInfo.TITLE,req.getQuery()).or().like(AttachedInfo.CONTENT, req.getQuery());
        }
        wrapper.orderDesc(Lists.newArrayList(AttachedInfo.UPDATE_TIME));
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<AttachedInfo> attachedInfos = super.selectList(wrapper);
        PageInfo<AttachedInfo> pageInfo = new PageInfo<>(attachedInfos);
        if (CollectionUtils.isNotEmpty(attachedInfos)) {
            result.setList(new ArrayList<>());
            for (AttachedInfo attachedInfo : attachedInfos) {
                AttachedInfoResp resultItem = new AttachedInfoResp();
                BeanUtils.copyProperties(attachedInfo, resultItem);
                resultItem.setType(AttachedInfoTypeEnum.getEnumByCode(attachedInfo.getType()));
                result.getList().add(resultItem);
            }
            result.setTotal(pageInfo.getTotal());
        }
        return result;
    }
    @Override
    public AttachedInfoResp getAttachedInfoById(String id, Long storeId) {
        log.info("getAttachedInfoById-> req -> id {} storeId {}", id, storeId);
        AttachedInfoResp result =  null ;
        AttachedInfo o = super.selectOne(new EntityWrapper<AttachedInfo>().eq(AttachedInfo.ID, id)
                .eq(AttachedInfo.STORE_ID, storeId)
                .eq(AttachedInfo.ISDELETE,Integer.valueOf(0)));
        if (o != null) {
            result = new AttachedInfoResp();
            BeanUtils.copyProperties(o, result);
            result.setType(AttachedInfoTypeEnum.getEnumByCode(o.getType()));
        }
        return result;
    }

    @Override
    public Boolean del(String id, Long storeId) {
        log.info("del-> req -> id {} storeId {}", id, storeId);
        AttachedInfoResp result =  null ;
        AttachedInfo o = super.selectOne(new EntityWrapper<AttachedInfo>().eq(AttachedInfo.ID, id)
                .eq(AttachedInfo.STORE_ID, storeId));
        o.setIsDelete(Integer.valueOf(1));
        return super.updateById(o);
    }


}
