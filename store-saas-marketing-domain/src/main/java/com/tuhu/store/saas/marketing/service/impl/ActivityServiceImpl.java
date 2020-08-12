package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
//import com.codingapi.tx.annotation.TxTransaction;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdReqVO;
import com.tuhu.store.saas.crm.vo.BaseIdsReqVO;
import com.tuhu.store.saas.crm.vo.CustomerSearchVO;
import com.tuhu.store.saas.dto.product.IssuedDTO;
import com.tuhu.store.saas.dto.product.ServiceGoodDTO;
import com.tuhu.store.saas.marketing.dataobject.Customer;
import com.tuhu.store.saas.marketing.enums.CrmReturnCodeEnum;
import com.tuhu.store.saas.marketing.enums.MarketingBizErrorCodeEnum;
import com.tuhu.store.saas.marketing.enums.MarketingCustomerUseStatusEnum;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ActivityCustomerMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ActivityItemMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ActivityMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ActivityTemplateMapper;
import com.tuhu.store.saas.marketing.po.*;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.remote.product.StoreProductClient;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.response.*;
//import com.tuhu.saas.crm.bo.response.resp.CommonResp;
//import com.tuhu.saas.crm.enums.CrmReturnCodeEnum;
//import com.tuhu.saas.crm.mapper.*;
//import com.tuhu.saas.crm.po.*;
//import com.tuhu.saas.crm.rpc.dto.ActivityCustomerDTO;
//import com.tuhu.saas.crm.rpc.dto.ActivityDTO;
//import com.tuhu.saas.crm.rpc.dto.ActivityItemDTO;
//import com.tuhu.saas.crm.rpc.vo.ActivityCustomerRpcVO;
//import com.tuhu.saas.crm.rpc.vo.ServiceOrderActivityUseVO;
//import com.tuhu.saas.crm.service.ICustomerService;
//import com.tuhu.saas.crm.service.IRemindService;
//import com.tuhu.saas.crm.service.MiniAppService;
//import com.tuhu.saas.crm.utils.DataTimeUtil;
//import com.tuhu.saas.product.rpc.IGoodsRpcService;
//import com.tuhu.saas.product.rpc.IIssuedSpuService;
//import com.tuhu.saas.product.rpc.dto.IssuedDTO;
//import com.tuhu.saas.product.rpc.dto.ServiceGoodDTO;
//import com.tuhu.saas.product.rpc.vo.IssuedVO;
//import com.tuhu.saas.user.rpc.IStoreInfoRpcService;
//import com.tuhu.saas.user.rpc.dto.ClientEventRecordDTO;
//import com.tuhu.saas.user.rpc.dto.StoreInfoDTO;
//import com.tuhu.saas.user.rpc.vo.ClientEventRecordVO;
//import com.tuhu.saas.user.rpc.vo.EventTypeEnum;
//import com.tuhu.saas.user.rpc.vo.StoreInfoVO;
import com.tuhu.store.saas.marketing.service.IActivityService;
import com.tuhu.store.saas.marketing.service.IClientEventRecordService;
import com.tuhu.store.saas.marketing.service.IRemindService;
import com.tuhu.store.saas.marketing.service.MiniAppService;
import com.tuhu.store.saas.marketing.util.CodeFactory;
import com.tuhu.store.saas.marketing.util.DataTimeUtil;
import com.tuhu.store.saas.marketing.util.Md5Util;
import com.tuhu.store.saas.user.dto.ClientEventRecordDTO;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.dto.StoreInfoDTO;
import com.tuhu.store.saas.user.vo.ClientEventRecordVO;
import com.tuhu.store.saas.user.vo.EventTypeEnum;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import com.tuhu.store.saas.vo.product.IssuedVO;
import com.xiangyun.versionhelper.VersionHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ActivityServiceImpl implements IActivityService {

    @Resource
    private ActivityTemplateMapper activityTemplateMapper;

    @Resource
    private ActivityMapper activityMapper;

    @Autowired
    private CodeFactory codeFactory;

    /**
     * 营销活动报名数量缓存
     */
    private static final String activityApplyCountPrefix = "ACTIVITY:APPLYCOUNT:";

    /**
     * 营销活动报名数量缓存-包括已取消的订单数
     */
    private static final String activityOrderCountPrefix = "ACTIVITY:ORDER_COUNT:";
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Autowired
    private StoreProductClient storeProductClient;

    @Resource
    private ActivityItemMapper activityItemMapper;

    @Resource
    private ActivityCustomerMapper activityCustomerMapper;

    /**
     * 营销活动个人报名限制缓存key
     */
    private static final String personalActivityApplyPrefix = "ACTIVITY:PERSONAL:";

    @Autowired
    private CustomerClient iCustomerService;

    @Autowired
    private MiniAppService miniAppService;

    @Autowired
    private IClientEventRecordService iClientEventRecordService;

    @Autowired
    private IRemindService iRemindService;

//    @Autowired
//    private IGoodsRpcService goodsRpcService;

    @Value("${sms.template.activity.apply:468}")
    private String applyMessageTemplateId;
    @Value("${sms.template.activity.writeoff:465}")
    private String writeOffMessageTemplateId;
    @Value("${sms.template.activity.cancel:466}")
    private String cancelMessageTemplateId;

    @Override
    @Transactional
//    @TxTransaction(isStart = true)
    public AddActivityReq addNewActivity(AddActivityReq addActivityReq) {
        log.info("新增营销活动，request={}", JSONObject.toJSONString(addActivityReq));
        //校验输入
        String validateResult = this.validateAddActivityReq(addActivityReq);
        if (null != validateResult) {
            throw new MarketingException(validateResult);
        }
        Activity activity = convertToNewActivity(addActivityReq);
        Long storeId = addActivityReq.getStoreId();
        //生成营销活动编码
        String codeNumber = codeFactory.getCodeNumber(CodeFactory.activityRedisPrefix, storeId);
        String code = codeFactory.generateActivityCode(storeId, codeNumber);
        activity.setActivityCode(code);
        //生成营销活动编码的密文
        String encryptedCode = Md5Util.md5(code, CodeFactory.codeSalt);
        activity.setEncryptedCode(encryptedCode);
        activityMapper.insertSelective(activity);
        //则缓存报名人数
        String key = activityApplyCountPrefix.concat(code);
        redisTemplate.opsForValue().increment(key, 0L);
        redisTemplate.opsForValue().increment(activityOrderCountPrefix.concat(code), 0L);

        //维护营销活动item明细
        this.addNewActivityItem(addActivityReq, code);
        if (null != activity.getActivityTemplateId()) {
            activityTemplateMapper.referById(activity.getActivityTemplateId());
        }
        return addActivityReq;
    }

    @Override
    @Transactional
    public void addNewActivityItem(AddActivityReq addActivityReq, String code) {
        List<ActivityItemReq> items = addActivityReq.getItems();
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        List<ActivityItem> activityItemList = new ArrayList<>();
        for (ActivityItemReq activityItemReq : items) {
//            if (null != activityItemReq.getIsFromCloud() && activityItemReq.getIsFromCloud()) {
//                issuedGoodOrServiceSpu(activityItemReq, addActivityReq.getTenantId(), addActivityReq.getCreateUser());
//            }
            ActivityItem activityItem = new ActivityItem();
            BeanUtils.copyProperties(activityItemReq, activityItem);
            activityItem.setActivityCode(code);
            activityItem.setCreateUser(activityItemReq.getUserId());
            activityItem.setCreateTime(new Date());
            activityItem.setTenantId(addActivityReq.getTenantId());
            activityItemList.add(activityItem);
        }
        activityItemMapper.insertBatch(activityItemList);
    }

    /**
     * 营销活动下发服务项目或商品
     *
     * @return
     */
    private void issuedGoodOrServiceSpu(ActivityItemReq activityItemReq, Long tenantId, String userId) {
        IssuedVO issuedVO = new IssuedVO();
        issuedVO.setPid(activityItemReq.getPid());
        issuedVO.setStoreId(activityItemReq.getStoreId());
        issuedVO.setTenantId(tenantId);
        issuedVO.setUserId(userId);
        issuedVO.setPrice(activityItemReq.getActualPrice());
        issuedVO.setVehicleType(activityItemReq.getVehicleType());
        if (null != activityItemReq.getGoodsType() && activityItemReq.getGoodsType()) {
            issuedVO.setHour(Long.valueOf(activityItemReq.getItemQuantity()));
        }
        IssuedDTO issuedDTO = null;
        try {
            log.info("营销活动下发服务项目或商品入参:{}", JSONObject.toJSONString(issuedVO));
            BizBaseResponse<IssuedDTO> goodsResp = storeProductClient.issuedGoodOrServiceSpu(issuedVO);
            if (goodsResp.getData() != null) {
                issuedDTO = goodsResp.getData();
            }
            log.info("营销活动下发服务项目或商品出参:{}", JSONObject.toJSONString(issuedDTO));
        } catch (Exception e) {
            log.error("Product出现异常：{}", e);
            throw new MarketingException(e.getMessage());
        }
        if (issuedDTO != null) {
            activityItemReq.setGoodsId(issuedDTO.getGoodId());
        }
    }

    /**
     * 构建营销活动对象
     *
     * @param addActivityReq
     * @return
     */
    private Activity convertToNewActivity(AddActivityReq addActivityReq) {
        Activity activity = new Activity();
        BeanUtils.copyProperties(addActivityReq, activity);
        //新建默认上架
        activity.setStatus(Boolean.TRUE);
        Date date = new Date();
        activity.setCreateTime(date);
        activity.setUpdateTime(date);
        activity.setUpdateUser(addActivityReq.getCreateUser());
        activity.setStartTime(DataTimeUtil.getDateStartTime(activity.getStartTime()));
        activity.setEndTime(DataTimeUtil.getDateZeroTime(activity.getEndTime()));
        if (CollectionUtils.isNotEmpty(addActivityReq.getItems())) {
            activity.setActivityPrice(BigDecimal.valueOf(addActivityReq.getItems().stream().mapToLong(ActivityItemReq::getActualPrice).sum()));
        }
        return activity;
    }

    /**
     * 校验新增营销活动的入参
     *
     * @param addActivityReq
     * @return
     */
    private String validateAddActivityReq(AddActivityReq addActivityReq) {
        if (null == addActivityReq) {
            return CrmReturnCodeEnum.REQUEST_ARG_IS_EMPTY.getDesc();
        }
        Long storeId = addActivityReq.getStoreId();
        if (null == storeId || storeId.compareTo(0L) <= 0) {
            return "门店ID无效";
        }
        //营销活动
        if (null == addActivityReq.getType()) {
            addActivityReq.setType((byte) 0);
        }
        //报名人数限制
        Long applyNumber = addActivityReq.getApplyNumber();
        if (null == applyNumber) {
            applyNumber = Long.valueOf(-1L);
            addActivityReq.setApplyNumber(applyNumber);
        }
        if (applyNumber.compareTo(0L) < 0 && !applyNumber.equals(-1L)) {
            return "报名人数限制只能为不限或限制（正整数）";
        }
//        String hotline = addActivityReq.getHotline();
//        if (!checkPattern("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$", hotline)) {
//            return "咨询热线请填入正确的手机号";
//        }
        if (null == addActivityReq.getPayType()) {
            addActivityReq.setPayType(Boolean.FALSE);
        }
        //活动开始时间
        if (null == addActivityReq.getStartTime()) {
            return "活动开始时间不能为空";
        }
        if (null == addActivityReq.getEndTime()) {
            return "活动结束时间不能为空";
        }
        Date startTime = DataTimeUtil.getDateStartTime(addActivityReq.getStartTime());
        Date endTime = DataTimeUtil.getDateZeroTime(addActivityReq.getEndTime());

        if (endTime.compareTo(new Date()) <= 0) {
            return "活动结束时间不能早于当前时间";
        }
        if (endTime.compareTo(startTime) <= 0) {
            return "活动结束时间不能早于活动开始时间";
        }
        //活动项目
        List<ActivityItemReq> items = addActivityReq.getItems();
        if (CollectionUtils.isEmpty(items)) {
            return "服务项目及商品信息不能全为空";
        }
        //服务项目-前台只传了商品code时，调接口获取商品id
//        getServiceGoodsList(addActivityReq.getItems(),addActivityReq.getStoreId(),addActivityReq.getTenantId());
        StringBuilder sb = new StringBuilder();
        for (ActivityItemReq item : items) {
            if (null == item.getIsFromCloud() || !item.getIsFromCloud()) {
                if (StringUtils.isBlank(item.getGoodsId())) {
                    sb.append("商品或服务项目GoodsID无效;");
                }
            } else {
                if (StringUtils.isBlank(item.getPid())) {
                    sb.append("商品或服务项目pID无效;");
                }
            }
            if (StringUtils.isBlank(item.getGoodsCode())) {
                sb.append("服务项目或商品编码不能为空");
            }
            if (StringUtils.isBlank(item.getGoodsName())) {
                sb.append("服务项目或商品名称不能为空");
            }
            if (null == item.getGoodsType()) {
                sb.append("商品类型不能为空");
            }
            if (null == item.getItemQuantity() || item.getItemQuantity().compareTo(0) <= 0) {
                sb.append("商品数量无效");
            }
            if (null == item.getOriginalPrice() || item.getOriginalPrice().compareTo(0L) < 0) {
                sb.append("原单价无效;");
            }
            if (null == item.getActualPrice() || item.getActualPrice().compareTo(0L) < 0) {
                sb.append("活动价无效;");
            }
            item.setStoreId(addActivityReq.getStoreId());
            item.setUserId(addActivityReq.getCreateUser());
        }
        if (sb.length() > 0) {
            return sb.toString();
        }
        Long activityTemplateId = addActivityReq.getActivityTemplateId();
        if (null != activityTemplateId && activityTemplateId.compareTo(0L) <= 0) {
            return "营销活动模板ID无效";
        } else if (null != activityTemplateId) {
            ActivityTemplate activityTemplate = activityTemplateMapper.selectByPrimaryKey(activityTemplateId);
            if (null == activityTemplate || Boolean.FALSE.equals(activityTemplate.getStatus())) {
                return "营销活动模板无效";
            }
        }
        //校验名称
        List<Activity> activityList = this.getActivityByTitle(addActivityReq.getActivityTitle(), storeId);
        if (CollectionUtils.isNotEmpty(activityList)) {
            return "已存在同名的营销活动";
        }
        if (!checkStoreInfo(storeId, addActivityReq.getTenantId(), Long.valueOf(addActivityReq.getCompanyId()))) {
            return "门店信息未完善";
        }
        return null;
    }

    /**
     * 服务项目-前台只传了商品code时，调接口获取商品id
     * @param
     */
    private void getServiceGoodsList(List<ActivityItemReq> items, Long storeId, Long tenantId) {
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        //过滤出服务项目
        items = items.stream().filter(activityItemReq -> activityItemReq.getGoodsType() && activityItemReq.getGoodsCode() != null && activityItemReq.getGoodsCode() != "").collect(Collectors.toList());
        List<String> codeList = items.stream().map(ActivityItemReq::getGoodsCode).collect(Collectors.toList());
        //调product rpc接口获取价格,拿取A档
        Map<String, ServiceGoodDTO> map = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(codeList)) {
            BizBaseResponse<List<ServiceGoodDTO>> goodsListResp = storeProductClient.queryServiceGoodListBySpuCodes(codeList, storeId, tenantId);
            if (goodsListResp.getData() != null) {
                List<ServiceGoodDTO> goodsList = goodsListResp.getData();
                map = goodsList.stream().collect(Collectors.toMap(ServiceGoodDTO::getCode, dto -> dto, (k1, k2) -> k2));
            }
        }
        Map<String, ServiceGoodDTO> mapTemp = Maps.newHashMap();
        mapTemp.putAll(map);
        items.forEach(item -> {
            if (item.getGoodsType()) {//true:服务项目
                item.setGoodsId(mapTemp.get(item.getGoodsCode()) != null ? mapTemp.get(item.getGoodsCode()).getId() : "");
            }
        });
    }



    /**
     * 检查门店信息是否完善
     * <br>完善判断规则：是否有门店地址，联系人，联系方式
     *
     * @return
     */
    private boolean checkStoreInfo(Long storeId, Long tanentId, Long companyId) {
        log.info("查询门店信息请求，storeId={},tanentId={},companyId={}", storeId, tanentId, companyId);
        StoreInfoVO storeInfoVO = new StoreInfoVO();
        storeInfoVO.setStoreId(storeId);
        storeInfoVO.setCompanyId(companyId);
        storeInfoVO.setTanentId(tanentId);
        try {
            StoreDTO storeInfoDTO = storeInfoClient.getStoreInfo(storeInfoVO).getData();
            if (null == storeInfoDTO) {
                return false;
            } else {
                if (StringUtils.isBlank(storeInfoDTO.getAddress()) || StringUtils.isBlank(storeInfoDTO.getContactName()) || StringUtils.isBlank(storeInfoDTO.getMobilePhone())) {
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("查询门店信息RPC接口异常,storeId=" + storeId + ",companyId=" + companyId + ",tanentId=" + tanentId, e);
        }
        return true;
    }

    /**
     * 检查指定的内容是否符合正则表达式
     *
     * @param patten
     * @param content
     * @return
     */
    private boolean checkPattern(String patten, String content) {
        return Pattern.matches(patten, content);
    }

    @Override
    public List<Activity> getActivityByTitle(String title, Long storeId) {
        if (StringUtils.isBlank(title) || null == storeId) {
            return null;
        }
        ActivityExample activityExample = new ActivityExample();
        ActivityExample.Criteria activityExampleCriteria = activityExample.createCriteria();
        activityExampleCriteria.andActivityTitleEqualTo(title);
        activityExampleCriteria.andStoreIdEqualTo(storeId);
        List<Activity> activityList = activityMapper.selectByExample(activityExample);
        if (CollectionUtils.isNotEmpty(activityList)) {
            return activityList;
        }
        return null;
    }

    @Override
    public ActivityResp getActivityDetailById(Long activityId, Long storeId) {
        log.info("查询营销活动详情请求activityId：{}, storeId: {}", activityId, storeId);
        if (null == activityId || activityId.compareTo(0L) <= 0) {
            return null;
        }
        Activity activity = activityMapper.selectByPrimaryKey(activityId);
        if (null == activity || !storeId.equals(activity.getStoreId())) {
            //禁止查询非本门店的营销活动
            return null;
        }
        ActivityResp activityResp = new ActivityResp();
        BeanUtils.copyProperties(activity, activityResp);
        if (null != activity.getActivityContent()){
            activityResp.setContents(JSONObject.parseArray(activity.getActivityContent(),ActivityContent.class));
        }
        //统计已报名的人数
        ActivityCustomerExample customerExample = new ActivityCustomerExample();
        ActivityCustomerExample.Criteria customerExampleCriteria = customerExample.createCriteria();
        customerExampleCriteria.andActivityCodeEqualTo(activity.getActivityCode());
        customerExampleCriteria.andUseStatusNotEqualTo((byte) 2);
        int count = activityCustomerMapper.countByExample(customerExample);
        activityResp.setApplyCount(Long.valueOf(count));
        //查询活动项目
        ActivityItemExample itemExample = new ActivityItemExample();
        ActivityItemExample.Criteria itemExampleCriteria = itemExample.createCriteria();
        itemExampleCriteria.andActivityCodeEqualTo(activity.getActivityCode());
        List<ActivityItem> activityItemList = activityItemMapper.selectByExample(itemExample);
        if (CollectionUtils.isNotEmpty(activityItemList)) {
            List<ActivityItemResp> activityItemRespList = new ArrayList<>();
            for (ActivityItem activityItem : activityItemList) {
                ActivityItemResp activityItemResp = new ActivityItemResp();
                BeanUtils.copyProperties(activityItem, activityItemResp);
                activityItemRespList.add(activityItemResp);
            }
            activityResp.setItems(activityItemRespList);
        }
        return activityResp;
    }

    @Override
    public ActivityResp getActivityDetailById(Long activityId) {
        ActivityResp activityResp = new ActivityResp();
        if (null == activityId || activityId.compareTo(0L) <= 0) {
            return null;
        }
        Activity activity = activityMapper.selectByPrimaryKey(activityId);
        if(activity != null){
            BeanUtils.copyProperties(activity, activityResp);
        }
        return activityResp;
    }

    @Override
    public ActivityChangeStatusReq changeActivityStatus(ActivityChangeStatusReq activityChangeStatusReq) {
        log.info("修改营销活动上下架状态，request={}", JSONObject.toJSONString(activityChangeStatusReq));
        Activity activity = activityMapper.selectByPrimaryKey(activityChangeStatusReq.getActivityId());
        if (null == activity || !activity.getStoreId().equals(activityChangeStatusReq.getStoreId())) {
            throw new MarketingException("营销活动不存在");
        }
        Boolean oldStatus = activity.getStatus();
        if (activityChangeStatusReq.getStatus().equals(oldStatus)) {
            String statusMessage = null;
            if (oldStatus) {
                statusMessage = "上架";
            } else {
                statusMessage = "下架";
            }
            throw new MarketingException("营销活动已经是" + statusMessage + "状态");
        }
        VersionHelper.checkVersion(activity.getUpdateTime());
        activity.setStatus(activityChangeStatusReq.getStatus());
        activity.setUpdateUser(activityChangeStatusReq.getUserId());
        activity.setUpdateTime(new Date());
        activityMapper.updateByPrimaryKey(activity);
        return null;
    }

    @Override
    public PageInfo<ActivityResp> listActivity(ActivityListReq activityListReq) {
        log.info("查询营销活动列表请求request：{}", JSONObject.toJSONString(activityListReq));
        PageInfo<ActivityResp> activityRespPageInfo = new PageInfo<>();
        ActivityExample activityExample = new ActivityExample();
        ActivityExample.Criteria activityExampleCriteria = activityExample.createCriteria();
        activityExampleCriteria.andStoreIdEqualTo(activityListReq.getStoreId());
        if (StringUtils.isNotBlank(activityListReq.getTitle())) {
            activityExampleCriteria.andActivityTitleLike("%" + activityListReq.getTitle() + "%");
        }
        Integer dateStatus = activityListReq.getDateStatus();
        if (null != dateStatus) {
            Date date = new Date();
            //0.未开始
            switch (dateStatus.intValue()) {
                case 0:
                    activityExampleCriteria.andStartTimeGreaterThan(date);
                    break;
                case 1:
                    activityExampleCriteria.andStartTimeLessThanOrEqualTo(date);
                    activityExampleCriteria.andEndTimeGreaterThanOrEqualTo(date);
                    break;
                case 2:
                    activityExampleCriteria.andEndTimeLessThan(date);
                    break;
                case 3:
                    activityExampleCriteria.andEndTimeGreaterThan(date);
                default:
                    break;
            }
        }
        activityExample.setOrderByClause("create_time desc");
        PageHelper.startPage(activityListReq.getPageNum() + 1, activityListReq.getPageSize());
        List<Activity> activityList = activityMapper.selectByExample(activityExample);
        PageInfo<Activity> activityPageInfo = new PageInfo<>(activityList);
        BeanUtils.copyProperties(activityPageInfo, activityRespPageInfo);
        if (CollectionUtils.isEmpty(activityList)) {
            activityRespPageInfo.setList(new ArrayList<>());
            return activityRespPageInfo;
        }
        //营销活动编码集合
        List<String> activityCodeList = activityList.stream().map(Activity::getActivityCode).collect(Collectors.toList());
        //查询营销活动报名情况
        List<Map<String, Object>> applyCountList = activityCustomerMapper.countByActivityCodeAndUseStatus(activityCodeList, Lists.newArrayList(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(3)));
        Map<String, Long> applyCountMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(applyCountList)) {
            applyCountList.forEach(applyCountElement -> {
                Object activityCodeObj = applyCountElement.get("activityCode");
                if (null != activityCodeObj) {
                    String activityCode = String.valueOf(activityCodeObj);
                    Object applyCountObj = applyCountElement.get("number");
                    Long applyCount = 0L;
                    if (null != applyCountObj) {
                        applyCount = Long.valueOf(String.valueOf(applyCountObj));
                    }
                    applyCountMap.put(activityCode, applyCount);
                }
            });
        }
        //查询营销活动核销情况
        List<Map<String, Object>> writeOffCountList = activityCustomerMapper.countByActivityCodeAndUseStatus(activityCodeList, Lists.newArrayList(Integer.valueOf(1), Integer.valueOf(3)));
        Map<String, Long> writeOffCountMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(writeOffCountList)) {
            writeOffCountList.forEach(writeOffCountElement -> {
                Object activityCodeObj = writeOffCountElement.get("activityCode");
                if (null != activityCodeObj) {
                    String activityCode = String.valueOf(activityCodeObj);
                    Object applyCountObj = writeOffCountElement.get("number");
                    Long applyCount = 0L;
                    if (null != applyCountObj) {
                        applyCount = Long.valueOf(String.valueOf(applyCountObj));
                    }
                    writeOffCountMap.put(activityCode, applyCount);
                }
            });
        }
        List<ActivityResp> activityRespList = new ArrayList<>();
        activityList.forEach(activity -> {
            ActivityResp activityResp = new ActivityResp();
            BeanUtils.copyProperties(activity, activityResp);
            activityResp.setApplyCount(applyCountMap.get(activity.getActivityCode()));
            activityResp.setWriteOffCount(writeOffCountMap.get(activity.getActivityCode()));
            activityResp.setDateStatus(getDataStatusByStartTimeAndEndTime(activity));
            //如果活动已过期,则删除缓存的报名人数
            if (activity.getEndTime().compareTo(new Date()) <= 0) {
                String key = activityApplyCountPrefix + activity.getActivityCode();
                redisTemplate.delete(key);
            }
            activityRespList.add(activityResp);
        });
        activityRespPageInfo.setList(activityRespList);
        return activityRespPageInfo;
    }

    /**
     * 未开始:0,进行中:1,已结束:2
     * @return
     */
    private int getDataStatusByStartTimeAndEndTime(Activity activity){
        int status=0;
        Date now=new Date();
        if (now.after(activity.getStartTime())&& now.before(activity.getEndTime())){
            status=1;
        }else if (now.after(activity.getEndTime())){
            status=2;
        }
        return status;
    }

    @Override
    @Transactional
