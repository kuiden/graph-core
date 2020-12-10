package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.springcloud.common.bean.BeanUtil;
import com.tuhu.store.saas.marketing.constant.SeckillConstant;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.AttachedInfo;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivity;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivityItem;
import com.tuhu.store.saas.marketing.dataobject.SeckillRegistrationRecord;
import com.tuhu.store.saas.marketing.enums.SeckillActivityStatusEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillActivityMapper;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.card.CardTemplateModel;
import com.tuhu.store.saas.marketing.request.seckill.*;
import com.tuhu.store.saas.marketing.response.seckill.*;
import com.tuhu.store.saas.marketing.service.AttachedInfoService;
import com.tuhu.store.saas.marketing.service.ICardService;
import com.tuhu.store.saas.marketing.service.MiniAppService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityItemService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.marketing.util.IdKeyGen;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
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
    private MiniAppService miniAppService;

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Autowired
    private SeckillActivityItemService seckillActivityItemService;

    @Autowired
    private AttachedInfoService attachedInfoService;
    @Autowired
    private ICardService cardService;
    @Autowired
    private SeckillActivityItemService itemService;


    @Autowired
    IdKeyGen idKeyGen;

    private Function<SeckillActivityModel, Boolean> insertSeckillActivityItemFunc = (model) -> {
        List<SeckillActivityItem> items = new ArrayList<>();
        for (SeckillActivityItemModel itemModel : model.getItems()) {
            SeckillActivityItem item = new SeckillActivityItem();
            item.setSeckillActivityId(model.getId());
            BeanUtils.copyProperties(itemModel, item);
            item.setId(idKeyGen.generateId(model.getTenantId()));
            items.add(item);
        }
        return itemService.insertBatch(items);
    };

    private Function<SeckillActivityModel, Boolean> saveFuncAttachedInfoFunc = (model) -> {
        //SeckillActivityRulesInfo
        Wrapper<AttachedInfo> wrapper = new EntityWrapper();
        wrapper.eq(AttachedInfo.FOREIGN_KEY, model.getId())
                .eq(AttachedInfo.STORE_ID, model.getStoreId()).eq(AttachedInfo.TENANT_ID, model.getTenantId())
                .in(AttachedInfo.TYPE, Lists.newArrayList(AttachedInfoTypeEnum.SECKILLACTIVITYRULESINFO.getEnumCode()
                        , AttachedInfoTypeEnum.SECKILLACTIVITYSTOREINFO.getEnumCode()));
        List<AttachedInfo> attachedInfos = attachedInfoService.selectList(wrapper);
        Date now = new Date(System.currentTimeMillis());
        if (CollectionUtils.isNotEmpty(attachedInfos)) {

            for (AttachedInfo attachedInfo : attachedInfos) {
                attachedInfo.setUpdateUser(model.getUpdateUser());
                attachedInfo.setUpdateTime(now);
                if (attachedInfo.getType().equals(AttachedInfoTypeEnum.SECKILLACTIVITYRULESINFO.getEnumCode())) {
                    //活动规则处理
                    attachedInfo.setContent(model.getRulesInfo());
                }
                if (attachedInfo.getType().equals(AttachedInfoTypeEnum.SECKILLACTIVITYSTOREINFO.getEnumCode())) {
                    //门店信息处理
                    attachedInfo.setContent(model.getStoreInfo());
                }

            }
            attachedInfoService.updateBatchById(attachedInfos);

        } else {
            // 进入新增流程
            AttachedInfo attachedInfo = new AttachedInfo();
            attachedInfo.setForeignKey(model.getId());

            attachedInfo.setCreateTime(now);
            attachedInfo.setStoreId(model.getStoreId());
            attachedInfo.setTenantId(model.getTenantId());
            attachedInfo.setUpdateTime(now);
            attachedInfo.setUpdateUser(model.getUpdateUser());
            attachedInfo.setCreateUser(model.getUpdateUser());
            attachedInfo.setType(AttachedInfoTypeEnum.SECKILLACTIVITYRULESINFO.getEnumCode());
            attachedInfo.setContent(model.getRulesInfo());
            attachedInfoService.insert(attachedInfo);
            attachedInfo.setType(AttachedInfoTypeEnum.SECKILLACTIVITYSTOREINFO.getEnumCode());
            attachedInfo.setContent(model.getStoreInfo());
            attachedInfoService.insert(attachedInfo);
        }
        return Boolean.TRUE;
    };

    @Transactional
    @Override
    public String saveSeckillActivity(SeckillActivityModel model) {
        log.info("saveSeckillActivity-> start -> model -> {}", model);
        String result;
        boolean isInsert = StringUtils.isNotBlank(model.getId()) ? true : false;
        SeckillActivityModel entityModel = isInsert ? null : super.selectById(model.getId()).toModel();
        String checkResult = model.init().checkModel(entityModel, isInsert);
        //如果检查信息为空的话则进入保存模式
        if (!StringUtils.isNotBlank(checkResult)) {
            log.info("数据保存失败 -> model ->{} entity -> {}", model, entityModel);
            throw new StoreSaasMarketingException(checkResult);
        }
        //更新保存信息
        CardTemplateModel cardTemplateModel = model.toCardTemplateModel();
        Long cardTemplateId = cardService.saveCardTemplate(cardTemplateModel, model.getUpdateUser());
        model.setTemplateId(cardTemplateId.toString());
        Date now = new Date();
        model.setUpdateTime(now);
        SeckillActivity entity = new SeckillActivity(model);
        if (isInsert) {
            entity.setCreateTime(now);
            entity.setCreateUser(model.getUpdateUser());
            entity.setId(idKeyGen.generateId(model.getTenantId()));
            // 新增
            //添加一张次卡模板
            //保存商品和服务明显
            //计算 商品/服务总价格
            if (super.insert(entity)) {
                result = entity.getId();
                model.setId(entity.getId());
                //初始化商品明细
                insertSeckillActivityItemFunc.apply(model);
                saveFuncAttachedInfoFunc.apply(model);
                //添加活动规则
                //添加门店信息

            } else {
                throw new StoreSaasMarketingException("数据添加失败");
            }
        } else {
            //删除关联活动的商品和服务item
            itemService.deleteBatchIds(model.getItems().stream().map(x -> x.getId()).collect(Collectors.toList()));
            if (super.updateById(entity)) {
                result = entity.getId();
                insertSeckillActivityItemFunc.apply(model);
                saveFuncAttachedInfoFunc.apply(model);
            } else {
                throw new StoreSaasMarketingException("数据修改失败");
            }
            //新增关联的服务和item
        }
        return result;

    }

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
        Date now = new Date();
        //添加进行中的活动
        activityList.addAll(this.baseMapper.selectList(new EntityWrapper<SeckillActivity>()
                .eq("store_id", storeId).eq("tenant_id", tenantId)
                .eq("is_delete", 0).le("start_time", now)
                .ge("end_time", now).ne("status", 9).orderBy("end_time")));
        //添加未开始的活动
        activityList.addAll(this.baseMapper.selectList(new EntityWrapper<SeckillActivity>()
                .eq("store_id", storeId).eq("tenant_id", tenantId)
                .eq("is_delete", 0).gt("start_time", now)
                .ne("status", 9).orderBy("start_time")));
        List<String> activityIds = activityList.stream().map(x -> x.getId()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(activityIds)) {
            //查询活动对应的支付成功的订单
            List<SeckillRegistrationRecord> seckillRegistrationRecords = seckillRegistrationRecordService.selectList(new EntityWrapper<SeckillRegistrationRecord>()
                    .in("seckill_activity_id", activityIds).eq("pay_status", SeckillConstant.PAY_SUCCESS_STATUS)
                    .eq("is_delete", 0).eq("store_id", storeId).eq("tenant_id", tenantId));
            Map<String, List<SeckillRegistrationRecord>> activityIdNumMap = seckillRegistrationRecords.stream().collect(Collectors.groupingBy(x -> x.getSeckillActivityId()));
            //组装返回数据
            for (SeckillActivity seckillActivity : activityList) {
                SeckillActivityListResp resp = new SeckillActivityListResp();
                BeanUtils.copyProperties(seckillActivity, resp);
                if (resp.getStatus().equals(SeckillActivityStatusEnum.SJ.getStatus())) {
                    resp.setStatusName(SeckillActivityStatusEnum.SJ.getStatusName());
                } else if (resp.getStatus().equals(SeckillActivityStatusEnum.WSJ.getStatus())) {
                    resp.setStatusName(SeckillActivityStatusEnum.WSJ.getStatusName());
                }
                resp.setTotalNumber(seckillActivity.getSellNumber());
                //计算已售出数量
                if (activityIdNumMap.containsKey(seckillActivity.getId())) {
                    Integer salesNumber = 0;
                    for (SeckillRegistrationRecord record : activityIdNumMap.get(seckillActivity.getId())) {
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
        //查询活动
        SeckillActivity seckillActivity = this.check(req.getSeckillActivityId(),false);
        BeanUtils.copyProperties(seckillActivity, result);
        result.setTotalNumber(seckillActivity.getSellNumber());
        //查询已售数量、当前客户已购数量
        List<SeckillRegistrationRecord> seckillRegistrationRecords = seckillRegistrationRecordService.selectList(new EntityWrapper<SeckillRegistrationRecord>()
                .eq("seckill_activity_id", req.getSeckillActivityId()).eq("pay_status", SeckillConstant.PAY_SUCCESS_STATUS)
                .eq("is_delete", 0).eq("store_id", req.getStoreId()).eq("tenant_id", req.getTenantId()));
        if (CollectionUtils.isNotEmpty(seckillRegistrationRecords)) {
            Integer salesNumber = 0;
            Integer hasBuyNumber = 0;
            for (SeckillRegistrationRecord record : seckillRegistrationRecords) {
                salesNumber += record.getQuantity().intValue();
                if (record.getCustomerId().equals(req.getCustomerId())) {
                    hasBuyNumber += record.getQuantity().intValue();
                }
            }
            result.setSalesNumber(salesNumber);
            result.setBuyNumber(hasBuyNumber);
        }
        //查询活动状态
        Date now = new Date();
        if (seckillActivity.getStatus().equals(9)) {
            result.setStatusName(SeckillActivityStatusEnum.XJ.getStatusName());
        } else if (seckillActivity.getStartTime().compareTo(now) > 0) {
            result.setStatus(0); //未开始
            result.setStatusName(SeckillActivityStatusEnum.WSJ.getStatusName());
        } else if (seckillActivity.getEndTime().compareTo(now) >= 0) {
            result.setStatus(1);  //进行中
            result.setStatusName(SeckillActivityStatusEnum.SJ.getStatusName());
        } else {
            result.setStatus(9); //已结束
            result.setStatusName(SeckillActivityStatusEnum.XJ.getStatusName());
        }
        //查活动规则、门店介绍
        List<AttachedInfo> ruleInfoList = attachedInfoService.selectList(new EntityWrapper<AttachedInfo>()
                .eq("foreign_key", seckillActivity.getId()).eq("type", AttachedInfoTypeEnum.SECKILLACTIVITYRULESINFO.getEnumCode())
                .eq("store_id", req.getStoreId()).eq("tenant_id", req.getTenantId()));
        if (CollectionUtils.isNotEmpty(ruleInfoList)) {
            result.setActivityRule(ruleInfoList.get(0).getContent());
        }
        List<AttachedInfo> storeInfoList = attachedInfoService.selectList(new EntityWrapper<AttachedInfo>()
                .eq("foreign_key", seckillActivity.getId()).eq("type", AttachedInfoTypeEnum.SECKILLACTIVITYSTOREINFO.getEnumCode())
                .eq("store_id", req.getStoreId()).eq("tenant_id", req.getTenantId()));
        if (CollectionUtils.isNotEmpty(storeInfoList)) {
            result.setStoreIntroduction(storeInfoList.get(0).getContent());
        }
        //查活动项目 按服务、商品排序
        List<SeckillActivityItem> activityItems = seckillActivityItemService.queryItemsByActivityId(req.getSeckillActivityId(), req.getStoreId(), req.getTenantId());
        if (CollectionUtils.isNotEmpty(activityItems)) {
            List<SeckillActivityDetailResp.ActivityDetailItem> activityDetailItems = new ArrayList<>();
            for (SeckillActivityItem activityItem : activityItems) {
                SeckillActivityDetailResp.ActivityDetailItem activityDetailItem = new SeckillActivityDetailResp.ActivityDetailItem();
                BeanUtils.copyProperties(activityItem, activityDetailItem);
                activityDetailItems.add(activityDetailItem);
            }
            result.setItems(activityDetailItems);
        }
        //查门店信息
        StoreInfoVO storeInfoVO = new StoreInfoVO();
        storeInfoVO.setStoreId(req.getStoreId());
        storeInfoVO.setTanentId(req.getTenantId());
        BizBaseResponse<StoreDTO> resultData = storeInfoClient.getStoreInfo(storeInfoVO);
        if (null != resultData && null != resultData.getData()) {
            StoreDTO storeDTO = resultData.getData();
            SeckillActivityDetailResp.StoreInfo storeInfo = new SeckillActivityDetailResp.StoreInfo();
            BeanUtils.copyProperties(storeDTO, storeInfo);
            //电话设置为c端预约电话
            storeInfo.setMobilePhone(storeDTO.getClientAppointPhone());
            //门店照片
            String imagePathsString = storeDTO.getImagePaths();
            if (StringUtils.isNotBlank(imagePathsString)){
                String[] imagePaths = imagePathsString.split(",");
                storeInfo.setImagePaths(imagePaths);
            }
            result.setStoreInfo(storeInfo);
            //咨询热线设置为c端预约电话
            result.setPhoneNumber(storeDTO.getClientAppointPhone());
        }
        return result;
    }

    @Override
    public PageInfo<SeckillRecordListResp> clientActivityRecordList(SeckillActivityDetailReq req) {
        log.info("clientActivityRecordList -> req:{}", req);
        PageInfo<SeckillRecordListResp> pageInfo = new PageInfo<>();
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        //按照购买时间倒序排
        List<SeckillRegistrationRecord> seckillRegistrationRecords = seckillRegistrationRecordService.selectList(new EntityWrapper<SeckillRegistrationRecord>()
                .eq("seckill_activity_id", req.getSeckillActivityId()).eq("pay_status", SeckillConstant.PAY_SUCCESS_STATUS)
                .eq("is_delete", 0).eq("store_id", req.getStoreId()).eq("tenant_id", req.getTenantId())
                .orderBy("payment_time", false));
        PageInfo<SeckillRegistrationRecord> recordPageInfo = new PageInfo<>(seckillRegistrationRecords);
        List<SeckillRecordListResp> respList = new ArrayList<>();
        for (SeckillRegistrationRecord record : seckillRegistrationRecords) {
            SeckillRecordListResp resp = new SeckillRecordListResp();
            BeanUtils.copyProperties(record, resp);
            respList.add(resp);
        }
        BeanUtils.copyProperties(recordPageInfo, pageInfo);
        pageInfo.setList(respList);
        return pageInfo;
    }

    @Override
    public List<CustomerActivityOrderListResp> customerActivityOrderList(String customerId, Long storeId, Long tenantId) {
        log.info("customerActivityOrderList -> customerId:{},storeId:{},tenantId:{}", customerId, storeId, tenantId);
        List<CustomerActivityOrderListResp> result = new ArrayList<>();
        //查询客户所有支付成功的订单,按支付时间倒序排
        List<SeckillRegistrationRecord> seckillRegistrationRecords = seckillRegistrationRecordService.selectList(new EntityWrapper<SeckillRegistrationRecord>()
                .eq("customer_id", customerId).eq("store_id", storeId).eq("tenant_id", tenantId)
                .eq("pay_status", SeckillConstant.PAY_SUCCESS_STATUS).eq("is_delete", 0).orderBy("payment_time", false));
        if (CollectionUtils.isNotEmpty(seckillRegistrationRecords)) {
            //查询活动
            List<String> activityIds = seckillRegistrationRecords.stream().map(x -> x.getSeckillActivityId()).distinct().collect(Collectors.toList());
            List<SeckillActivity> seckillActivityList = this.baseMapper.selectList(new EntityWrapper<SeckillActivity>().in("id", activityIds)
                    .eq("store_id", storeId).eq("tenant_id", tenantId).eq("is_delete", 0));
            //聚合订单数据
            Map<String, List<SeckillRegistrationRecord>> activityRecordMap = seckillRegistrationRecords.stream().collect(Collectors.groupingBy(x -> x.getSeckillActivityId()));
            for (SeckillActivity seckillActivity : seckillActivityList) {
                if (activityRecordMap.containsKey(seckillActivity.getId())) {
                    CustomerActivityOrderListResp customerActivityOrderListResp = new CustomerActivityOrderListResp();
                    BeanUtils.copyProperties(seckillActivity, customerActivityOrderListResp);
                    BigDecimal amount = BigDecimal.ZERO;
                    for (SeckillRegistrationRecord record : activityRecordMap.get(seckillActivity.getId())) {
                        //取订单应付金额
                        amount = amount.add(record.getExpectAmount());
                    }
                    customerActivityOrderListResp.setAmount(amount);
                    result.add(customerActivityOrderListResp);
                }
            }
        }
        return result;
    }

    @Override
    public CustomerActivityOrderDetailResp customerActivityOrderDetail(SeckillActivityDetailReq req) {
        log.info("customerActivityOrderDetail -> req:{}", req);
        CustomerActivityOrderDetailResp result = new CustomerActivityOrderDetailResp();
        //查询活动详情
        SeckillActivityDetailResp activityDetailResp = this.clientActivityDetail(req);
        result.setActivityInfo(activityDetailResp);
        //查询客户在某一个活动下的购买记录
        List<SeckillRegistrationRecord> seckillRegistrationRecords = seckillRegistrationRecordService.selectList(new EntityWrapper<SeckillRegistrationRecord>()
                .eq("seckill_activity_id", req.getSeckillActivityId()).eq("customer_id", req.getCustomerId()).eq("store_id", req.getStoreId())
                .eq("tenant_id", req.getTenantId()).eq("pay_status", SeckillConstant.PAY_SUCCESS_STATUS).eq("is_delete", 0)
                .orderBy("payment_time", false));
        if (CollectionUtils.isNotEmpty(seckillRegistrationRecords)) {
            Date now = new Date();
            List<CustomerActivityOrderDetailResp.PurchaseRecord> purchaseRecordList = new ArrayList<>();
            for (SeckillRegistrationRecord record : seckillRegistrationRecords) {
                CustomerActivityOrderDetailResp.PurchaseRecord purchaseRecord = new CustomerActivityOrderDetailResp.PurchaseRecord();
                BeanUtils.copyProperties(record, purchaseRecord);
                //设置状态
                if (null != record.getEffectiveTime() && record.getEffectiveTime().compareTo(now) < 0) {
                    purchaseRecord.setStatusName("已过期");
                } else {
                    purchaseRecord.setStatusName("使用中");
                    //购买人 = 使用人 且有次卡未过期 修改标识
                    if (record.getBuyerPhoneNumber().equals(record.getUserPhoneNumber())) {
                        result.setHasCard(true);
                    }
                }
                purchaseRecordList.add(purchaseRecord);
            }
            result.setRecordList(purchaseRecordList);
        }
        return result;
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
                Date now = DateUtils.getNoSecondOfDate(new Date()); //时间没有秒
                if (startTime.compareTo(now) >= 0) {
                    // 未开始定义：开始时间大于 = 当前时间，活动为未上架状态
                    response.setStatusName(SeckillActivityStatusEnum.WSJ.getStatusName());
                } else if (now.compareTo(startTime) >= 0 && endTime.after(now)) {
                    // 进行中定义：当前时间大于等于活动开始时间且小于结束时间，活动为进行中状态check
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
        SeckillActivity activity = check(seckillActivityId,Boolean.TRUE);
        if (SeckillActivityStatusEnum.XJ.getStatus().equals(activity.getStatus())) {
            throw new StoreSaasMarketingException("活动已下架");
        }
        activity.setStatus(SeckillActivityStatusEnum.XJ.getStatus());
        activity.setUpdateTime(new Date());
        activity.setUpdateUser(UserContextHolder.getStoreUserId());
        return this.updateById(activity);
    }

    @Override
    @Transactional
    public boolean onShelf(String seckillActivityId) {
        SeckillActivity activity = check(seckillActivityId,Boolean.TRUE);
        if (SeckillActivityStatusEnum.XJ.getStatus().equals(activity.getStatus())) {
            throw new StoreSaasMarketingException("活动已下架");
        }
        activity.setStatus(SeckillActivityStatusEnum.SJ.getStatus());
        activity.setUpdateTime(new Date());
        activity.setUpdateUser(UserContextHolder.getStoreUserId());
        return this.updateById(activity);
    }

    @Override
    @Transactional
    public SeckillActivityResp poster(SeckillActivityQrCodeReq request) {
        log.info("poster{}", JSON.toJSONString(request));
        SeckillActivity activity = check(request.getSeckillActivityId(),Boolean.TRUE);
        SeckillActivityResp resp = new SeckillActivityResp();
        BeanUtils.copyProperties(activity, resp);
        String wxQrUrl = activity.getWxQrUrl();
        if (StringUtils.isNotBlank(wxQrUrl)) {
            resp.setWxQrUrl(wxQrUrl);
        } else {
            wxQrUrl = getWxQrUrl(activity, request);
            resp.setWxQrUrl(wxQrUrl);
        }
        StoreDTO dto = this.getStoreInfo(Boolean.FALSE);
        resp.setStoreName(dto.getStoreName());//门店名称
        resp.setAddress(dto.getAddress());    //门店地址
        resp.setOpeningEffectiveDate(dto.getOpeningEffectiveDate()); //营业时间
        resp.setOpeningExpiryDate(dto.getOpeningExpiryDate());//营业时间
        resp.setClientAppointPhone(dto.getClientAppointPhone());//联系电话
        return resp;
    }

    @Override
    @Transactional
    public String qrCodeUrl(SeckillActivityQrCodeReq request) {
        log.info("qrCodeUrl{}", JSON.toJSONString(request));
        SeckillActivity activity = check(request.getSeckillActivityId(),Boolean.TRUE);
        String wxQrUrl = activity.getWxQrUrl();
        if (StringUtils.isNotBlank(wxQrUrl)) {
            return wxQrUrl;
        }
        return getWxQrUrl(activity, request);
    }

    /**
     * 获取微信二维码，并保存
     *
     * @param activity
     * @param request
     * @return
     */
    private String getWxQrUrl(SeckillActivity activity, SeckillActivityQrCodeReq request) {
        String wxQrUrl = miniAppService.getQrCodeUrl(request.getScene(), request.getPath(), request.getWidth());
        log.info("wxQrUrl{}", wxQrUrl);
        if (StringUtils.isBlank(wxQrUrl)) {
            throw new StoreSaasMarketingException("获取二维码失败");
        }
        activity.setUpdateUser(UserContextHolder.getStoreUserId());
        activity.setUpdateTime(new Date());
        activity.setWxQrUrl(wxQrUrl);
        this.updateById(activity);
        return wxQrUrl;
    }

    @Override
    public PageInfo<SeckillRegistrationRecordResp> pageBuyOrBrowseList(SeckillActivityReq req) {
        check(req.getSeckillActivityId(),Boolean.TRUE);
        if (req.getStatus().equals(0)) {//购买记录
            return seckillRegistrationRecordService.pageBuyList(req);
        } else {//浏览未购买
            return seckillRegistrationRecordService.pageNoBuyBrowseList(req);
        }
    }

    public SeckillActivity check(String seckillActivityId, Boolean flag) {
        if (null == seckillActivityId) {
            throw new StoreSaasMarketingException("活动ID不能为空");
        }
        SeckillActivity activity = this.selectById(seckillActivityId);
        if (null == activity) {
            throw new StoreSaasMarketingException("活动不存在");
        }
        if (flag) {
            //B端
            if (!UserContextHolder.getStoreId().equals(activity.getStoreId())) {
                throw new StoreSaasMarketingException("非本店活动");
            }
        }
        if (!flag) {
            //C端
            if (!EndUserContextHolder.getStoreId().equals(activity.getStoreId())) {
                throw new StoreSaasMarketingException("非本店活动");
            }
        }
        return activity;
    }

    /**
     * 获取登录门店的信息
     *
     * @return
     */
    public StoreDTO getStoreInfo(boolean flag) {
        StoreInfoVO vo = new StoreInfoVO();
        vo.setStoreId(UserContextHolder.getStoreId());
        vo.setTanentId(UserContextHolder.getTenantId());
        if (flag) {
            vo.setStoreId(EndUserContextHolder.getStoreId());
            vo.setTanentId(EndUserContextHolder.getTenantId());
        }
        BizBaseResponse<StoreDTO> result = storeInfoClient.getStoreInfo(vo);
        log.info("getStoreInfo返回参数为:{}", JSONObject.toJSONString(result));
        if (result == null || !result.isSuccess()) {
            throw new StoreSaasMarketingException("获取门店信息出错");
        }
        if (Objects.isNull(result.getData())) {
            throw new StoreSaasMarketingException("获取门店信息为空");
        }
        return result.getData();
    }
}
