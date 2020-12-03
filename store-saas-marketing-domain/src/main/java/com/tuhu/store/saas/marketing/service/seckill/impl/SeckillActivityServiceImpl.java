package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.tuhu.springcloud.common.bean.BeanUtil;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivity;
import com.tuhu.store.saas.marketing.enums.SeckillActivityStatusEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillActivityMapper;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityStatisticsResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRegistrationRecordResp;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 秒杀活动表 服务实现类
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@Service
@Slf4j
public class SeckillActivityServiceImpl extends ServiceImpl<SeckillActivityMapper, SeckillActivity> implements SeckillActivityService {
    @Override
    public int autoUpdateOffShelf() {
        return this.baseMapper.autoUpdateOffShelf();
    }

    @Override
    public PageInfo<SeckillActivityResp> pageList(SeckillActivityReq req) {
        log.info("seckillPageList{}", JSON.toJSONString(req));
        PageInfo<SeckillActivityResp> responsePageInfo = new PageInfo<>();
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        PageInfo<SeckillActivity> pageInfo = new PageInfo<>(this.selectList(buildSearchParams(req)));
        List<SeckillActivityResp> responseList = Lists.newArrayList();
        List<SeckillActivity> list = pageInfo.getList();
        if (null != pageInfo && CollectionUtils.isNotEmpty(list)) {
            //TODO map 返回下单数量
            Map<String, Integer> activityIdNumMap = new HashMap<>();
            List<String> ActivityId = new ArrayList<>();
            responseList = list.stream().map(o -> {
                SeckillActivityResp response = new SeckillActivityResp();
                BeanUtils.copyProperties(o, response);
                dataHander(response, req, o, activityIdNumMap);
                return response;
            }).collect(Collectors.toList());
        }
        BeanUtil.copyProperties(pageInfo, responsePageInfo);
        responsePageInfo.setList(responseList);
        return responsePageInfo;
    }

    /**
     * 返回数据组装
     * @param response
     * @param req
     */
    private void dataHander(SeckillActivityResp response,SeckillActivityReq req, SeckillActivity o, Map<String, Integer> activityIdNumMap){
        //TODO 返回下单数量
        Integer num = activityIdNumMap.get(o.getId());
        if (null != num) {
            response.setSalesNumber(num);
        }
        if (req.getStatus().equals(SeckillActivityStatusEnum.WSJ.getStatus())) {
            response.setStatus(req.getStatus());
            response.setStatusName(SeckillActivityStatusEnum.WSJ.getStatusName());
        } else if (req.getStatus().equals(SeckillActivityStatusEnum.SJ.getStatus())) {
            response.setStatus(req.getStatus());
            response.setStatusName(SeckillActivityStatusEnum.SJ.getStatusName());
        } else {
            response.setStatus(req.getStatus());
            if (o.getStatus().equals(SeckillActivityStatusEnum.XJ.getStatus())) {
                Date startTime = o.getStartTime();
                Date endTime = o.getEndTime();
                Date now = new Date();
                if(startTime.after(now)){
                    // 未开始定义：当前时间小于活动开始时间，活动为进行中状态
                    response.setStatusName(SeckillActivityStatusEnum.WSJ.getStatusName());
                }else if(startTime.before(now) && endTime.after(now)){
                    // 进行中定义：当前时间大于等于活动开始时间且小于结束时间，活动为进行中状态
                    response.setStatusName(SeckillActivityStatusEnum.SJ.getStatusName());
                }else {
                    // 已结束定义：当前时间大于等于活动结束时间，活动为进行中状态
                    response.setStatusName(SeckillActivityStatusEnum.XJ.getStatusName());
                }
            } else {
                response.setStatusName(SeckillActivityStatusEnum.XJ.getStatusName());
            }
        }
    }


    /**
     * 构建查询条件
     *
     * @param req
     * @return
     */
    private EntityWrapper<SeckillActivity> buildSearchParams(SeckillActivityReq req) {
        EntityWrapper<SeckillActivity> search = new EntityWrapper<>();
        search.orderBy(SeckillActivity.UPDATE_TIME, Boolean.FALSE);
        if (null != req.getStoreId()) {
            search.eq(SeckillActivity.STORE_ID, req.getStoreId());
        }
        if (null != req.getTenantId()) {
            search.eq(SeckillActivity.TENANT_ID, req.getTenantId());
        }
        if (req.getStatus().equals(SeckillActivityStatusEnum.WSJ.getStatus())) {
            // 未开始定义：当前时间小于活动开始时间
            search.gt(SeckillActivity.START_TIME, new Date()); //>
        }else if(req.getStatus().equals(SeckillActivityStatusEnum.SJ.getStatus())){
            // 进行中定义：当前时间大于等于活动开始时间且小于结束时间，活动为进行中状态
            search.le(SeckillActivity.START_TIME, new Date()); //<=
            search.gt(SeckillActivity.END_TIME, new Date());//>
        }else {
            // 已结束定义：当前时间大于等于活动结束时间，活动为进行中状态
            search.andNew().lt(SeckillActivity.END_TIME, new Date()).//<
            or().eq(SeckillActivity.STATUS, SeckillActivityStatusEnum.XJ.getStatus());
        }
        if (null != req.getActivityTitle()) {
            search.eq(SeckillActivity.ACTIVITY_TITLE, req.getActivityTitle());
        }
        return search;
    }

    @Override
    @Transactional
    public boolean offShelf(String activityId) {
        SeckillActivity activity = check(activityId);
        activity.setStatus(SeckillActivityStatusEnum.XJ.getStatus());
        activity.setUpdateTime(new Date());
        activity.setUpdateUser(UserContextHolder.getStoreUserId());
        return this.updateById(activity);
    }

    private SeckillActivity check(String activityId) {
        if (null == activityId) {
            throw new StoreSaasMarketingException("活动ID不能为空");
        }
        SeckillActivity activity = this.selectById(activityId);
        if (null == activity) {
            throw new StoreSaasMarketingException("活动不存在");
        }
        if (!UserContextHolder.getStoreId().equals(activity.getStoreId())) {
            throw new StoreSaasMarketingException("非本店活动");
        }
        return activity;
    }

    @Override
    public SeckillActivityStatisticsResp dataStatistics(String activityId) {
        SeckillActivity activity = check(activityId);
        SeckillActivityStatisticsResp resp = new SeckillActivityStatisticsResp();
        //TODO 各种取数据计算
        return resp;
    }

    @Override
    public List<SeckillRegistrationRecordResp> participateDetail(String customersId) {

        return null;
    }

    @Override
    public PageInfo<SeckillRegistrationRecordResp> pageBuyList(SeckillActivityReq req) {
        PageInfo<SeckillRegistrationRecordResp> responsePageInfo = new PageInfo<>();
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        //TODO 查询报名记录
        PageInfo<SeckillActivity> pageInfo = new PageInfo<>();
        BeanUtil.copyProperties(pageInfo, responsePageInfo);
        responsePageInfo.setList(null);
        return responsePageInfo;
    }
}