//    @TxTransaction(isStart = true)
    public EditActivityReq editActivity(EditActivityReq editActivityReq) {
        log.info("编辑营销活动，request={}", JSONObject.toJSONString(editActivityReq));
        if (null == editActivityReq) {
            throw new MarketingException(CrmReturnCodeEnum.REQUEST_ARG_IS_EMPTY.getDesc());
        }
        Long activityId = editActivityReq.getId();
        if (null == activityId || activityId.compareTo(0L) <= 0) {
            throw new MarketingException("营销活动ID不能为空");
        }
        ActivityResp oldActivity = this.getActivityDetailById(activityId, editActivityReq.getStoreId());
        if (null == oldActivity) {
            throw new MarketingException("营销活动不存在");
        }
        Date date = new Date();
        if (oldActivity.getStartTime().compareTo(date) <= 0) {
            throw new MarketingException("活动过开始时间，不允许编辑");
        }
        if (CollectionUtils.isNotEmpty(editActivityReq.getItems())) {
            editActivityReq.setActivityPrice(BigDecimal.valueOf(editActivityReq.getItems().stream().mapToLong(ActivityItemReq::getActualPrice).sum()));
        }
        //校验输入
        String validateResult = this.validateEditActivityReq(oldActivity, editActivityReq);
        if (null != validateResult) {
            throw new MarketingException(validateResult);
        }
        Activity activity = convertToEditActivity(oldActivity, editActivityReq);
        if (null != activity.getUpdateTime()) {
            VersionHelper.checkVersion(activity.getUpdateTime());
        }

        activityMapper.updateByPrimaryKeySelective(activity);
        //报名人数缓存
        String key = activityApplyCountPrefix + activity.getActivityCode();
        if (StringUtils.isBlank(redisTemplate.opsForValue().get(key))) {
            redisTemplate.opsForValue().increment(key, 0L);
        }
        if (null != activity.getPicActivityTemplateId() && !activity.getPicActivityTemplateId().equals(oldActivity.getPicActivityTemplateId())) {
            activityTemplateMapper.referById(activity.getPicActivityTemplateId());
        }
        //维护活动项目
        editActivityItems(oldActivity, editActivityReq);
        return editActivityReq;
    }

    /**
     * 维护活动项目
     *
     * @param oldActivity
     * @param editActivityReq
     */
    @Transactional
    @Override
    public void editActivityItems(ActivityResp oldActivity, EditActivityReq editActivityReq) {
        Date date = new Date();
        Map<Long, ActivityItemResp> oldActivityItemMap = oldActivity.getItems().stream().collect(Collectors.toMap(ActivityItemResp::getId, activityItemResp -> activityItemResp));
        List<ActivityItem> addActivityItems = new ArrayList<>();
        List<ActivityItem> updateActivityItems = new ArrayList<>();
        for (ActivityItemReq activityItemReq : editActivityReq.getItems()) {
            if (null != activityItemReq.getIsFromCloud() && activityItemReq.getIsFromCloud() && null == activityItemReq.getGoodsId()) {
                issuedGoodOrServiceSpu(activityItemReq, editActivityReq.getTenantId(), editActivityReq.getUpdateUser());
            }
            Long itemId = activityItemReq.getId();
            ActivityItem activityItem = new ActivityItem();
            if (null != itemId) {
                updateActivityItems.add(activityItem);
                ActivityItemResp activityItemResp = oldActivityItemMap.remove(itemId);
                BeanUtils.copyProperties(activityItemResp, activityItem);
                BeanUtils.copyProperties(activityItemReq, activityItem);
                activityItem.setUpdateTime(date);
                activityItem.setUpdateUser(editActivityReq.getUpdateUser());
            } else {
                addActivityItems.add(activityItem);
                BeanUtils.copyProperties(activityItemReq, activityItem);
                activityItem.setActivityCode(oldActivity.getActivityCode());
                activityItem.setCreateUser(editActivityReq.getUpdateUser());
                activityItem.setCreateTime(date);
                activityItem.setUpdateTime(date);
            }
        }
        //新增活动项目
        if (addActivityItems.size() > 0) {
            activityItemMapper.insertBatch(addActivityItems);
        }
        //更新活动项目
        if (updateActivityItems.size() > 0) {
            for (ActivityItem activityItem : updateActivityItems) {
                activityItemMapper.updateByPrimaryKeySelective(activityItem);
            }
        }
        //删除活动项目
        if (oldActivityItemMap.size() > 0) {
            List<Long> deleteActivityItemIds = oldActivityItemMap.values().stream().map(ActivityItemResp::getId).collect(Collectors.toList());
            ActivityItemExample activityItemExample = new ActivityItemExample();
            ActivityItemExample.Criteria activityItemExampleCriteria = activityItemExample.createCriteria();
            activityItemExampleCriteria.andIdIn(deleteActivityItemIds);
            activityItemMapper.deleteByExample(activityItemExample);
        }
    }

    /**
     * 构建编辑后的活动对象
     *
     * @param oldActivity
     * @param editActivityReq
     * @return
     */
    private Activity convertToEditActivity(ActivityResp oldActivity, EditActivityReq editActivityReq) {
        Activity editActivity = new Activity();
        BeanUtils.copyProperties(oldActivity, editActivity);
        if (null != editActivityReq.getType()) {
            editActivity.setType(editActivityReq.getType());
        }
        if (null != editActivityReq.getPicUrl()) {
            editActivity.setPicUrl(editActivityReq.getPicUrl());
        }
        if (null != editActivityReq.getActivityTitle()) {
            editActivity.setActivityTitle(editActivityReq.getActivityTitle());
        }
        if (null != editActivityReq.getActivityIntroduce()) {
            editActivity.setActivityIntroduce(editActivityReq.getActivityIntroduce());
        }
        editActivity.setApplyNumber(editActivityReq.getApplyNumber());
        editActivity.setHotline(editActivityReq.getHotline());
        if (null != editActivityReq.getPayType()) {
            editActivity.setPayType(editActivityReq.getPayType());
        }
        editActivity.setStartTime(DataTimeUtil.getDateStartTime(editActivityReq.getStartTime()));
        editActivity.setEndTime(DataTimeUtil.getDateZeroTime(editActivityReq.getEndTime()));
        if (null != editActivityReq.getActivityTemplateId()) {
            editActivity.setActivityTemplateId(editActivityReq.getActivityTemplateId());
        }
        if (null != editActivityReq.getPicActivityTemplateId()) {
            editActivity.setPicActivityTemplateId(editActivityReq.getPicActivityTemplateId());
        }
        editActivity.setUpdateTime(new Date());
        return editActivity;
    }

    /**
     * 校验营销活动编辑入参
     *
     * @param oldActivity
     * @param editActivityReq
     * @return
     */
    private String validateEditActivityReq(ActivityResp oldActivity, EditActivityReq editActivityReq) {
        Long storeId = editActivityReq.getStoreId();
        if (null == storeId || storeId.compareTo(0L) <= 0) {
            return "门店ID无效";
        }
        //营销活动
        if (null == editActivityReq.getType()) {
            editActivityReq.setType((byte) 0);
        }
        //报名人数限制
        Long applyNumber = editActivityReq.getApplyNumber();
        if (null == applyNumber) {
            applyNumber = Long.valueOf(-1L);
            editActivityReq.setApplyNumber(applyNumber);
        }
        if (applyNumber.compareTo(0L) < 0 && !applyNumber.equals(-1L)) {
            return "报名人数限制只能为不限或限制（正整数）";
        }
        //报名人数限制不能小于已报名人数
        Long applyCount = oldActivity.getApplyCount();
        if (null == applyCount) {
            applyCount = 0L;
        }
        if (!applyNumber.equals(-1L) && applyNumber.compareTo(applyCount) < 0) {
            return "报名人数限制不能小于已报名人数";
        }
//        String hotline = editActivityReq.getHotline();
//        if (!checkPattern("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$", hotline)) {
//            return "咨询热线请填入正确的手机号";
//        }
        if (null == editActivityReq.getPayType()) {
            editActivityReq.setPayType(Boolean.FALSE);
        }
        //活动开始时间
        if (null == editActivityReq.getStartTime()) {
            return "活动开始时间不能为空";
        }
        if (null == editActivityReq.getEndTime()) {
            return "活动结束时间不能为空";
        }
        Date startTime = DataTimeUtil.getDateStartTime(editActivityReq.getStartTime());
        Date endTime = DataTimeUtil.getDateZeroTime(editActivityReq.getEndTime());

        if (endTime.compareTo(new Date()) <= 0) {
            return "活动结束时间不能早于当前时间";
        }
        if (endTime.compareTo(startTime) <= 0) {
            return "活动结束时间不能早于活动开始时间";
        }
        //活动项目
        List<ActivityItemReq> items = editActivityReq.getItems();
        if (CollectionUtils.isEmpty(items)) {
            return "服务项目及商品信息不能全为空";
        }
        //服务项目-前台只传了商品code时，调接口获取商品id
        getServiceGoodsList(editActivityReq.getItems(),editActivityReq.getStoreId(),editActivityReq.getTenantId());
        StringBuilder sb = new StringBuilder();
        for (ActivityItemReq item : items) {
            if (null == item.getIsFromCloud() || !item.getIsFromCloud()) {
                if (StringUtils.isBlank(item.getGoodsId())) {
                    sb.append("商品或服务项目GoodsID无效;");
                }
            } else {
                if (StringUtils.isBlank(item.getPid())) {
                    sb.append("商品或服务项目pID无效;");
                }
            }
            if (StringUtils.isBlank(item.getGoodsCode())) {
                sb.append("服务项目或商品编码不能为空");
            }
            if (StringUtils.isBlank(item.getGoodsName())) {
                sb.append("服务项目或商品名称不能为空");
            }
            if (null == item.getGoodsType()) {
                sb.append("商品类型不能为空");
            }
            if (null == item.getItemQuantity() || item.getItemQuantity().compareTo(0) <= 0) {
                sb.append("商品数量无效");
            }
            if (null == item.getOriginalPrice() || item.getOriginalPrice().compareTo(0L) < 0) {
                sb.append("原单价无效;");
            }
            if (null == item.getActualPrice() || item.getActualPrice().compareTo(0L) < 0) {
                sb.append("活动价无效;");
            }
            item.setStoreId(editActivityReq.getStoreId());
            item.setUserId(editActivityReq.getUpdateUser());
        }
        if (sb.length() > 0) {
            return sb.toString();
        }
        Long activityTemplateId = editActivityReq.getActivityTemplateId();
        if (null != activityTemplateId && activityTemplateId.compareTo(0L) <= 0) {
            return "营销活动模板ID无效";
        } else if (null != activityTemplateId) {
            ActivityTemplate activityTemplate = activityTemplateMapper.selectByPrimaryKey(activityTemplateId);
            if (null == activityTemplate || Boolean.FALSE.equals(activityTemplate.getStatus())) {
                return "营销活动模板无效";
            }
        }
        Long picActivityTemplateId = editActivityReq.getPicActivityTemplateId();
        if (null != picActivityTemplateId && picActivityTemplateId.compareTo(0L) <= 0) {
            return "头图营销活动模板ID无效";
        } else if (null != picActivityTemplateId) {
            ActivityTemplate activityTemplate = activityTemplateMapper.selectByPrimaryKey(picActivityTemplateId);
            if (null == activityTemplate || Boolean.FALSE.equals(activityTemplate.getStatus())) {
                return "头图营销活动模板无效";
            }
        }
        //校验名称
        if (!editActivityReq.getActivityTitle().equals(oldActivity.getActivityTitle())) {
            List<Activity> activityList = this.getActivityByTitle(editActivityReq.getActivityTitle(), storeId);
            if (CollectionUtils.isNotEmpty(activityList)) {
                for (Activity activity : activityList) {
                    if (!activity.getId().equals(editActivityReq.getId())) {
                        return "已存在同名的营销活动";
                    }
                }
            }
        }
        if (!checkStoreInfo(storeId, editActivityReq.getTenantId(), Long.valueOf(editActivityReq.getCompanyId()))) {
            return "门店信息未完善";
        }
        return null;
    }

    @Override
    @Transactional
    public CommonResp<String> applyActivity(ActivityApplyReq activityApplyReq) {
        log.info("活动报名，入参:{}", JSONObject.toJSONString(activityApplyReq));
        if (null == activityApplyReq) {
            return CommonResp.failed(4000, CrmReturnCodeEnum.REQUEST_ARG_IS_EMPTY.getDesc());
        }
        String customerId = activityApplyReq.getCustomerId();
        if (StringUtils.isBlank(customerId)) {
            return CommonResp.failed(4000, "客户ID不能为空");
        }
        String encryptedCode = activityApplyReq.getEncryptedCode();
        if (StringUtils.isBlank(encryptedCode)) {
            return CommonResp.failed(4000, "活动编码不能为空");
        }
        if (null == activityApplyReq.getStoreId() || activityApplyReq.getStoreId().compareTo(0L) <= 0) {
            return CommonResp.failed(4000, "门店ID无效");
        }
        Activity activity = activityMapper.getActivityByEncryptedCodeAndStoreId(encryptedCode, activityApplyReq.getStoreId());
        if (null == activity) {
            return CommonResp.failed(4001, "指定的营销活动不存在");
        }
        //1.营销活动本身是否允许报名
        CommonResp<String> checkActivity = checkActivityForApply(activity);
        if (null != checkActivity && !checkActivity.isSuccess()) {
            return checkActivity;
        }
        //2.阻止用户的并发请求
        String lockKey = personalActivityApplyPrefix.concat(encryptedCode).concat(customerId);
        Long personnalApplyCount = redisTemplate.opsForValue().increment(lockKey, 1L);
        redisTemplate.expire(lockKey, 2, TimeUnit.SECONDS);
        if (personnalApplyCount.compareTo(1L) > 0) {
            return CommonResp.failed(4009, "操作过于频繁，请稍后再试");
        } else {
            //检查是否已报名
            ActivityCustomerExample activityCustomerExample = new ActivityCustomerExample();
            ActivityCustomerExample.Criteria activityCustomerExampleCriteria = activityCustomerExample.createCriteria();
            activityCustomerExampleCriteria.andActivityCodeEqualTo(activity.getActivityCode());
            activityCustomerExampleCriteria.andCustomerIdEqualTo(customerId);
            activityCustomerExampleCriteria.andUseStatusNotEqualTo((byte) 2);
            int count = activityCustomerMapper.countByExample(activityCustomerExample);
            if (count > 0) {
                return CommonResp.failed(4005, "您已参加过本活动");
            }
        }
        //3.报名
        CommonResp<String> result = genarateActivityCustomer(activity, activityApplyReq);
        return result;
    }

    /**
     * 生成活动报名信息
     *
     * @param activity
     * @param activityApplyReq
     * @return
     */
    @Transactional
    public CommonResp<String> genarateActivityCustomer(Activity activity, ActivityApplyReq activityApplyReq) {
        String activityCode = activity.getActivityCode();
        Long applyNumber = activity.getApplyNumber();
        //如果不是不限制报名人数
        String key = activityApplyCountPrefix.concat(activityCode);
        if (!applyNumber.equals(-1L)) {
            String applyCountStr = redisTemplate.opsForValue().get(key);
            if (StringUtils.isBlank(applyCountStr)) {//如果之前未存放报名人数
                ActivityCustomerExample activityCustomerExample = new ActivityCustomerExample();
                ActivityCustomerExample.Criteria activityCustomerExampleCriteria = activityCustomerExample.createCriteria();
                activityCustomerExampleCriteria.andActivityCodeEqualTo(activity.getActivityCode());
                activityCustomerExampleCriteria.andUseStatusNotEqualTo((byte) 2);
                int count = activityCustomerMapper.countByExample(activityCustomerExample);
                if (Long.valueOf(count).compareTo(applyNumber) >= 0) {
                    log.warn("营销活动[code={},name={}]已报名完毕.", activityCode, activity.getActivityTitle());
                    redisTemplate.delete(key);
                    return CommonResp.failed(4006, "报名人数已满");
                } else if (StringUtils.isBlank(applyCountStr = redisTemplate.opsForValue().get(key))) {
                    Long initCount = redisTemplate.opsForValue().increment(key, Long.valueOf(count));
                    applyCountStr = String.valueOf(initCount);
                    if (initCount.compareTo(Long.valueOf(count)) != 0) {//初始化值不相等,说明有别的请求进行了初始化
                        applyCountStr = String.valueOf(redisTemplate.opsForValue().increment(key, Long.valueOf(0 - count)));
                    }
                }
            }
            Long applyCount = Long.valueOf(applyCountStr);
            if (applyCount.compareTo(applyNumber) >= 0) {
                log.warn("营销活动[code={},name={}]已报名完毕.", activityCode, activity.getActivityTitle());
                redisTemplate.delete(key);
                return CommonResp.failed(4006, "报名人数已满");
            }
        }
        Long newApplyCount = redisTemplate.opsForValue().increment(key, 1L);//记录一次报名
        if (newApplyCount.compareTo(applyNumber) > 0 && !applyNumber.equals(-1L)) {
            log.warn("营销活动[code={},name={}]已报名完毕.", activityCode, activity.getActivityTitle());
            redisTemplate.delete(key);
            return CommonResp.failed(4006, "报名人数已满");
        }
        //再次检查活动是否可以报名
        CommonResp<String> result = checkActivityForApply(activity);
        if (null != result && !result.isSuccess()) {
            redisTemplate.delete(key);
            return result;
        }

        Long newOrderCount = redisTemplate.opsForValue().increment(activityOrderCountPrefix.concat(activityCode), 1L);//订单数量加1，包含已取消订单
        ActivityCustomer activityCustomer = new ActivityCustomer();
        activityCustomer.setActivityCode(activityCode);
        activityCustomer.setActivityOrderCode(activityCode.concat(codeFactory.formatCodeWithZero(5, newOrderCount)));
        activityCustomer.setCustomerId(activityApplyReq.getCustomerId());
        activityCustomer.setTelephone(activityApplyReq.getTelephone());
        activityCustomer.setStoreId(activityApplyReq.getStoreId());
        activityCustomer.setTenantId(activityApplyReq.getTenantId());
        activityCustomer.setCreateTime(new Date());
        activityCustomer.setStartTime(activity.getStartTime());
        activityCustomer.setEndTime(activity.getEndTime());
        activityCustomer.setUseStatus((byte) 0);
        if (null != activityApplyReq.getCustomerName()){
            activityCustomer.setCustomerName(activityApplyReq.getCustomerName());
        }

        //发送报名成功通知短信
        SendRemindReq sendRemindReq = new SendRemindReq();
        sendRemindReq.setStoreId(activityApplyReq.getStoreId());
        sendRemindReq.setTenantId(activityApplyReq.getTenantId());
        sendRemindReq.setUserId(activity.getCreateUser());
        CustomerAndVehicleReq customerAndVehicleReq = new CustomerAndVehicleReq();
        customerAndVehicleReq.setCustomerId(activityApplyReq.getCustomerId());
        sendRemindReq.setCustomerList(Collections.singletonList(customerAndVehicleReq));
        sendRemindReq.setMessageTemplateId(applyMessageTemplateId);
        List<String> datas = new ArrayList<>();
        datas.add(activity.getActivityTitle());
        String datePattern = "yyyy年MM月dd日";
        // todo 活动截止时间
        datas.add(DateFormatUtils.format(activity.getEndTime(), datePattern));
        sendRemindReq.setDatas(JSONObject.toJSONString(datas));
        StringBuilder messageStatus = new StringBuilder("000");
        try {
            iRemindService.send(sendRemindReq,false);
            messageStatus.replace(0, 1, "1");
        } catch (Exception e) {
            log.error("报名成功发送短信失败，request={},error={}", JSONObject.toJSONString(sendRemindReq), ExceptionUtils.getStackTrace(e));
        }
        activityCustomer.setMessageStatus(messageStatus.toString());
        activityCustomerMapper.insertSelective(activityCustomer);
        return new CommonResp<>(activityCustomer.getActivityOrderCode());
    }

    /**
     * 检查营销活动是否允许报名
     *
     * @param activity
     * @return
     */
    private CommonResp<String> checkActivityForApply(Activity activity) {
        //1.活动是否禁用
        if (null == activity.getStatus() || !activity.getStatus()) {
            return CommonResp.failed(4002, "营销活动已下架");
        }
        //2.活动是否过期
        if (activity.getEndTime().compareTo(new Date()) <= 0) {
            return CommonResp.failed(4003, "营销活动已过期");
        }
        //3.活动是否开始
        if (activity.getStartTime().compareTo(new Date()) > 0) {
            return CommonResp.failed(4004, "营销活动还未开始");
        }
        return null;
    }

    @Override
    public ActivityCustomerResp getActivityCustomerDetail(ActivityCustomerReq activityCustomerReq) {
        log.info("客户活动详情，入参:{}", JSONObject.toJSONString(activityCustomerReq));
        ActivityCustomerResp activityCustomerResp = new ActivityCustomerResp();
        if (null == activityCustomerReq) {
            throw new MarketingException(MarketingBizErrorCodeEnum.AC_ORDER_CODE_NOT_INPUT.getDesc());
        }
        String activityOrderCode = activityCustomerReq.getActivityOrderCode();
        if (StringUtils.isBlank(activityOrderCode)) {
            throw new MarketingException("活动报名订单号不能为空");
        }
        //1.根据活动报名订单号查询活动报名信息
        ActivityCustomerExample activityCustomerExample = new ActivityCustomerExample();
        ActivityCustomerExample.Criteria activityCustomerExampleCriteria = activityCustomerExample.createCriteria();
        activityCustomerExampleCriteria.andActivityOrderCodeEqualTo(activityOrderCode);
        List<ActivityCustomer> activityCustomerList = activityCustomerMapper.selectByExample(activityCustomerExample);
        ActivityCustomer activityCustomer = activityCustomerList.get(0);

        if(activityCustomer == null){
            throw new MarketingException(MarketingBizErrorCodeEnum.AC_ORDER_NOT_EXIST.getDesc());
        }
        //response-set:基本信息copy
        BeanUtils.copyProperties(activityCustomer, activityCustomerResp);
        //2.根据活动编码查询活动详情
        String activityCode = activityCustomer.getActivityCode();
        ActivityResp activityResp = this.getActivityByActivityCode(activityCode);
        //response-set:活动详情
        getOriginalPriceOfActivity(activityResp);
        activityCustomerResp.setActivity(activityResp);
//        if (!activityCustomerReq.getIsFromClient()) {
            //3.根据客户id查询客户及车辆详情
            BaseIdReqVO baseIdReqVO = new BaseIdReqVO();
            baseIdReqVO.setId(activityCustomer.getCustomerId());
            CustomerDTO customer = iCustomerService.getCustomerById(baseIdReqVO).getData();
            if (null == customer) {
//                throw new MarketingException("客户不存在");
            }else {
//                response-set:客户全部信息
                activityCustomerResp.setCustomerName(customer.getName());
            }



            // todo 获取用户车辆详情
//            CustomerDetailResp customerDetailResp = iCustomerService.queryCustomer(customerId, customer.getTenantId(), customer.getStoreId());
//            activityCustomerResp.setCustomerDetail(customerDetailResp);
//        }
        log.info("客户活动详情，出参:{}", JSONObject.toJSONString(activityCustomerResp));
        return activityCustomerResp;
    }


    @Override
    public Boolean getOriginalPriceOfActivity(ActivityResp activityResp){
        Long totalPrice = 0L;
        if(activityResp.getActivityCode()==null){
            return false;
        }
        for(ActivityItemResp item : activityResp.getItems()){
            totalPrice += item.getOriginalPrice() * item.getItemQuantity();
        }
        activityResp.setOriginalTotalPrice(new BigDecimal(totalPrice));
        return true;
    }

    @Override
    public ActivityResp getActivityByActivityCode(String activityCode) {
        if (StringUtils.isBlank(activityCode)) {
            return null;
        }
        ActivityExample activityExample = new ActivityExample();
        ActivityExample.Criteria activityExampleCriteria = activityExample.createCriteria();
        activityExampleCriteria.andActivityCodeEqualTo(activityCode);
        List<Activity> activityList = activityMapper.selectByExample(activityExample);
        if (CollectionUtils.isEmpty(activityList)) {
            return null;
        }
        Activity activity = activityList.get(0);
        ActivityResp activityResp = new ActivityResp();
        BeanUtils.copyProperties(activity, activityResp);
        if (null != activity.getActivityContent()){
            activityResp.setContents(JSONObject.parseArray(activity.getActivityContent(),ActivityContent.class));
        }
        //查询活动项目
        ActivityItemExample itemExample = new ActivityItemExample();
        ActivityItemExample.Criteria itemExampleCriteria = itemExample.createCriteria();
        itemExampleCriteria.andActivityCodeEqualTo(activity.getActivityCode());
        List<ActivityItem> activityItemList = activityItemMapper.selectByExample(itemExample);
        if (CollectionUtils.isNotEmpty(activityItemList)) {
            List<ActivityItemResp> activityItemRespList = new ArrayList<>();
            for (ActivityItem activityItem : activityItemList) {
                ActivityItemResp activityItemResp = new ActivityItemResp();
                BeanUtils.copyProperties(activityItem, activityItemResp);
                activityItemRespList.add(activityItemResp);
            }
            activityResp.setItems(activityItemRespList);
        }
        return activityResp;
    }

    @Override
    @Transactional
    public Boolean writeOffOrCancelActivityCustomer(ActivityCustomerReq activityCustomerReq) {
        log.info("客户报名核销或取消订单，入参:{}", JSONObject.toJSONString(activityCustomerReq));
        if (null == activityCustomerReq) {
            throw new MarketingException(CrmReturnCodeEnum.REQUEST_ARG_IS_EMPTY.getDesc());
        }
        Boolean result=true;
        Integer useStatus = activityCustomerReq.getUseStatus();
        if(useStatus==null ||
            !(useStatus.equals(MarketingCustomerUseStatusEnum.AC_ORDER_IS_USED.getStatus())
            || useStatus.equals(MarketingCustomerUseStatusEnum.AC_ORDER_IS_CANCELED.getStatus())
            )){
            throw new MarketingException(MarketingBizErrorCodeEnum.PARAM_ERROR.getDesc()+",检查useStatus");
        }
        String activityOrderCode = activityCustomerReq.getActivityOrderCode();
        if(activityOrderCode==null){
            throw new MarketingException(MarketingBizErrorCodeEnum.AC_ORDER_CODE_NOT_INPUT.getDesc());
        }
        //查询开始
        ActivityCustomerExample activityCustomerExample= new ActivityCustomerExample();
        activityCustomerExample.setDistinct(true);
        ActivityCustomerExample.Criteria activityExampleCriteria = activityCustomerExample.createCriteria();
        activityExampleCriteria.andActivityOrderCodeEqualTo(activityOrderCode);
        String customerId=activityCustomerReq.getCustomerId();
        if(StringUtils.isNotEmpty(customerId)){
            activityExampleCriteria.andCustomerIdEqualTo(customerId);
        }
        Long storeId = activityCustomerReq .getStoreId();
        if(storeId != null){
            activityExampleCriteria.andStoreIdEqualTo(storeId);
        }

        List<ActivityCustomer> activityCustomerList = activityCustomerMapper.selectByExample(activityCustomerExample);
        if(activityCustomerList.size() < 1){
            throw new MarketingException("查询不到活动信息");
        }
        ActivityCustomer activityCustomer = activityCustomerList.get(0);
        //查询结果
        if(activityCustomer == null){
            throw new MarketingException(MarketingBizErrorCodeEnum.AC_ORDER_NOT_EXIST.getDesc());
        }
        //状态检查
        if(activityCustomer.getUseStatus().intValue() == useStatus.intValue() ){
            if(useStatus.equals(MarketingCustomerUseStatusEnum.AC_ORDER_IS_USED.getStatus())) {
                //重复核销
                throw new MarketingException("已核销，请勿重复执行");
            }else{
                //取消
                throw new MarketingException("已取消，无法操作");
            }
        }

        try {
            //发送短信通知
//            SendRemindReq sendRemindReq = new SendRemindReq();
//            sendRemindReq.setStoreId(activityCustomerReq.getStoreId());
//            sendRemindReq.setTenantId(activityCustomerReq.getTenantId());
//            sendRemindReq.setUserId(activityCustomerReq.getUserId());
//            CustomerAndVehicleReq customerAndVehicleReq = new CustomerAndVehicleReq();
//            customerAndVehicleReq.setCustomerId(activityCustomerReq.getCustomerId());
//            sendRemindReq.setList(Collections.singletonList(customerAndVehicleReq));
//            List<String> datas = Collections.singletonList(activityResp.getActivityTitle());
//            sendRemindReq.setDatas(JSONObject.toJSONString(datas));
            StringBuilder messageStatus = new StringBuilder(activityCustomer.getMessageStatus());
            if(useStatus.equals(MarketingCustomerUseStatusEnum.AC_ORDER_IS_USED.getStatus())) {
                //核销
//                sendRemindReq.setMessageTemplateId(writeOffMessageTemplateId);
                //状态二进制消息更新
                messageStatus.replace(1, 2, "1");
            }else{
                //取消
//                sendRemindReq.setMessageTemplateId(cancelMessageTemplateId);
                messageStatus.replace(2, 3, "1");
                String notCancelKey=activityApplyCountPrefix.concat(activityCustomer.getActivityCode());
                redisTemplate.opsForValue().increment(notCancelKey,-1L);
            }
            activityCustomer.setUseTime(new Date());
            activityCustomer.setMessageStatus(messageStatus.toString());
            activityCustomer.setUseStatus(useStatus.byteValue());
//            iRemindService.send(sendRemindReq);
        } catch (Exception e) {
//            log.error("营销活动发送短信异常,request={},error={}", JSONObject.toJSONString(sendRemindReq), ExceptionUtils.getStackTrace(e));
        }
        result = activityCustomerMapper.updateByPrimaryKeySelective(activityCustomer) > 0;
        return result;
    }


