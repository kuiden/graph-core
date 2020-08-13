package com.tuhu.store.saas.marketing.service.impl;

import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ActivityTemplateMapper;
import com.tuhu.store.saas.marketing.po.ActivityTemplate;
import com.tuhu.store.saas.marketing.request.ActivityTemplateAdd;
import com.tuhu.store.saas.marketing.request.ActivityTemplateRequest;
import com.tuhu.store.saas.marketing.service.IActivityTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
}
