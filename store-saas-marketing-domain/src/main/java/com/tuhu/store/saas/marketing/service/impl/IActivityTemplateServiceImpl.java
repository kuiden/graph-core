package com.tuhu.store.saas.marketing.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.beust.jcommander.internal.Lists;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ActivityTemplateMapper;
import com.tuhu.store.saas.marketing.po.ActivityTemplate;
import com.tuhu.store.saas.marketing.request.ActivityTemplateAdd;
import com.tuhu.store.saas.marketing.request.ActivityTemplateRequest;
import com.tuhu.store.saas.marketing.request.ChangeSortAcTemplateReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillTempPicResp;
import com.tuhu.store.saas.marketing.service.IActivityTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/13 14:53
 */
@Service
@Slf4j
public class IActivityTemplateServiceImpl implements IActivityTemplateService {

    @Autowired
    ActivityTemplateMapper activityTemplateMapper;

    @Override
    public Long insert(ActivityTemplateAdd req) {
        ActivityTemplate template = new ActivityTemplate();
        EntityWrapper<ActivityTemplate> wrapper = new EntityWrapper<>();
        wrapper.orderBy("sort",false);
        List<ActivityTemplate> list = activityTemplateMapper.selectList(wrapper);
        if(CollectionUtils.isEmpty(list)){
            template.setSort(1);
        }else {
            template.setSort(list.get(0).getSort() + 1);
            if(CollectionUtils.isNotEmpty(list.stream().filter(x -> req.getActivityTitle().
                    equals(x.getActivityTitle())).collect(Collectors.toList()))){
                throw new StoreSaasMarketingException("已存在同名称模板");
            }
        }
        BeanUtils.copyProperties(req,template);
        activityTemplateMapper.insertActivityTemplate(template);
        return template.getId();
    }

    @Override
    public ActivityTemplate queryDetailById(Long id) {
        return activityTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateById(ActivityTemplateAdd req) {
        ActivityTemplate template = new ActivityTemplate();
        BeanUtils.copyProperties(req,template);
        template.setUpdateTime(new Date());
        activityTemplateMapper.updateById(template);
    }

    @Override
    public void delete(Long id) {
        activityTemplateMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<ActivityTemplate> queryList(ActivityTemplateRequest req) {
        return activityTemplateMapper.queryList(req.getSearchKey(),req.getStatus(),req.getForB());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeSort(ChangeSortAcTemplateReq req) {
        //获取当前模板id的排序
        ActivityTemplate curTemplate = activityTemplateMapper.selectByPrimaryKey(req.getId());
        if(curTemplate == null){
            throw new StoreSaasMarketingException("此活动模板无效，id:"+req.getId());
        }
        //排序
        EntityWrapper<ActivityTemplate> wrapper = new EntityWrapper<>();
        wrapper.orderBy("sort",false);
        List<ActivityTemplate> list = activityTemplateMapper.selectList(wrapper);
        ActivityTemplate swapTemplate = null;
        for(int i=0 ; i<list.size() ; i++){
            ActivityTemplate template = list.get(i);
            if(template.getId() == req.getId()){
                if(req.getIsUpGrade()){
                    log.info("fetch swapcategory upgrade");
                    if(i<=0){
                        log.warn("no need to upgrade,because this is the first one.");
                        return ;
                    }
                    swapTemplate = list.get(i-1);
                    break;
                }else{
                    log.info("fetch swapcategory downgrade");
                    if(i>=list.size()-1){
                        log.warn("no need to downgrade,because this is the last one.");
                        return ;
                    }
                    swapTemplate = list.get(i+1);
                    break;
                }
            }
        }
        if(swapTemplate == null){
            log.warn("can not find swapTemplate.");
            return ;
        }
        //写表
        Integer temp = swapTemplate.getSort();
        swapTemplate.setSort(curTemplate.getSort());
        curTemplate.setSort(temp);
        activityTemplateMapper.updateById(swapTemplate);
        activityTemplateMapper.updateById(curTemplate);
    }

    @Override
    public List<SeckillTempPicResp> getTempPicUrlList() {
        EntityWrapper<ActivityTemplate> wrapper = new EntityWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderBy("sort",false);
        List<ActivityTemplate> list = activityTemplateMapper.selectList(wrapper);
        List<SeckillTempPicResp> picRespList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(l->{
                SeckillTempPicResp pic = new SeckillTempPicResp();
                pic.setTempId(l.getId().toString());
                pic.setPicUrl(l.getPicUrl());
                if (StringUtils.isNotBlank(l.getPicUrl())) {
                    picRespList.add(pic);
                }
            });
        }
        return picRespList;
    }
}