//    @Override
//    @Transactional
//    @TxTransaction
//    public ActivityCustomerDTO useOrCancelActivityCustomerForOrder(ServiceOrderActivityUseVO serviceOrderActivityUseVO) {
//        log.info("使用或取消使用营销活动,request={}", JSONObject.toJSONString(serviceOrderActivityUseVO));
//        String result = validateServiceOrderActivityUseVO(serviceOrderActivityUseVO);
//        if (null != result) {
//            throw new CrmException(result);
//        }
//        //1.查询订单详情
//        String activityOrderCode = serviceOrderActivityUseVO.getActivityOrderCode();
//        ActivityCustomerReq activityCustomerReq = new ActivityCustomerReq();
//        activityCustomerReq.setStoreId(Long.valueOf(serviceOrderActivityUseVO.getStoreId()));
//        activityCustomerReq.setCustomerId(serviceOrderActivityUseVO.getCustomerId());
//        activityCustomerReq.setActivityOrderCode(activityOrderCode);
//        ActivityCustomerResp activityCustomerResp = this.getActivityCustomerDetail(activityCustomerReq);
//        Boolean use = serviceOrderActivityUseVO.getUse();
//        ActivityCustomer activityCustomer = new ActivityCustomer();
//        BeanUtils.copyProperties(activityCustomerResp, activityCustomer);
//        ActivityCustomerExample activityCustomerExample = new ActivityCustomerExample();
//        ActivityCustomerExample.Criteria activityCustomerExampleCriteria = activityCustomerExample.createCriteria();
//        activityCustomerExampleCriteria.andIdEqualTo(activityCustomer.getId());
//        //2.使用营销活动
//        if (use) {
//            if (!activityCustomerResp.getUseStatus().equals((byte) 1)) {
//                throw new CrmException("当前状态不允许开单");
//            }
//            activityCustomer.setServiceOrderId(serviceOrderActivityUseVO.getOrderId());
//            activityCustomer.setServiceOrderTime(new Date());
//            activityCustomer.setUseStatus((byte) 3);
//            activityCustomerExampleCriteria.andUseStatusEqualTo((byte) 1);
//        } else {
//            if (!activityCustomerResp.getUseStatus().equals((byte) 3)) {
//                throw new CrmException("当前状态不允许取消");
//            }
//            activityCustomer.setServiceOrderId(null);
//            activityCustomer.setServiceOrderTime(null);
//            activityCustomer.setUseStatus((byte) 1);
//            activityCustomerExampleCriteria.andUseStatusEqualTo((byte) 3);
//        }
//        //3.持久化更新
//        int count = activityCustomerMapper.updateByExample(activityCustomer, activityCustomerExample);
//        if (count != 1) {
//            String message = use ? "开单" : "取消开单";
//            throw new CrmException("客户营销活动" + message + "失败");
//        }
//        //4.构建返回结果
//        ActivityCustomerDTO activityCustomerDTO = new ActivityCustomerDTO();
//        BeanUtils.copyProperties(activityCustomer, activityCustomerDTO);
//        ActivityDTO activityDTO = new ActivityDTO();
//        BeanUtils.copyProperties(activityCustomerResp.getActivity(), activityDTO);
//        List<ActivityItemResp> items = activityCustomerResp.getActivity().getItems();
//        List<ActivityItemDTO> itemDTOS = new ArrayList<>();
//        for (ActivityItemResp itemResp : items) {
//            ActivityItemDTO activityItemDTO = new ActivityItemDTO();
//            BeanUtils.copyProperties(itemResp, activityItemDTO);
//            itemDTOS.add(activityItemDTO);
//        }
//        activityDTO.setItems(itemDTOS);
//        activityCustomerDTO.setActivity(activityDTO);
//        return activityCustomerDTO;
//    }

