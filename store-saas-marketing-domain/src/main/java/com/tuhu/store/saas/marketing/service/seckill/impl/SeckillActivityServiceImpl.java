package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.springcloud.common.bean.BeanUtil;
import com.tuhu.store.saas.marketing.constant.SeckillConstant;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivity;
import com.tuhu.store.saas.marketing.enums.SeckillActivityStatusEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillActivityMapper;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityDetailReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityModel;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityQrCodeReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityDetailResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityListResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRegistrationRecordResp;
import com.tuhu.store.saas.marketing.service.ICardService;
import com.tuhu.store.saas.marketing.service.MiniAppService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private SeckillRegistrationRecordService seckillRegistrationRecordService;

    @Autowired
    private MiniAppService miniAppService;

    @Autowired
    private StoreInfoClient storeInfoClient;
    @Autowired
    private ICardService cardService ;

    public String saveSeckillActivity(SeckillActivityModel model) {
        log.info("saveSeckillActivity-> start -> model -> {}", model);
        boolean isInsert = StringUtils.isNotBlank(model.getId()) ? true : false;
        SeckillActivityModel entity = isInsert ? null : super.selectById(model.getId()).toModel();
        String checkResult = model.checkModel(entity, isInsert);
        //如果检查信息为空的话则进入保存模式
        if (!StringUtils.isNotBlank(checkResult)) {
            log.info("数据保存失败 -> model ->{} entity -> {}", model, entity);
            throw new StoreSaasMarketingException(checkResult);
        }
        if (isInsert) {
            // 新增
            //添加一张次卡模板


            //保存商品和服务明显
            //计算 商品/服务总价格
        }

        return "";

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

    @Override
    @Transactional
    public SeckillActivityResp poster(SeckillActivityQrCodeReq request) {
        log.info("poster{}", JSON.toJSONString(request));
        SeckillActivity activity = check(request.getSeckillActivityId());
        SeckillActivityResp resp = new SeckillActivityResp();
        BeanUtils.copyProperties(activity, resp);
        String wxQrUrl = activity.getWxQrUrl();
        if (null != wxQrUrl) {
            resp.setWxQrUrl(wxQrUrl);
        } else {
            wxQrUrl = getWxQrUrl(activity, request);
            resp.setWxQrUrl(wxQrUrl);
        }
        StoreDTO dto = this.getStoreInfo();
        resp.setStoreName(dto.getStoreName());//门店名称
        resp.setAddress(dto.getAddress());    //门店地址
        resp.setOpeningEffectiveDate(dto.getOpeningEffectiveDate()); //营业时间
        resp.setOpeningExpiryDate(dto.getOpeningExpiryDate());//营业时间
        resp.setClientAppointPhone(dto.getClientAppointPhone());//联系电话
        return resp;
    }

    /**
     * 获取登录门店的信息
     * @return
     */
    public StoreDTO getStoreInfo() {
        StoreInfoVO vo = new StoreInfoVO();
        vo.setStoreId(UserContextHolder.getStoreId());
        vo.setTanentId(UserContextHolder.getTenantId());
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

    @Override
    @Transactional
    public String qrCodeUrl(SeckillActivityQrCodeReq request) {
        log.info("qrCodeUrl{}", JSON.toJSONString(request));
        SeckillActivity activity = check(request.getSeckillActivityId());
        String wxQrUrl = activity.getWxQrUrl();
        if (null != wxQrUrl) {
            return wxQrUrl;
        }
        return getWxQrUrl(activity, request);
    }

    /**
     * 获取微信二维码，并保存
     * @param activity
     * @param request
     * @return
     */
    private String getWxQrUrl(SeckillActivity activity, SeckillActivityQrCodeReq request) {
        String wxQrUrl = miniAppService.getQrCodeUrl(request.getScene(), request.getPath(), request.getWidth());
        log.info("wxQrUrl{}", wxQrUrl);
        if (org.apache.commons.lang.StringUtils.isBlank(wxQrUrl)) {
            throw new StoreSaasMarketingException("获取二维码失败");
        }
        activity.setUpdateUser(UserContextHolder.getStoreUserId());
        activity.setUpdateTime(new Date());
        activity.setWxQrUrl(wxQrUrl);
        this.updateById(activity);
        return wxQrUrl;
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
        } else {//浏览未购买
            return seckillRegistrationRecordService.pageNoBuyBrowseList(req);
        }
    }
}
