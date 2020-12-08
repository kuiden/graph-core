package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
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
import com.tuhu.store.saas.marketing.enums.SeckillActivityStatusEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillActivityMapper;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.card.CardTemplateModel;
import com.tuhu.store.saas.marketing.request.seckill.*;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityDetailResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityListResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRegistrationRecordResp;
import com.tuhu.store.saas.marketing.service.AttachedInfoService;
import com.tuhu.store.saas.marketing.service.ICardService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityItemService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    private StoreInfoClient storeInfoClient;
    @Autowired
    private ICardService cardService ;
    @Autowired
    private SeckillActivityItemService itemService;
    @Autowired
    private AttachedInfoService attachedInfoService;

    @Autowired
    IdKeyGen idKeyGen;

    Function <SeckillActivityModel, Boolean> insertSeckillActivityItemFunc =(model)->{
        List<SeckillActivityItem> items = new ArrayList<>();
        for (SeckillActivityItemModel itemModel : model.getItems()) {
            SeckillActivityItem item = new SeckillActivityItem();
            item.setSeckillActivityId(model.getId());
            BeanUtils.copyProperties(itemModel,item);
            item.setId(idKeyGen.generateId(model.getTenantId()));
            items.add(item);
        }
        return itemService.insertBatch(items);
    };

    Function <SeckillActivityModel, Boolean> saveFuncAttachedInfoFunc = (model)->{
        //SeckillActivityRulesInfo
        Wrapper<AttachedInfo> wrapper = new  EntityWrapper();
        wrapper.eq(AttachedInfo.FOREIGN_KEY, model.getId())
                .eq(AttachedInfo.STORE_ID, model.getStoreId()).eq(AttachedInfo.TENANT_ID,model.getTenantId())
                .in(AttachedInfo.TYPE,Lists.newArrayList(AttachedInfoTypeEnum.SECKILLACTIVITYRULESINFO.getEnumCode()
                        ,AttachedInfoTypeEnum.SECKILLACTIVITYSTOREINFO.getEnumCode()));
        List<AttachedInfo> attachedInfos = attachedInfoService.selectList(wrapper);
        Date now = new Date(System.currentTimeMillis());
        if (CollectionUtils.isNotEmpty(attachedInfos)){

            for (AttachedInfo attachedInfo : attachedInfos) {
                attachedInfo.setUpdateUser(model.getUpdateUser());
                attachedInfo.setUpdateTime(now);
                if (attachedInfo.getType().equals(AttachedInfoTypeEnum.SECKILLACTIVITYRULESINFO.getEnumCode())){
                    //活动规则处理
                    attachedInfo.setContent(model.getRulesInfo());
                }
                if (attachedInfo.getType().equals(AttachedInfoTypeEnum.SECKILLACTIVITYSTOREINFO.getEnumCode())){
                    //门店信息处理
                    attachedInfo.setContent(model.getStoreInfo());
                }

            }
            attachedInfoService.updateBatchById(attachedInfos);

        }else
        {
        // 进入新增流程
            AttachedInfo  attachedInfo = new AttachedInfo();
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
    public String saveSeckillActivity(SeckillActivityModel model) {
        log.info("saveSeckillActivity-> start -> model -> {}", model);
        String result ="";
        boolean isInsert = StringUtils.isNotBlank(model.getId()) ? true : false;
        SeckillActivityModel entityModel = isInsert ? null : super.selectById(model.getId()).toModel();
        String checkResult = model.checkModel(entityModel, isInsert);
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

            }else {
                throw  new StoreSaasMarketingException("数据添加失败");
            }
        } else {
            //删除关联活动的商品和服务item
            itemService.deleteBatchIds(model.getItems().stream().map(x->x.getId()).collect(Collectors.toList()));
             if(super.updateById(entity)){
                 result = entity.getId();
                 insertSeckillActivityItemFunc.apply(model);
                 saveFuncAttachedInfoFunc.apply(model);
             }else {
                 throw  new StoreSaasMarketingException("数据修改失败");
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
        log.info("clientPageList -> storeId:{},tenantId:{}", storeId, tenantId);
        List<SeckillActivityListResp> result = new ArrayList<>();
        List<SeckillActivity> activityList = new ArrayList<>();
        //查门店所有进行中和未开始的秒杀活动，优先展示进行中的活动，再展示未开始的活动
        Date cDate = new Date();
        //添加进行中的活动
        activityList.addAll(this.baseMapper.selectList(new EntityWrapper<SeckillActivity>()
                .eq("store_id",storeId).eq("tenant_id",tenantId)
                .eq("is_delete",0).le("start_time",cDate)
                .gt("end_time",cDate).ne("status",9).orderBy("end_time")));
        //添加未开始的活动
        activityList.addAll(this.baseMapper.selectList(new EntityWrapper<SeckillActivity>()
                .eq("store_id",storeId).eq("tenant_id",tenantId)
                .eq("is_delete",0).gt("start_time",cDate)
                .ne("status",9).orderBy("start_time")));
        //查询活动对应的支付成功的订单数量
        List<String> activityIds = activityList.stream().map(x->x.getId()).collect(Collectors.toList());
        Map<String, Integer> activityIdNumMap = seckillRegistrationRecordService.activityIdNumMap(activityIds);
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
            if (activityIdNumMap.containsKey(seckillActivity.getId())){
                resp.setSalesNumber(activityIdNumMap.get(seckillActivity.getId()));
            }
            result.add(resp);
        }
        return result;
    }

    @Override
    public SeckillActivityDetailResp clientActivityDetail(SeckillActivityDetailReq req) {
        SeckillActivityDetailResp result = new SeckillActivityDetailResp();
        //查活动

        //查活动项目

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
            String imagePaths = storeDTO.getImagePaths();
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