//    /**
//     * 校验工单用营销活动或需求的入参
//     *
//     * @param serviceOrderActivityUseVO
//     * @return
//     */
//    private String validateServiceOrderActivityUseVO(ServiceOrderActivityUseVO serviceOrderActivityUseVO) {
//        if (null == serviceOrderActivityUseVO) {
//            return CrmReturnCodeEnum.REQUEST_ARG_IS_EMPTY.getDesc();
//        }
//        if (null == serviceOrderActivityUseVO.getUse()) {
//            return "操作类型不能为空";
//        }
//        if (StringUtils.isBlank(serviceOrderActivityUseVO.getActivityOrderCode())) {
//            return "用户营销活动订单号不能为空";
//        }
//        if (StringUtils.isBlank(serviceOrderActivityUseVO.getOrderId())) {
//            return "订单号不能为空";
//        }
//        if (StringUtils.isBlank(serviceOrderActivityUseVO.getCustomerId())) {
//            return "客户ID不能为空";
//        }
//        if (StringUtils.isBlank(serviceOrderActivityUseVO.getStoreId())) {
//            return "门店ID不能为空";
//        }
//        if (StringUtils.isBlank(serviceOrderActivityUseVO.getTenantId())) {
//            return "租户ID不能为空";
//        }
//        if (StringUtils.isBlank(serviceOrderActivityUseVO.getCompanyId())) {
//            return "公司ID不能为空";
//        }
//        return null;
//    }

    @Override
    public PageInfo<SimpleActivityCustomerResp> listActivityCustomer(ActivityCustomerListReq activityCustomerListReq) {
        log.info("查询客户营销活动订单列表请求request：{}", JSONObject.toJSONString(activityCustomerListReq));
        if (StringUtils.isBlank(activityCustomerListReq.getActivityCode())) {
            throw new MarketingException("营销活动编码不能为空");
        }
        PageInfo<SimpleActivityCustomerResp> activityCustomerRespPageInfo = new PageInfo<>();
        ActivityCustomerExample activityCustomerExample = new ActivityCustomerExample();
        ActivityCustomerExample.Criteria activityCustomerExampleCriteria = activityCustomerExample.createCriteria();
        if (StringUtils.isNotBlank(activityCustomerListReq.getSearch())) {
            //如果输入了姓名或手机号查询
            CustomerSearchVO customerSearchVO = new CustomerSearchVO();
            customerSearchVO.setSearch(activityCustomerListReq.getSearch());
            customerSearchVO.setStoreId(activityCustomerListReq.getStoreId());
            customerSearchVO.setTenantId(activityCustomerListReq.getTenantId());
            List<CustomerDTO> customerList = iCustomerService.getCustomerListByPhoneOrName(customerSearchVO).getData();
            if (CollectionUtils.isEmpty(customerList)) {
                activityCustomerRespPageInfo.setList(new ArrayList<>());
                return activityCustomerRespPageInfo;
            } else {
                List<String> customerIdList = customerList.stream().map(CustomerDTO::getId).collect(Collectors.toList());
                activityCustomerExampleCriteria.andCustomerIdIn(customerIdList);
            }
        }
        activityCustomerExampleCriteria.andStoreIdEqualTo(activityCustomerListReq.getStoreId());
        activityCustomerExampleCriteria.andActivityCodeEqualTo(activityCustomerListReq.getActivityCode());
        activityCustomerExample.setOrderByClause("create_time desc");
        PageHelper.startPage(activityCustomerListReq.getPageNum() + 1, activityCustomerListReq.getPageSize());
        List<ActivityCustomer> activityCustomerList = activityCustomerMapper.selectByExample(activityCustomerExample);
        PageInfo<ActivityCustomer> activityCustomerPageInfo = new PageInfo<>(activityCustomerList);
        BeanUtils.copyProperties(activityCustomerPageInfo, activityCustomerRespPageInfo);
        if (CollectionUtils.isEmpty(activityCustomerList)) {
            activityCustomerRespPageInfo.setList(new ArrayList<>());
            return activityCustomerRespPageInfo;
        }
        List<String> customerIdList = activityCustomerList.stream().map(ActivityCustomer::getCustomerId).collect(Collectors.toList());
        BaseIdsReqVO customrIdsReq = new BaseIdsReqVO();
        customrIdsReq.setId(customerIdList);
        List<CustomerDTO> customerList = iCustomerService.getCustomerListByIdList(customrIdsReq).getData();
        if (null == customerList) {
            customerList = new ArrayList<>();
        }
        Map<String, CustomerDTO> customerMap = customerList.stream().collect(Collectors.toMap(CustomerDTO::getId, customer -> customer));
        List<SimpleActivityCustomerResp> simpleActivityCustomerRespList = new ArrayList<>();
        for (ActivityCustomer activityCustomer : activityCustomerList) {
            SimpleActivityCustomerResp simpleActivityCustomerResp = new SimpleActivityCustomerResp();
            BeanUtils.copyProperties(activityCustomer, simpleActivityCustomerResp);
            String customerId = activityCustomer.getCustomerId();
            CustomerDTO customer = customerMap.get(customerId);
            if (null != customer) {
                simpleActivityCustomerResp.setName(customer.getName());
                if (null != activityCustomerListReq.getIsFromClient() && activityCustomerListReq.getIsFromClient()) {
                    simpleActivityCustomerResp.setPhoneNumber(makeupPhoneNumber(customer.getPhoneNumber()));
                } else {
                    simpleActivityCustomerResp.setPhoneNumber(customer.getPhoneNumber());
                }
            }
            simpleActivityCustomerRespList.add(simpleActivityCustomerResp);

            if (activityCustomer.getUseStatus()!=null
                    && activityCustomer.getUseStatus()==0
                    && (new Date()).after(activityCustomer.getEndTime())){
                simpleActivityCustomerResp.setUseStatus(Byte.valueOf("-1"));//已过期
            }
        }
        activityCustomerRespPageInfo.setList(simpleActivityCustomerRespList);
        return activityCustomerRespPageInfo;
    }

    private String makeupPhoneNumber(String src) {
        if (StringUtils.isBlank(src)) {
            return "";
        }
        if (src.length() < 7) {
            return src;
        }
        String start = StringUtils.substring(src, 0, 3);
        String end = StringUtils.substring(src, src.length() - 4, src.length());
        return start.concat("****").concat(end);
    }


    /**
     * 查询活动详情
     * 基本信息，活动商品，是否已参加
     *
     * @param activityId
     * @param storeId
     * @param customerId
     * @return
     */
    @Override
    public ActivityResp getActivityDetailForClient(Long activityId, Long storeId, String customerId) {
        ActivityResp activityResp = this.getActivityDetailById(activityId, storeId);
        if (activityResp != null && StringUtils.isNotBlank(customerId)) {
            List<String> codeList = Lists.newArrayList();
            codeList.add(activityResp.getActivityCode());
            Map<String ,ActivityCustomer> applyedMap = getActivityApplyedMap(codeList, storeId, customerId);
            if (MapUtils.isNotEmpty(applyedMap) && applyedMap.containsKey(activityResp.getActivityCode())) {
                activityResp.setApplyed(true);//已参加该活动
                activityResp.setActivityOrderCode(applyedMap.get(activityResp.getActivityCode()).getActivityOrderCode());
            }
        }
        return activityResp;
    }


    /**
     * 显示1、已启用 2、未过期 3、当前门店
     *
     * @param storeId
     * @return
     */
    @Override
    public List<Activity> getActivityListByStoreId(Long storeId) {
        log.info("车主端查询营销活动列表请求request：{}", JSONObject.toJSONString(storeId));
        ActivityExample activityExample = new ActivityExample();
        ActivityExample.Criteria activityExampleCriteria = activityExample.createCriteria();
        activityExampleCriteria.andStoreIdEqualTo(storeId);
        activityExampleCriteria.andStatusEqualTo(true);
        activityExampleCriteria.andEndTimeGreaterThan(new Date());
        activityExample.setOrderByClause("end_time asc");
        List<Activity> activityList = activityMapper.selectByExample(activityExample);
        return activityList;
    }


    /**
     * 查询用户是否有参加过列表中活动
     *
     * @param codeList
     * @param customerId
     * @return
     */
    private Map<String ,ActivityCustomer> getActivityApplyedMap(List<String> codeList, Long storeId, String customerId) {
        Map<String, ActivityCustomer> resultMap = Maps.newHashMap();
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(codeList) || StringUtils.isBlank(customerId)) {
            return resultMap;
        }

        try {
            ActivityCustomerExample example = new ActivityCustomerExample();
            ActivityCustomerExample.Criteria criteria = example.createCriteria();
            criteria.andCustomerIdEqualTo(customerId);
            if (storeId != null) {
                criteria.andStoreIdEqualTo(storeId);
            }
            criteria.andUseStatusNotEqualTo((byte)2);//非取消
            criteria.andActivityCodeIn(codeList);
            List<ActivityCustomer> list = activityCustomerMapper.selectByExample(example);

            if (org.apache.commons.collections4.CollectionUtils.isEmpty(list)) {
                return resultMap;
            }

            list.forEach(activityCustomer -> {
                resultMap.put(activityCustomer.getActivityCode(), activityCustomer);
            });
        } catch (Exception e) {
            log.error("getActivityApplyedMap error:", e);
        }

        return resultMap;
    }

    @Override
    public ActivityCustomerPageResp getMyActivityList(ActivityCustomerListRequest request) {
        ActivityCustomerPageResp activityCustomerPageResp = new ActivityCustomerPageResp();
        Page<ActivityCustomerItem> activityCustomerItemsPage = new Page<>();
        activityCustomerPageResp.setActivityCustomerItems(activityCustomerItemsPage);

        List<ActivityCustomer> activityCustomers = getMyActivityCustomersForClient(request);
        List<ActivityCustomerItem> itemList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(activityCustomers)) {
            HashSet<String> activityCodeList = Sets.newHashSet();
            activityCustomers.forEach(activityCustomer -> {
                activityCodeList.add(activityCustomer.getActivityCode());
                ActivityCustomerItem item = new ActivityCustomerItem();
                BeanUtils.copyProperties(activityCustomer, item);
                itemList.add(item);
            });
            Map<String, Activity> activityMap = getActivityMapByCodeList(new ArrayList<String>(activityCodeList), request.getStoreId());
            itemList.forEach(item -> {
                item.setActivity(activityMap.get(item.getActivityCode()));
                if (item.getUseStatus() == 0 && item.getEndTime().before(new Date())) {//已过期
                    item.setUseStatus((byte) -1);
                }
            });
        }
        activityCustomerItemsPage.addAll(itemList);

        PageInfo<ActivityCustomer> activityCustomerPageInfo = new PageInfo<>(activityCustomers);
        ActivityCustomerPageResp.PageInfo pageInfo = new ActivityCustomerPageResp.PageInfo();
        pageInfo.setTotal(activityCustomerPageInfo.getTotal());
        pageInfo.setPages(activityCustomerPageInfo.getPages());
        pageInfo.setPageNum(request.getPageNum());
        pageInfo.setPageSize(request.getPageSize());
        activityCustomerPageResp.setPageInfo(pageInfo);
        if (pageInfo.getPageNum() >= pageInfo.getPages()) {
            activityCustomerPageResp.setActivityCustomerItems(null);
        }

        return activityCustomerPageResp;
    }

    @Override
    public String getQrCodeForActivity(ActivityQrCodeRequest request) {
        Activity activity = activityMapper.selectByPrimaryKey(request.getActivityId());
        if (activity == null) {
            return null;
        }
        if (StringUtils.isNotBlank(activity.getWeixinQrUrl())) {
            return activity.getWeixinQrUrl();
        }

        String qrUrl = miniAppService.getQrCodeUrl(request.getScene(), request.getPath(), request.getWidth());

        /*
          保存url到activity表
        */
        saveQrUrlToDatabase(request.getActivityId(), qrUrl);

        return qrUrl;
    }

    /**
     * 保存二维码图片url到数据库
     *
     * @param activityId
     * @param qrUrl
     */
    private void saveQrUrlToDatabase(Long activityId, String qrUrl) {
        try {
            if (StringUtils.isNotBlank(qrUrl)) {
                Activity record = new Activity();
                record.setId(activityId);
                record.setWeixinQrUrl(qrUrl);
                int result = activityMapper.updateById(record);
            }
        } catch (Exception e) {
            log.error("saveQrUrlToDatabase error:", e);
        }
    }

    private List<ActivityCustomer> getMyActivityCustomersForClient(ActivityCustomerListRequest request) {
        ActivityCustomerExample example = new ActivityCustomerExample();
        ActivityCustomerExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(request.getCustomerId());
        criteria.andStoreIdEqualTo(request.getStoreId());
//        使用状态0:   未核销1：已核销2：已取消3：已开单（控制开单按钮展示）
        if (request.getStatus() != null) {
            switch (request.getStatus()) {
                case 0://0:   待消费
                    criteria.andUseStatusEqualTo((byte) 0);
                    criteria.andEndTimeGreaterThanOrEqualTo(new Date());
                    break;
                case 1:
                    List<Byte> values = Lists.newArrayList();
                    values.add((byte) 1);
                    values.add((byte) 3);
                    criteria.andUseStatusIn(values);
                    break;//1：已消费
                case 2:
                    criteria.andUseStatusEqualTo((byte) 0);
                    criteria.andEndTimeLessThan(new Date());
                    break;//2：已过期
            }
        }
        example.setOrderByClause("create_time desc");
        PageHelper.startPage(request.getPageNum() + 1, request.getPageSize());
        List<ActivityCustomer> activityCustomers = activityCustomerMapper.selectByExample(example);
        return activityCustomers;
    }


    private Map<String, Activity> getActivityMapByCodeList(List<String> codeList, Long storeId) {
        ActivityExample example = new ActivityExample();
        ActivityExample.Criteria criteria = example.createCriteria();
        criteria.andStoreIdEqualTo(storeId);
        criteria.andActivityCodeIn(codeList);
        List<Activity> activityList = activityMapper.selectByExample(example);
        Map<String, Activity> map = Maps.newHashMap();
        if (CollectionUtils.isEmpty(activityList)) {
            return map;
        }
        activityList.forEach(activity -> {
            map.put(activity.getActivityCode(), activity);
        });
        return map;
    }

    @Override
    public Map<String, Object> getActivityStatistics(Long activityId, Long storeId) {
        log.info("查询营销活动数据请求activityId：{}, storeId: {}", activityId, storeId);
        if (null == activityId || activityId.compareTo(0L) <= 0) {
            throw new MarketingException("入参无效");
        }
        ActivityResp activityResp = this.getActivityDetailById(activityId, storeId);
        if (null == activityResp) {
            //禁止查询非本门店的营销活动
            throw new MarketingException("活动不存在");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("activityCode", activityResp.getActivityCode());
        result.put("applyCount", activityResp.getApplyCount());//报名数

        //统计已核销的人数
        int writeOffCount = getCountOfActivityItemByCodeAndUseStatus(activityResp.getActivityCode(), (byte) 1);
        int orderCount = getCountOfActivityItemByCodeAndUseStatus(activityResp.getActivityCode(), (byte) 3);
        writeOffCount += orderCount;
        result.put("writeOffCount", Integer.valueOf(writeOffCount));//核销数
        result.put("orderCount", Integer.valueOf(orderCount));//开单数
        //计算活动金额
        List<ActivityItemResp> itemRespList = activityResp.getItems();
        BigDecimal singleActivityAmount = BigDecimal.ZERO;
        if (null == itemRespList || CollectionUtils.isEmpty(itemRespList)){
            if (null != activityResp.getActivityPrice()){
                singleActivityAmount = activityResp.getActivityPrice();
            }
        }else {
            for (ActivityItemResp activityItemResp : itemRespList) {
                singleActivityAmount = singleActivityAmount.add(new BigDecimal(activityItemResp.getActualPrice()).multiply(new BigDecimal(activityItemResp.getItemQuantity())));
            }
        }
        //收入合计
        result.put("totalAmount", singleActivityAmount.multiply(new BigDecimal(writeOffCount)));
        //开单金额
        result.put("orderAmount", singleActivityAmount.multiply(new BigDecimal(orderCount)));
        //查询访问数据
        ClientEventRecordVO clientEventRecordVO = new ClientEventRecordVO();
        clientEventRecordVO.setStoreId(String.valueOf(storeId));
        clientEventRecordVO.setContentType("activity");
        clientEventRecordVO.setContentValue(activityResp.getEncryptedCode());
        List<String> eventTypes = Arrays.stream(EventTypeEnum.values()).map(EventTypeEnum::getCode).collect(Collectors.toList());
        clientEventRecordVO.setEventTypes(eventTypes);
        try {
            Map<String, ClientEventRecordDTO> clientEventRecordDTOMap = iClientEventRecordService.getClientEventRecordStatisticsByEvent(clientEventRecordVO);
            if (MapUtils.isNotEmpty(clientEventRecordDTOMap)) {
                ClientEventRecordDTO visitRecord = clientEventRecordDTOMap.get(EventTypeEnum.VISIT.getCode());
                if (null != visitRecord) {
                    result.put("visitUserCount", visitRecord.getUserCount());//访问用户数
                    result.put("visitCount", visitRecord.getEventCount());//活动访问数
                } else {
                    result.put("visitUserCount", Long.valueOf(0));//访问用户数
                    result.put("visitCount", Long.valueOf(0));//活动访问数
                }
                ClientEventRecordDTO forwardRecord = clientEventRecordDTOMap.get(EventTypeEnum.WECHATFORWARD.getCode());
                if (null != forwardRecord) {
                    result.put("wechatForwardCount", forwardRecord.getEventCount());//活动转发数
                } else {
                    result.put("wechatForwardCount", Long.valueOf(0));//活动转发数
                }
                //通过登录新增的客户数
                ClientEventRecordDTO loginRecord = clientEventRecordDTOMap.get(EventTypeEnum.LOGIN.getCode());
                Long loginUserCount = null;
                if (null != loginRecord) {
                    loginUserCount = loginRecord.getUserCount();
                }
                if (null == loginUserCount) {
                    loginUserCount = 0L;
                }
                //通过注册新增的客户数
                Long registeredUserCount = null;
                ClientEventRecordDTO registeredRecord = clientEventRecordDTOMap.get(EventTypeEnum.REGISTERED.getCode());
                if (null != registeredRecord) {
                    registeredUserCount = registeredRecord.getUserCount();
                }
                if (null == registeredUserCount) {
                    registeredUserCount = 0L;
                }
                result.put("newUserCount", loginUserCount + registeredUserCount);//新增客户数
            } else {
                result.put("visitUserCount", Long.valueOf(0));//访问用户数
                result.put("visitCount", Long.valueOf(0));//活动访问数
                result.put("wechatForwardCount", Long.valueOf(0));//活动转发数
                result.put("newUserCount", Long.valueOf(0));//新增客户数
            }
        } catch (Exception e) {
            log.error("查询营销活动数据远程接口异常", e);
            result.put("visitUserCount", Long.valueOf(0));//访问用户数
            result.put("visitCount", Long.valueOf(0));//活动访问数
            result.put("wechatForwardCount", Long.valueOf(0));//活动转发数
            result.put("newUserCount", Long.valueOf(0));//新增客户数
        }
        return result;
    }

//    @Override
//    public int updateActivityCustomerForOrder(ActivityCustomerRpcVO activityCustomerRpcVO) {
//        log.info("查询营销活动详情：{}", JSONObject.toJSONString(activityCustomerRpcVO));
//        ActivityCustomerExample example = new ActivityCustomerExample();
//        ActivityCustomerExample.Criteria criteria = example.createCriteria();
//        criteria.andActivityCodeEqualTo(activityCustomerRpcVO.getActivityCode());
//        criteria.andStoreIdEqualTo(Long.parseLong(activityCustomerRpcVO.getStoreId()));
//        criteria.andCustomerIdEqualTo(activityCustomerRpcVO.getCustomerId());
//        criteria.andUseStatusEqualTo(activityCustomerRpcVO.getUseStatus());
//        List<ActivityCustomer> activityCustomerList = activityCustomerMapper.selectByExample(example);
//        if (CollectionUtils.isEmpty(activityCustomerList)) {
//            throw new CrmException("查询营销活动异常");
//        }
//        ActivityCustomer activityCustomer = activityCustomerList.get(0);
//        // 修改为已开单
//        activityCustomer.setUseStatus((byte)3);
//        activityCustomer.setServiceOrderId(activityCustomerRpcVO.getServiceOrderId());
//        activityCustomer.setServiceOrderTime(new Date());
//        int rst = activityCustomerMapper.updateByPrimaryKeySelective(activityCustomer);
//        log.info("营销活动id：{}, 回写：{}", activityCustomer.getId(), rst > 0 ? "成功" : "失败");
//        return rst;
//    }

//    @Override
//    public ActivityCustomerDTO getActivityCustomer(ActivityCustomerRpcVO activityCustomerRpcVO) {
//        ActivityCustomerDTO dto = null;
//        ActivityCustomerExample example = new ActivityCustomerExample();
//        ActivityCustomerExample.Criteria criteria = example.createCriteria();
//        criteria.andStoreIdEqualTo(Long.parseLong(activityCustomerRpcVO.getStoreId()));
//        criteria.andCustomerIdEqualTo(activityCustomerRpcVO.getCustomerId());
//        criteria.andServiceOrderIdEqualTo(activityCustomerRpcVO.getServiceOrderId());
//        criteria.andUseStatusEqualTo(activityCustomerRpcVO.getUseStatus());
//        List<ActivityCustomer> activityCustomers = activityCustomerMapper.selectByExample(example);
//        if (CollectionUtils.isNotEmpty(activityCustomers)) {
//            dto = new ActivityCustomerDTO();
//            try {
//                BeanUtils.copyProperties(activityCustomers.get(0), dto);
//            } catch (Exception e) {
//                log.info("获取营销活动异常", e);
//            }
//        }
//
//        return dto;
//    }

//    @Override
//    public int deleteActivityCustomer(ActivityCustomerRpcVO activityCustomerRpcVO) {
//        ActivityCustomerExample example = new ActivityCustomerExample();
//        ActivityCustomerExample.Criteria criteria = example.createCriteria();
//        criteria.andStoreIdEqualTo(Long.parseLong(activityCustomerRpcVO.getStoreId()));
//        criteria.andCustomerIdEqualTo(activityCustomerRpcVO.getCustomerId());
//        criteria.andActivityCodeEqualTo(activityCustomerRpcVO.getActivityCode());
//        criteria.andServiceOrderIdEqualTo(activityCustomerRpcVO.getServiceOrderId());
//        criteria.andUseStatusEqualTo(activityCustomerRpcVO.getUseStatus());
//        int rst = activityCustomerMapper.deleteByExample(example);
//        return rst;
//    }

    //营销版
    @Override
    public AddActivityRequest addActivity(AddActivityRequest addActivityReq) {
        log.info("新增营销活动，request={}", JSONObject.toJSONString(addActivityReq));
        //校验输入
        String validateResult = null;
        if (null == addActivityReq) {
            validateResult = CrmReturnCodeEnum.REQUEST_ARG_IS_EMPTY.getDesc();
        }
        Long storeId = addActivityReq.getStoreId();
        if (null == storeId || storeId.compareTo(0L) <= 0) {
            validateResult = "门店ID无效";
        }
        //营销活动
        if (null == addActivityReq.getType()) {
            addActivityReq.setType((byte) 0);
        }
        //报名人数限制
        Long applyNumber = addActivityReq.getApplyNumber();
        if (null == applyNumber) {
            applyNumber = Long.valueOf(-1L);
            addActivityReq.setApplyNumber(applyNumber);
        }
        if (applyNumber.compareTo(0L) < 0 && !applyNumber.equals(-1L)) {
            validateResult = "报名人数限制只能为不限或限制（正整数）";
        }
        //付款方式：线上报名，到店付款
        if (null == addActivityReq.getPayType()) {
            addActivityReq.setPayType(Boolean.FALSE);
        }
        //活动开始时间
        if (null == addActivityReq.getStartTime()) {
            validateResult = "活动开始时间不能为空";
        }
        if (null == addActivityReq.getEndTime()) {
            validateResult = "活动结束时间不能为空";
        }
        Date startTime = DataTimeUtil.getDateStartTime(addActivityReq.getStartTime());
        Date endTime = DataTimeUtil.getDateZeroTime(addActivityReq.getEndTime());
        if (endTime.compareTo(new Date()) <= 0) {
            validateResult = "活动结束时间不能早于当前时间";
        }
        if (endTime.compareTo(startTime) <= 0) {
            validateResult = "活动结束时间不能早于活动开始时间";
        }
        if (CollectionUtils.isEmpty(addActivityReq.getContents())){
            validateResult = "活动内容不能为空";
        }
        Long activityTemplateId = addActivityReq.getActivityTemplateId();
        if (null != activityTemplateId && activityTemplateId.compareTo(0L) <= 0) {
            validateResult = "营销活动模板ID无效";
        } else if (null != activityTemplateId) {
//            ActivityTemplate activityTemplate = activityTemplateMapper.selectByPrimaryKey(activityTemplateId);
//            if (null == activityTemplate || Boolean.FALSE.equals(activityTemplate.getStatus())) {
//                validateResult = "营销活动模板无效";
//            }
        }
        //校验名称
        List<Activity> activityList = this.getActivityByTitle(addActivityReq.getActivityTitle(), storeId);
        if (CollectionUtils.isNotEmpty(activityList)) {
            validateResult = "已存在同名的营销活动";
        }
        if (!checkStoreInfo(storeId, addActivityReq.getTenantId(), Long.valueOf(addActivityReq.getCompanyId()))) {
            validateResult = "门店信息未完善";
        }

        if (null != validateResult) {
            throw new MarketingException(validateResult);
        }
        Activity activity = new Activity();
        BeanUtils.copyProperties(addActivityReq, activity);
        //新建默认上架
        activity.setStatus(Boolean.TRUE);
        Date date = new Date();
        activity.setCreateTime(date);
        activity.setUpdateTime(date);
        activity.setUpdateUser(addActivityReq.getCreateUser());
        activity.setStartTime(DataTimeUtil.getDateStartTime(activity.getStartTime()));
        activity.setEndTime(DataTimeUtil.getDateZeroTime(activity.getEndTime()));
        activity.setActivityContent(JSONObject.toJSONString(addActivityReq.getContents()));

        //生成营销活动编码
        String codeNumber = codeFactory.getCodeNumber(CodeFactory.activityRedisPrefix, storeId);
        String code = codeFactory.generateActivityCode(storeId, codeNumber);
        activity.setActivityCode(code);
        //生成营销活动编码的密文
        String encryptedCode = Md5Util.md5(code, CodeFactory.codeSalt);
        activity.setEncryptedCode(encryptedCode);
        activityMapper.insertSelective(activity);
        //则缓存报名人数
        String key = activityApplyCountPrefix.concat(code);
        redisTemplate.opsForValue().increment(key, 0L);
        redisTemplate.opsForValue().increment(activityOrderCountPrefix.concat(code), 0L);

        if (null != activity.getActivityTemplateId()) {
            //模板引用次数+1
//            activityTemplateMapper.referById(activity.getActivityTemplateId());
        }
        return addActivityReq;
    }

    //营销版
    @Override
    public ActivityResponse getActivityById(Long activityId, Long storeId) {
        log.info("查询营销活动详情请求activityId：{}, storeId: {}", activityId, storeId);
        if (null == activityId || activityId.compareTo(0L) <= 0) {
            return null;
        }
        Activity activity = activityMapper.selectByPrimaryKey(activityId);
        if (null == activity || !storeId.equals(activity.getStoreId())) {
            //禁止查询非本门店的营销活动
            return null;
        }
        ActivityResponse activityResp = new ActivityResponse();
        BeanUtils.copyProperties(activity, activityResp);
        activityResp.setContents(JSONObject.parseArray(activity.getActivityContent(), ActivityContent.class));
        //统计已报名的人数
        ActivityCustomerExample customerExample = new ActivityCustomerExample();
        ActivityCustomerExample.Criteria customerExampleCriteria = customerExample.createCriteria();
        customerExampleCriteria.andActivityCodeEqualTo(activity.getActivityCode());
        customerExampleCriteria.andUseStatusNotEqualTo((byte) 2);
        int count = activityCustomerMapper.countByExample(customerExample);
        activityResp.setApplyCount(Long.valueOf(count));
        return activityResp;
    }

    //营销版
    @Override
    public PageInfo<ActivityResponse> list(ActivityListReq activityListReq){
        log.info("查询营销活动列表请求request：{}", JSONObject.toJSONString(activityListReq));
        PageInfo<ActivityResponse> activityRespPageInfo = new PageInfo<>();
        ActivityExample activityExample = new ActivityExample();
        ActivityExample.Criteria activityExampleCriteria = activityExample.createCriteria();
        activityExampleCriteria.andStoreIdEqualTo(activityListReq.getStoreId());
        if (StringUtils.isNotBlank(activityListReq.getTitle())) {
            activityExampleCriteria.andActivityTitleLike("%" + activityListReq.getTitle() + "%");
        }
        Integer dateStatus = activityListReq.getDateStatus();
        if (null != dateStatus) {
            Date date = new Date();
            //0.未开始
            switch (dateStatus.intValue()) {
                case 0:
                    activityExampleCriteria.andStartTimeGreaterThan(date);
                    break;
                case 1:
                    activityExampleCriteria.andStartTimeLessThanOrEqualTo(date);
                    activityExampleCriteria.andEndTimeGreaterThanOrEqualTo(date);
                    break;
                case 2:
                    activityExampleCriteria.andEndTimeLessThan(date);
                    break;
                default:
                    break;
            }
        }
        activityExample.setOrderByClause("create_time desc");
        PageHelper.startPage(activityListReq.getPageNum() + 1, activityListReq.getPageSize());
        List<Activity> activityList = activityMapper.selectByExample(activityExample);
        PageInfo<Activity> activityPageInfo = new PageInfo<>(activityList);
        BeanUtils.copyProperties(activityPageInfo, activityRespPageInfo);
        if (CollectionUtils.isEmpty(activityList)) {
            activityRespPageInfo.setList(new ArrayList<>());
            return activityRespPageInfo;
        }
        //营销活动编码集合
        List<String> activityCodeList = activityList.stream().map(Activity::getActivityCode).collect(Collectors.toList());
        //查询营销活动报名情况
        List<Map<String, Object>> applyCountList = activityCustomerMapper.countByActivityCodeAndUseStatus(activityCodeList, Lists.newArrayList(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(3)));
        Map<String, Long> applyCountMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(applyCountList)) {
            applyCountList.forEach(applyCountElement -> {
                Object activityCodeObj = applyCountElement.get("activityCode");
                if (null != activityCodeObj) {
                    String activityCode = String.valueOf(activityCodeObj);
                    Object applyCountObj = applyCountElement.get("number");
                    Long applyCount = 0L;
                    if (null != applyCountObj) {
                        applyCount = Long.valueOf(String.valueOf(applyCountObj));
                    }
                    applyCountMap.put(activityCode, applyCount);
                }
            });
        }
        //查询营销活动核销情况
        List<Map<String, Object>> writeOffCountList = activityCustomerMapper.countByActivityCodeAndUseStatus(activityCodeList, Lists.newArrayList(Integer.valueOf(1), Integer.valueOf(3)));
        Map<String, Long> writeOffCountMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(writeOffCountList)) {
            writeOffCountList.forEach(writeOffCountElement -> {
                Object activityCodeObj = writeOffCountElement.get("activityCode");
                if (null != activityCodeObj) {
                    String activityCode = String.valueOf(activityCodeObj);
                    Object applyCountObj = writeOffCountElement.get("number");
                    Long applyCount = 0L;
                    if (null != applyCountObj) {
                        applyCount = Long.valueOf(String.valueOf(applyCountObj));
                    }
                    writeOffCountMap.put(activityCode, applyCount);
                }
            });
        }
        List<ActivityResponse> activityRespList = new ArrayList<>();
        activityList.forEach(activity -> {
            ActivityResponse activityResp = new ActivityResponse();
            BeanUtils.copyProperties(activity, activityResp);
            activityResp.setContents(JSONObject.parseArray(activity.getActivityContent(),ActivityContent.class));
            activityResp.setApplyCount(applyCountMap.get(activity.getActivityCode()));
            activityResp.setWriteOffCount(writeOffCountMap.get(activity.getActivityCode()));
            activityResp.setDateStatus(getDataStatusByStartTimeAndEndTime(activity));
            //如果活动已过期,则删除缓存的报名人数
            if (activity.getEndTime().compareTo(new Date()) <= 0) {
                String key = activityApplyCountPrefix + activity.getActivityCode();
                redisTemplate.delete(key);
            }
            activityRespList.add(activityResp);
        });
        activityRespPageInfo.setList(activityRespList);
        return activityRespPageInfo;
    }

    //营销版
