package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.springcloud.common.bean.BeanUtil;
import com.tuhu.store.saas.marketing.constant.SeckillConstant;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.AttachedInfo;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivity;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivityItem;
import com.tuhu.store.saas.marketing.dataobject.SeckillRegistrationRecord;
import com.tuhu.store.saas.marketing.enums.SeckillActivityStatusEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillActivityMapper;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityDetailReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityReq;
import com.tuhu.store.saas.marketing.response.seckill.*;
import com.tuhu.store.saas.marketing.service.seckill.AttachedInfoService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityItemService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private SeckillRegistrationRecordService seckillRegistrationRecordService;

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Autowired
    private SeckillActivityItemService seckillActivityItemService;

    @Autowired
    private AttachedInfoService attachedInfoService;

    @Override
    public int autoUpdateOffShelf() {
        return this.baseMapper.autoUpdateOffShelf();
    }

    @Override
    public PageInfo<SeckillActivityResp> pageList(SeckillActivityReq req) {
        log.info("seckillPageList{}", JSON.toJSONString(req));
        PageInfo<SeckillActivityResp> responsePageInfo = new PageInfo<>();
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List selectList = this.baseMapper.pageList(req.getTenantId(), req.getStoreId(), req.getActivityTitle(), req.getStatus());
        PageInfo<SeckillActivity> pageInfo = new PageInfo<>(selectList);
        List<SeckillActivityResp> responseList = Lists.newArrayList();
        List<SeckillActivity> list = pageInfo.getList();
        if (null != pageInfo && CollectionUtils.isNotEmpty(list)) {
            List<String> activityIds = new ArrayList<>();
            for (SeckillActivity activity : list) {
                activityIds.add(activity.getId());
            }
            Map<String, Integer> activityIdNumMap = seckillRegistrationRecordService.activityIdNumMap(activityIds);
            responseList = list.stream().map(o -> {
                SeckillActivityResp response = new SeckillActivityResp();
                BeanUtils.copyProperties(o, response);
                dataConversion(response, req, o, activityIdNumMap);
                return response;
            }).collect(Collectors.toList());
        }
        BeanUtil.copyProperties(pageInfo, responsePageInfo);
        responsePageInfo.setList(responseList);
        return responsePageInfo;
    }

    @Override
    public List<SeckillActivityListResp> clientActivityList(Long storeId, Long tenantId) {
        log.info("clientActivityList -> storeId:{},tenantId:{}", storeId, tenantId);
        List<SeckillActivityListResp> result = new ArrayList<>();
        List<SeckillActivity> activityList = new ArrayList<>();
        //查门店所有进行中和未开始的秒杀活动，优先展示进行中的活动，再展示未开始的活动
        Date cDate = new Date();
        //添加进行中的活动
        activityList.addAll(this.baseMapper.selectList(new EntityWrapper<SeckillActivity>()
                .eq("store_id",storeId).eq("tenant_id",tenantId)
                .eq("is_delete",0).le("start_time",cDate)
                .ge("end_time",cDate).ne("status",9).orderBy("end_time")));
        //添加未开始的活动
        activityList.addAll(this.baseMapper.selectList(new EntityWrapper<SeckillActivity>()
                .eq("store_id",storeId).eq("tenant_id",tenantId)
                .eq("is_delete",0).gt("start_time",cDate)
                .ne("status",9).orderBy("start_time")));
        List<String> activityIds = activityList.stream().map(x->x.getId()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(activityIds)){
            //查询活动对应的支付成功的订单
            List<SeckillRegistrationRecord> seckillRegistrationRecords = seckillRegistrationRecordService.selectList(new EntityWrapper<SeckillRegistrationRecord>()
                    .in("seckill_activity_id",activityIds).eq("pay_status", SeckillConstant.PAY_STATUS)
                    .eq("is_delete",0).eq("store_id",storeId).eq("tenant_id",tenantId));
            Map<String,List<SeckillRegistrationRecord>> activityIdNumMap = seckillRegistrationRecords.stream().collect(Collectors.groupingBy(x->x.getSeckillActivityId()));
            //组装返回数据
            for (SeckillActivity seckillActivity : activityList){
                SeckillActivityListResp resp = new SeckillActivityListResp();
                BeanUtils.copyProperties(seckillActivity,resp);
                if (resp.getStatus().equals(SeckillActivityStatusEnum.SJ.getStatus())){
                    resp.setStatusName(SeckillActivityStatusEnum.SJ.getStatusName());
                } else if (resp.getStatus().equals(SeckillActivityStatusEnum.WSJ.getStatus())){
                    resp.setStatusName(SeckillActivityStatusEnum.WSJ.getStatusName());
                }
                resp.setTotalNumber(seckillActivity.getSellNumber());
                //计算已售出数量
                if (activityIdNumMap.containsKey(seckillActivity.getId())){
                    Integer salesNumber = 0;
                    for (SeckillRegistrationRecord record : activityIdNumMap.get(seckillActivity.getId())){
                        salesNumber += record.getQuantity().intValue();
                    }
                    resp.setSalesNumber(salesNumber);
                }
                result.add(resp);
            }
        }
        return result;
    }

    @Override
    public SeckillActivityDetailResp clientActivityDetail(SeckillActivityDetailReq req) {
        log.info("clientActivityDetail -> req:{}", req);
        SeckillActivityDetailResp result = new SeckillActivityDetailResp();
        //查活动
        SeckillActivity seckillActivity = this.baseMapper.selectById(req.getSeckillActivityId());
        if (null == seckillActivity){
            log.error("秒杀活动id={}不存在",req.getSeckillActivityId());
            throw new StoreSaasMarketingException("秒杀活动不存在");
        }
        BeanUtils.copyProperties(seckillActivity,result);
        result.setTotalNumber(seckillActivity.getSellNumber());
        //查询已售数量、当前客户已购数量
        List<SeckillRegistrationRecord> seckillRegistrationRecords = seckillRegistrationRecordService.selectList(new EntityWrapper<SeckillRegistrationRecord>()
                .eq("seckill_activity_id",req.getSeckillActivityId()).eq("pay_status", SeckillConstant.PAY_STATUS)
                .eq("is_delete",0).eq("store_id",req.getStoreId()).eq("tenant_id",req.getTenantId()));
        if (CollectionUtils.isNotEmpty(seckillRegistrationRecords)){
            Integer salesNumber = 0;
            Integer hasBuyNumber = 0;
            for (SeckillRegistrationRecord record : seckillRegistrationRecords){
                salesNumber += record.getQuantity().intValue();
                if (record.getCustomerId().equals(req.getCostomerId())){
                    hasBuyNumber += record.getQuantity().intValue();
                }
            }
            result.setSalesNumber(salesNumber);
            result.setBuyNumber(hasBuyNumber);
        }
        //查询活动状态
        Date cDate = new Date();
        if (seckillActivity.getStatus().equals(9)){
            result.setStatusName(SeckillActivityStatusEnum.XJ.getStatusName());
        } else if (seckillActivity.getStartTime().compareTo(cDate) > 0){
            result.setStatus(0); //未开始
            result.setStatusName(SeckillActivityStatusEnum.WSJ.getStatusName());
        } else if (seckillActivity.getEndTime().compareTo(cDate) >= 0){
            result.setStatus(1);  //进行中
            result.setStatusName(SeckillActivityStatusEnum.SJ.getStatusName());
        } else {
            result.setStatus(9); //已结束
            result.setStatusName(SeckillActivityStatusEnum.XJ.getStatusName());
        }
        //查活动规则、门店介绍
        List<AttachedInfo> ruleInfoList = attachedInfoService.selectList(new EntityWrapper<AttachedInfo>()
                .eq("foreign_key",seckillActivity.getId()).eq("type","SECKILLACTIVITYRULESINFO")
                .eq("store_id",req.getStoreId()).eq("tenant_id",req.getTenantId()));
        if (CollectionUtils.isNotEmpty(ruleInfoList)){
            result.setActivityRule(ruleInfoList.get(0).getContent());
        }
        List<AttachedInfo> storeInfoList = attachedInfoService.selectList(new EntityWrapper<AttachedInfo>()
                .eq("foreign_key",seckillActivity.getId()).eq("type","SECKILLACTIVITYSTOREINFO")
                .eq("store_id",req.getStoreId()).eq("tenant_id",req.getTenantId()));
        if (CollectionUtils.isNotEmpty(storeInfoList)){
            result.setStoreIntroduction(storeInfoList.get(0).getContent());
        }
        //查活动项目 按服务、商品排序
        List<SeckillActivityItem> activityItems = seckillActivityItemService.queryItemsByActivityId(req.getSeckillActivityId(),req.getStoreId(),req.getTenantId());
        if (CollectionUtils.isNotEmpty(activityItems)){
            List<SeckillActivityDetailResp.ActivityDetailItem> activityDetailItems = new ArrayList<>();
            for (SeckillActivityItem activityItem : activityItems){
                SeckillActivityDetailResp.ActivityDetailItem activityDetailItem = new SeckillActivityDetailResp.ActivityDetailItem();
                BeanUtils.copyProperties(activityItem,activityDetailItem);
                activityDetailItems.add(activityDetailItem);
            }
            result.setItems(activityDetailItems);
        }
        //查门店信息
        StoreInfoVO storeInfoVO = new StoreInfoVO();
        storeInfoVO.setStoreId(req.getStoreId());
        storeInfoVO.setTanentId(req.getTenantId());
        BizBaseResponse<StoreDTO> resultData = storeInfoClient.getStoreInfo(storeInfoVO);
        if (null != resultData && null != resultData.getData()){
            StoreDTO storeDTO = resultData.getData();
            SeckillActivityDetailResp.StoreInfo storeInfo = new SeckillActivityDetailResp.StoreInfo();
            BeanUtils.copyProperties(storeDTO,storeInfo);
            //电话设置为c端预约电话
            storeInfo.setMobilePhone(storeDTO.getClientAppointPhone());
            //门店照片
            String imagePathsString = storeDTO.getImagePaths();
            String [] imagePaths = imagePathsString.split(",");
            storeInfo.setImagePaths(imagePaths);
            result.setStoreInfo(storeInfo);
            //咨询热线设置为c端预约电话
            result.setPhoneNumber(storeDTO.getClientAppointPhone());
        }
        return result;
    }

    @Override
    public PageInfo<SeckillRecordListResp> clientActivityRecordList(SeckillActivityDetailReq req) {
        PageInfo<SeckillRecordListResp> pageInfo = new PageInfo<>();
        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        //按照购买时间倒序排
        List<SeckillRegistrationRecord> seckillRegistrationRecords = seckillRegistrationRecordService.selectList(new EntityWrapper<SeckillRegistrationRecord>()
                .eq("seckill_activity_id",req.getSeckillActivityId()).eq("pay_status", SeckillConstant.PAY_STATUS)
                .eq("is_delete",0).eq("store_id",req.getStoreId()).eq("tenant_id",req.getTenantId())
                .orderBy("payment_time",false));
        PageInfo<SeckillRegistrationRecord> recordPageInfo = new PageInfo<>(seckillRegistrationRecords);
        List<SeckillRecordListResp> respList = new ArrayList<>();
        for (SeckillRegistrationRecord record : seckillRegistrationRecords){
            SeckillRecordListResp resp = new SeckillRecordListResp();
            BeanUtils.copyProperties(record,resp);
            respList.add(resp);
        }
        BeanUtils.copyProperties(recordPageInfo,pageInfo);
        pageInfo.setList(respList);
        return pageInfo;
    }

    /**
     * 返回数据组装
     *
     * @param response
     * @param req
     */
    private void dataConversion(SeckillActivityResp response, SeckillActivityReq req, SeckillActivity o, Map<String, Integer> activityIdNumMap) {
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
            if (SeckillActivityStatusEnum.XJ.getStatus().equals(o.getStatus()) || SeckillConstant.STATUS.equals(req.getStatus())) {
                Date startTime = o.getStartTime();
                Date endTime = o.getEndTime();
                Date now = new Date();
                if (startTime.after(now)) {
                    // 未开始定义：当前时间小于活动开始时间，活动为进行中状态
                    response.setStatusName(SeckillActivityStatusEnum.WSJ.getStatusName());
                } else if (startTime.before(now) && endTime.after(now)) {
                    // 进行中定义：当前时间大于等于活动开始时间且小于结束时间，活动为进行中状态
                    response.setStatusName(SeckillActivityStatusEnum.SJ.getStatusName());
                } else {
                    // 已结束定义：当前时间大于等于活动结束时间，活动为进行中状态
                    response.setStatusName(SeckillActivityStatusEnum.XJ.getStatusName());
                }
            } else {
                response.setStatusName(SeckillActivityStatusEnum.XJ.getStatusName());
            }
        }
    }

    @Override
    @Transactional
    public boolean offShelf(String seckillActivityId) {
        SeckillActivity activity = check(seckillActivityId);
        activity.setStatus(SeckillActivityStatusEnum.XJ.getStatus());
        activity.setUpdateTime(new Date());
        activity.setUpdateUser(UserContextHolder.getStoreUserId());
        return this.updateById(activity);
    }

    public SeckillActivity check(String seckillActivityId) {
        if (null == seckillActivityId) {
            throw new StoreSaasMarketingException("活动ID不能为空");
        }
        SeckillActivity activity = this.selectById(seckillActivityId);
        if (null == activity) {
            throw new StoreSaasMarketingException("活动不存在");
        }
        if (!UserContextHolder.getStoreId().equals(activity.getStoreId())) {
            throw new StoreSaasMarketingException("非本店活动");
        }
        return activity;
    }




    @Override
    public PageInfo<SeckillRegistrationRecordResp> pageBuyOrBrowseList(SeckillActivityReq req) {
        check(req.getSeckillActivityId());
        if (req.getStatus().equals(0)) {//购买记录
            return seckillRegistrationRecordService.pageBuyList(req);
        } else {//浏览未购买  //TODO 后面灯哥处理
            return seckillRegistrationRecordService.pageNoBuyBrowseList(req);
        }
    }
}