//    @Override
//    public EditActivityRequest edit(EditActivityRequest editActivityReq){
//        log.info("编辑营销活动，request={}", JSONObject.toJSONString(editActivityReq));
//        if (null == editActivityReq) {
//            throw new CrmException(CrmReturnCodeEnum.REQUEST_ARG_IS_EMPTY.getDesc());
//        }
//        Long activityId = editActivityReq.getId();
//        if (null == activityId || activityId.compareTo(0L) <= 0) {
//            throw new CrmException("营销活动ID不能为空");
//        }
//        ActivityResponse oldActivity = this.getActivityById(activityId, editActivityReq.getStoreId());
//        if (null == oldActivity) {
//            throw new CrmException("营销活动不存在");
//        }
//        Date date = new Date();
//        if (oldActivity.getStartTime().compareTo(date) <= 0) {
//            throw new CrmException("活动过开始时间，不允许编辑");
//        }
//
//        //校验输入
//        String validateResult = null;
//        Long storeId = editActivityReq.getStoreId();
//        if (null == storeId || storeId.compareTo(0L) <= 0) {
//            validateResult = "门店ID无效";
//        }
//        //营销活动
//        if (null == editActivityReq.getType()) {
//            editActivityReq.setType((byte) 0);
//        }
//        //报名人数限制
//        Long applyNumber = editActivityReq.getApplyNumber();
//        if (null == applyNumber) {
//            applyNumber = Long.valueOf(-1L);
//            editActivityReq.setApplyNumber(applyNumber);
//        }
//        if (applyNumber.compareTo(0L) < 0 && !applyNumber.equals(-1L)) {
//            validateResult = "报名人数限制只能为不限或限制（正整数）";
//        }
//        //报名人数限制不能小于已报名人数
//        Long applyCount = oldActivity.getApplyCount();
//        if (null == applyCount) {
//            applyCount = 0L;
//        }
//        if (!applyNumber.equals(-1L) && applyNumber.compareTo(applyCount) < 0) {
//            validateResult = "报名人数限制不能小于已报名人数";
//        }
//        if (null == editActivityReq.getPayType()) {
//            editActivityReq.setPayType(Boolean.FALSE);
//        }
//        //活动开始时间
//        if (null == editActivityReq.getStartTime()) {
//            validateResult = "活动开始时间不能为空";
//        }
//        if (null == editActivityReq.getEndTime()) {
//            validateResult = "活动结束时间不能为空";
//        }
//        Date startTime = DataTimeUtil.getDateStartTime(editActivityReq.getStartTime());
//        Date endTime = DataTimeUtil.getDateZeroTime(editActivityReq.getEndTime());
//
//        if (endTime.compareTo(new Date()) <= 0) {
//            validateResult = "活动结束时间不能早于当前时间";
//        }
//        if (endTime.compareTo(startTime) <= 0) {
//            validateResult = "活动结束时间不能早于活动开始时间";
//        }
//        if (CollectionUtils.isEmpty(editActivityReq.getContents())){
//            validateResult = "活动内容不能为空";
//        }
//        Long activityTemplateId = editActivityReq.getActivityTemplateId();
//        if (null != activityTemplateId && activityTemplateId.compareTo(0L) <= 0) {
//            validateResult = "营销活动模板ID无效";
//        } else if (null != activityTemplateId) {
////            ActivityTemplate activityTemplate = activityTemplateMapper.selectByPrimaryKey(activityTemplateId);
////            if (null == activityTemplate || Boolean.FALSE.equals(activityTemplate.getStatus())) {
////                validateResult = "营销活动模板无效";
////            }
//        }
//        Long picActivityTemplateId = editActivityReq.getPicActivityTemplateId();
//        if (null != picActivityTemplateId && picActivityTemplateId.compareTo(0L) <= 0) {
//            validateResult = "头图营销活动模板ID无效";
//        } else if (null != picActivityTemplateId) {
////            ActivityTemplate activityTemplate = activityTemplateMapper.selectByPrimaryKey(picActivityTemplateId);
////            if (null == activityTemplate || Boolean.FALSE.equals(activityTemplate.getStatus())) {
////                validateResult = "头图营销活动模板无效";
////            }
//        }
//        //校验名称
//        if (!editActivityReq.getActivityTitle().equals(oldActivity.getActivityTitle())) {
//            List<Activity> activityList = this.getActivityByTitle(editActivityReq.getActivityTitle(), storeId);
//            if (CollectionUtils.isNotEmpty(activityList)) {
//                for (Activity activity : activityList) {
//                    if (!activity.getId().equals(editActivityReq.getId())) {
//                        validateResult = "已存在同名的营销活动";
//                    }
//                }
//            }
//        }
//        if (!checkStoreInfo(storeId, editActivityReq.getTenantId(), Long.valueOf(editActivityReq.getCompanyId()))) {
//            validateResult = "门店信息未完善";
//        }
//
//        if (null != validateResult) {
//            throw new CrmException(validateResult);
//        }
//
//        Activity activity = new Activity();
//        BeanUtils.copyProperties(oldActivity, activity);
//        if (null != editActivityReq.getType()) {
//            activity.setType(editActivityReq.getType());
//        }
//        if (null != editActivityReq.getPicUrl()) {
//            activity.setPicUrl(editActivityReq.getPicUrl());
//        }
//        if (null != editActivityReq.getActivityTitle()) {
//            activity.setActivityTitle(editActivityReq.getActivityTitle());
//        }
//        if (null != editActivityReq.getActivityIntroduce()) {
//            activity.setActivityIntroduce(editActivityReq.getActivityIntroduce());
//        }
//        activity.setApplyNumber(editActivityReq.getApplyNumber());
//        activity.setHotline(editActivityReq.getHotline());
//        if (null != editActivityReq.getPayType()) {
//            activity.setPayType(editActivityReq.getPayType());
//        }
//        activity.setStartTime(DataTimeUtil.getDateStartTime(editActivityReq.getStartTime()));
//        activity.setEndTime(DataTimeUtil.getDateZeroTime(editActivityReq.getEndTime()));
//        if (null != editActivityReq.getActivityTemplateId()) {
//            activity.setActivityTemplateId(editActivityReq.getActivityTemplateId());
//        }
//        if (null != editActivityReq.getPicActivityTemplateId()) {
//            activity.setPicActivityTemplateId(editActivityReq.getPicActivityTemplateId());
//        }
//        activity.setUpdateTime(new Date());
//        activity.setActivityContent(JSONObject.toJSONString(editActivityReq.getContents()));
//        activity.setActivityPrice(editActivityReq.getActivityPrice());
//
//        if (null != activity.getUpdateTime()) {
//            VersionHelper.checkVersion(activity.getUpdateTime());
//        }
//
//        activityMapper.updateByPrimaryKeySelective(activity);
//        //报名人数缓存
//        String key = activityApplyCountPrefix + activity.getActivityCode();
//        if (StringUtils.isBlank(redisTemplate.opsForValue().get(key))) {
//            redisTemplate.opsForValue().increment(key, 0L);
//        }
//        if (null != activity.getPicActivityTemplateId() && !activity.getPicActivityTemplateId().equals(oldActivity.getPicActivityTemplateId())) {
////            activityTemplateMapper.referById(activity.getPicActivityTemplateId());
//        }
//        return editActivityReq;
//    }

    /**
     * 获取指定状态的客户活动数量
     *
     * @return
     */
    private int getCountOfActivityItemByCodeAndUseStatus(String activityCode, Byte useStatus) {
//        ActivityCustomerExample customerExample = new ActivityCustomerExample();
//        ActivityCustomerExample.Criteria customerExampleCriteria = customerExample.createCriteria();
//        customerExampleCriteria.andActivityCodeEqualTo(activityCode);
//        customerExampleCriteria.andUseStatusEqualTo(useStatus);
//        int count = activityCustomerMapper.countByExample(customerExample);
//        return count;
        return 0;
    }
}
