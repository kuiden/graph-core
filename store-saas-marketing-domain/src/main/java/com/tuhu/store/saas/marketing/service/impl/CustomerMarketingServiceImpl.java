package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdsReqVO;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.enums.SMSTypeEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CustomerMarketingMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.StoreCustomerGroupRelationMapper;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityModel;
import com.tuhu.store.saas.marketing.response.ActivityItemResp;
import com.tuhu.store.saas.marketing.response.ActivityResp;
import com.tuhu.store.saas.marketing.response.ActivityResponse;
import com.tuhu.store.saas.marketing.response.CouponResp;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupDto;
import com.tuhu.store.saas.marketing.service.*;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: ZhangXiao
 * @Description:
 * @Date: Created in 2019/5/24
 * @ProjectName: saas-crm
 * @Version: 1.0.0
 */
@Service
@Slf4j
public class CustomerMarketingServiceImpl implements ICustomerMarketingService {
    @Autowired
    private CustomerMarketingMapper customerMarketingMapper;

    @Autowired
    private IMarketingSendRecordService iMarketingSendRecordService;

    @Autowired
    private IActivityService activityService;

    @Autowired
    private ICouponService couponService;

    @Autowired
    private IMessageQuantityService iMessageQuantityService;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private IMessageTemplateLocalService messageTemplateLocalService;

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Autowired
    private ICustomerGroupService customerGroupService;
    @Autowired
    private StoreCustomerGroupRelationMapper storeCustomerGroupRelationMapper;
   @Autowired
    private IUtilityService iUtilityService;

    @Autowired
    private SeckillActivityService seckillActivityService;

    /* @Value("${.url.pre}")
    private String domainUrlPre;*/

    @Override
    public PageInfo<CustomerMarketing> customerMarketingList(MarketingReq req) {
        String funName = "定向营销任务列表显示";
        log.info("{} -> 请求参数: {}", funName, JSONObject.toJSONString(req));
        PageInfo<CustomerMarketing> pageInfo = new PageInfo<>();
        CustomerMarketingExample customerMarketingExample = new CustomerMarketingExample();
        CustomerMarketingExample.Criteria listCriterion = customerMarketingExample.createCriteria();
        //门店ID过滤
        listCriterion.andTenantIdEqualTo(req.getTenantId()).andStoreIdEqualTo(req.getStoreId());
        //统计分页信息
        Integer total = customerMarketingMapper.countByExample(customerMarketingExample);
        if (total <= 0) {
            pageInfo.setTotal(total);
            pageInfo.setStartRow(req.getPageNum());
            pageInfo.setPageSize(req.getPageSize());
            return pageInfo;
        }
        pageInfo.setTotal(total);
        customerMarketingExample.setOrderByClause("send_time desc, create_time desc");
        PageHelper.startPage(req.getPageNum() + 1, req.getPageSize());
        List<CustomerMarketing> customerMarketingList = customerMarketingMapper.selectByExample(customerMarketingExample);

        List<String> customerMarketingIds = customerMarketingList.stream().map(x->x.getId().toString()).collect(Collectors.toList());

        Map<String,Long> countMap = iMarketingSendRecordService.getMarketingSendRecordCount(customerMarketingIds);

        for(CustomerMarketing customerMarketing : customerMarketingList) {
            customerMarketing.setCustomerNum(countMap.get(customerMarketing.getId().toString()));
        }
        pageInfo.setList(customerMarketingList);
        pageInfo.setStartRow(req.getPageNum());
        pageInfo.setPageSize(req.getPageSize());
        log.info("{} -> 返回响应: {}", funName, JSONObject.toJSONString(pageInfo));

        return pageInfo;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addMarketingCustomer(MarketingAddReq addReq) {
        String funName = "定向营销任务新增";
        log.info("{} -> 请求参数: {}", funName, JSONObject.toJSONString(addReq));
        List<String> customerIds = checkCommonParams(addReq);
        addMarketing(addReq,customerIds);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMarketingCustomerByTaskType(MarketingUpdateReq addReq) {
        String funName = "更新定向营销任务状态";
        log.info("{} -> 请求参数: {}", funName, JSONObject.toJSONString(addReq));
        Long id = addReq.getId();
        CustomerMarketing customerMarketing = this.queryCustomerMarketing(id);
        if (customerMarketing!=null){
            customerMarketing.setTaskType(addReq.getTaskType());
            customerMarketing.setUpdateUser(addReq.getUpdateUser());
            customerMarketing.setUpdateTime(new Date());
            customerMarketing.setRemark(addReq.getRemark());
            customerMarketingMapper.updateByPrimaryKeySelective(customerMarketing);
        }
        log.info("更新定向营销任务状态成功");
    }

    @Override
    public void insert(CustomerMarketing customerMarketing) {
        if (customerMarketing != null) {
            customerMarketing.setCreateTime(new Date());
            customerMarketing.setUpdateTime(new Date());
            String md = customerMarketing.getMessageDatas();
            if (md!=null&&!"".equals(md)){
                String[] strArray = md.split(",");
                List<String> list = Arrays.asList(strArray);
                customerMarketing.setMessageDatas(this.getJson(list));
            }
            customerMarketingMapper.insertSelective(customerMarketing);
        }
    }

    @Override
    public String getSmsPreview(MarketingSmsReq req) {
        log.info("getSmsPreview========>"+ JSON.toJSONString(req));
        String template = "";
        if(req.getMarketingMethod().equals(Byte.valueOf("0"))){

            MessageTemplateLocal messageTemplateLocal = messageTemplateLocalService.getTemplateLocalById(SMSTypeEnum.MARKETING_COUPON.templateCode(),req.getStoreId());
            //短信模板占位符是从{1}开始，所以此处增加一个空串占位{0}
            //【云雀智修】车主您好,{1}优惠券,本店{2}已送到您的手机号,点击查看详情{3},退订回N
            template = messageTemplateLocal.getTemplateContent();

        }else if(req.getMarketingMethod().equals(Byte.valueOf("1"))){

            MessageTemplateLocal messageTemplateLocal = messageTemplateLocalService.getTemplateLocalById(SMSTypeEnum.MARKETING_ACTIVITY.templateCode(),req.getStoreId());
            //短信模板占位符是从{1}开始，所以此处增加一个空串占位{0}
            //【云雀智修】车主您好，{1}，本店{2}邀请您参加{3}活动，点击查看详情：{4},退订回N
            template = messageTemplateLocal.getTemplateContent();
        }

        String paramStr = getMessageData(req.getStoreId(), req.getMarketingMethod(), req.getCouponOrActiveId(),req.getOriginUrl());

        if(StringUtils.isEmpty(paramStr)) {
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"短信参数生成失败");
        }
        log.info("paramStr=========>"+paramStr);
        String[] params = StringUtils.splitByWholeSeparatorPreserveAllTokens(","+paramStr,",");
        log.info("params=========>"+JSON.toJSONString(params));
        return MessageFormat.format(template,params);
    }

    @Override
    public Boolean customerMarketingContainsCoupon(Long couponId, Long tenantId, Long storeId) {

        CustomerMarketingExample customerMarketingExample = new CustomerMarketingExample();
        CustomerMarketingExample.Criteria listCriterion = customerMarketingExample.createCriteria();
        //查询该优惠券建立的定向营销
        listCriterion.andMarketingMethodEqualTo(Byte.valueOf("0"))
                .andCouponIdEqualTo(couponId.toString())
                .andTenantIdEqualTo(tenantId)
                .andStoreIdEqualTo(storeId)
                .andIsDeleteEqualTo(Byte.valueOf("0"));
        //统计分页信息
        Integer total = customerMarketingMapper.countByExample(customerMarketingExample);

        if(total > 0) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 获取短信参数信息
     * @param storeId
     * @param marketingMethod
     * @param couponOrActiveId
     * @return
     */
    private String getMessageData(Long storeId, Byte marketingMethod, String couponOrActiveId,String orginUrl) {
        List<String> params = new ArrayList<>();
        //查询门店信息
        StoreInfoVO storeInfoVO = new StoreInfoVO();
        storeInfoVO.setStoreId(storeId);
        StoreDTO storeDTO = storeInfoClient.getStoreInfo(storeInfoVO).getData();

        if(marketingMethod.equals(Byte.valueOf("0"))){
            //优惠券营销
            CouponResp coupon = couponService.getCouponDetailById(Long.valueOf(couponOrActiveId));
            if (null == coupon || coupon.getId() == null) {
                //禁止查询非本门店的优惠券
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"优惠券不存在");
            }

            //短信模板占位符是从{1}开始，所以此处增加一个空串占位{0}
            //【云雀智修】车主您好,{1}优惠券,本店{2}已送到您的手机号,点击查看详情{3},退订回N
            params.add("价值" + coupon.getContentValue().intValue()+ "元" +coupon.getTitle());
            params.add(storeDTO.getClientAppointPhone());
            //TODO 替换短链
            if(StringUtils.isNotBlank(orginUrl)){
                params.add(setALabel(iUtilityService.getShortUrl(orginUrl)));
            }


        }else if(marketingMethod.equals(Byte.valueOf("1"))){
            //活动营销
            ActivityResp activityResp = activityService.getActivityDetailById(Long.valueOf(couponOrActiveId),storeId);
            if (null == activityResp) {
                //禁止查询非本门店的营销活动
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"活动不存在");
            }

            //算出活动价和原价
            BigDecimal activityPrice = activityResp.getActivityPrice().divide(BigDecimal.valueOf(100),2 ,RoundingMode.HALF_UP);
            BigDecimal srcPrice = new BigDecimal(0);
            List<ActivityItemResp> activityItemResps = activityResp.getItems();
            for(ActivityItemResp activityItemResp : activityItemResps){
//                if(activityItemResp.getGoodsType()){
                    //服务(价格/100)*(时长/100)
                    BigDecimal itemSiglePrice = BigDecimal.valueOf(activityItemResp.getOriginalPrice()).divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP);
                    BigDecimal exeTime = BigDecimal.valueOf(activityItemResp.getItemQuantity());
                    srcPrice = srcPrice.add(itemSiglePrice.multiply(exeTime));
//
//                }else{
//                    //商品 (价格/100)*个数
//                    BigDecimal itemPrice = BigDecimal.valueOf(activityItemResp.getOriginalPrice()).divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(activityItemResp.getItemQuantity()));
//                    srcPrice = srcPrice.add(itemPrice);
//                }
            }
            //短信模板占位符是从{1}开始，所以此处增加一个空串占位{0}
            //【云雀智修】车主您好，{1}，本店{2}邀请您参加{3}活动，点击查看详情：{4},退订回N
            params.add(activityPrice.toString()+"抵"+srcPrice.toString());
            params.add(storeDTO.getClientAppointPhone());
            params.add(activityResp.getActivityTitle());
            //生成短连接
          /*  StringBuffer url = new StringBuffer();
            url.append(domainUrlPre).append("/").append("client/activity/detail?storeId=").append(activityResp.getStoreId()).append("&activityId=").append(activityResp.getId());
            params.add( iUtilityService.getShortUrl(url.toString()));*/
            if(StringUtils.isNotBlank(orginUrl)){
                params.add(setALabel(iUtilityService.getShortUrl(orginUrl)));

            }
        }else if (marketingMethod.equals(Byte.valueOf("2"))) {
            //秒杀活动营销
            SeckillActivityModel seckillActivityModel = seckillActivityService.getSeckillActivityModelById(couponOrActiveId,storeId);
            if (null == seckillActivityModel) {
                //禁止查询非本门店的秒杀活动
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"活动不存在");
            }

            //算出活动价和原价
//            BigDecimal activityPrice = activityResp.getActivityPrice().divide(BigDecimal.valueOf(100),2 ,RoundingMode.HALF_UP);
//            BigDecimal srcPrice = new BigDecimal(0);
//            List<ActivityItemResp> activityItemResps = activityResp.getItems();
//            for(ActivityItemResp activityItemResp : activityItemResps){
////                if(activityItemResp.getGoodsType()){
//                //服务(价格/100)*(时长/100)
//                BigDecimal itemSiglePrice = BigDecimal.valueOf(activityItemResp.getOriginalPrice()).divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP);
//                BigDecimal exeTime = BigDecimal.valueOf(activityItemResp.getItemQuantity());
//                srcPrice = srcPrice.add(itemSiglePrice.multiply(exeTime));
////
////                }else{
////                    //商品 (价格/100)*个数
////                    BigDecimal itemPrice = BigDecimal.valueOf(activityItemResp.getOriginalPrice()).divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(activityItemResp.getItemQuantity()));
////                    srcPrice = srcPrice.add(itemPrice);
////                }
//            }
            BigDecimal activityPrice = seckillActivityModel.getNewPrice() != null ? seckillActivityModel.getNewPrice():new BigDecimal("0");
            BigDecimal srcPrice = seckillActivityModel.getOriginalPrice() != null ? seckillActivityModel.getOriginalPrice():new BigDecimal("0");
            //短信模板占位符是从{1}开始，所以此处增加一个空串占位{0}
            //【云雀智修】车主您好，{1}，本店{2}邀请您参加{3}活动，点击查看详情：{4},退订回N
            params.add(activityPrice.toString()+"抵"+srcPrice.toString());
            params.add(storeDTO.getClientAppointPhone());
            params.add(seckillActivityModel.getActivityTitle());
            //生成短连接
          /*  StringBuffer url = new StringBuffer();
            url.append(domainUrlPre).append("/").append("client/activity/detail?storeId=").append(activityResp.getStoreId()).append("&activityId=").append(activityResp.getId());
            params.add( iUtilityService.getShortUrl(url.toString()));*/
            if(StringUtils.isNotBlank(orginUrl)){
                params.add(setALabel(iUtilityService.getShortUrl(orginUrl)));

            }
        }

        return StringUtils.join(params,",");
    }

    private String setALabel(String shortUrl){
        return "<a href=\"javascript:void(0);\" style=\"color:#1b88ee\">"+shortUrl+"</a>";
    }

    /**
     * 根据id查询对应的客户实体类
     *
     * @param id
     * @return
     */
    private CustomerMarketing queryCustomerMarketing(Long id) {
        CustomerMarketingExample example = new CustomerMarketingExample();
        CustomerMarketingExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        List<CustomerMarketing> list = customerMarketingMapper.selectByExample(example);
        if (list.size() >0) {
            return list.get(0);
        } else {
            return null;
        }
    }


    private String getJson(List<String> params){
        if(params==null || params.isEmpty()){
            return null;
        }
        String paramStr = JSONObject.toJSONString(params);
        return paramStr;
    }

    /**
     * 校验用户数量和短信数量
     * @param addReq
     */
    private List<String>  checkCommonParams(MarketingAddReq addReq) {
        int cNum = 0;
        List<String> customerIds = null;
        if (StringUtils.isNotEmpty(addReq.getCustomerGroupIds())) {
            log.info("客群客户数量");
            String[] groupIds = addReq.getCustomerGroupIds().split(",");
            List<Long> groupList = new ArrayList<>();
            for(int i=0; i<groupIds.length ;i++){
                groupList.add(Long.valueOf(groupIds[i]));
            }
            CalculateCustomerCountReq req = new CalculateCustomerCountReq();
            req.setGroupList(groupList);
            req.setStoreId(addReq.getStoreId());
            req.setTenantId(addReq.getTenantId());
            customerIds = customerGroupService.calculateCustomerCount(req);
            cNum = customerIds.size();
        }else if(StringUtils.isNotEmpty(addReq.getCustomerIds())){
            log.info("指定用户数量");
            String[] strArray = addReq.getCustomerIds().split(",");
            cNum = strArray.length;
            if(cNum>0){
                customerIds = Arrays.asList(strArray);
            }else{
                customerIds = new ArrayList<>();
            }
        }
        //短信可用数量
        Long availableNum = iMessageQuantityService.getStoreMessageQuantity(addReq.getTenantId(), addReq.getStoreId());
        if(availableNum<cNum){
            log.warn("storeId:{} has not enough Sms,need:{},has:{}",addReq.getStoreId(),cNum,availableNum);
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"短信余额不足");
        }
        return customerIds;



    }

    /**
     * 添加活动定向营销
     * @param addReq
     */
    private void addMarketing(MarketingAddReq addReq,List<String> customerIds){

        CouponResp coupon = null;
        ActivityResponse activity = null;
        SeckillActivityModel secKill = new SeckillActivityModel();
        if("0".equals(addReq.getMarketingMethod().toString())){
            Long couponId = Long.valueOf(addReq.getCouponOrActiveId());
            coupon = couponService.getCouponDetailById(couponId);
            if (null == coupon || !addReq.getStoreId().equals(coupon.getStoreId())) {
                //禁止查询非本门店的优惠券
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"优惠券不存在或者不属于本店");
            }
//            if(coupon.getStatus().equals(0)){
//                //优惠券失效
//                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"请将优惠券启用");
//            }
//            if(addReq.getSendTime().after(coupon.getUseEndTime())){
//                //优惠券结束
//                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"优惠券已过期，不能做营销");
//            }

            if(addReq.getSendTime().before(DateUtils.now())){
                //发送时间小于当前时间
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"发送时间小于当前时间");
            }
        }else if ("1".equals(addReq.getMarketingMethod().toString())){
            Long activityId = Long.valueOf(addReq.getCouponOrActiveId());
            activity = activityService.getActivityById(activityId, Long.valueOf(addReq.getStoreId()));
            if (null == activity || !addReq.getStoreId().equals(activity.getStoreId())) {
                //禁止查询非本门店的营销活动
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"活动不存在或者不属于本店");
            }
            if(!activity.getStatus()){
                //活动下架了
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"请将活动上架");
            }
            if(addReq.getSendTime().after(activity.getEndTime())){
                //活动结束了
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"活动已结束，不能做营销");
            }
            if(addReq.getSendTime().before(DateUtils.now())){
                //发送时间小于当前时间
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"发送时间小于当前时间");
            }
            if(addReq.getSendTime().before(activity.getStartTime())){
                //活动还没有开始
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"发送时间不能小于活动开始时间");
            }
        }else if ("2".equals(addReq.getMarketingMethod().toString())){
            secKill = seckillActivityService.getSeckillActivityModelById(addReq.getCouponOrActiveId(), Long.valueOf(addReq.getStoreId()));
            if (null == secKill || !addReq.getStoreId().equals(secKill.getStoreId())) {
                //禁止查询非本门店的营销活动
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"秒杀活动不存在或者不属于本店");
            }
            if(secKill.getStatus() == 9){
                //活动下架了
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"请将秒杀活动上架");
            }
            if(addReq.getSendTime().after(secKill.getEndTime())){
                //活动结束了
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"秒杀活动已结束，不能做营销");
            }
            if(addReq.getSendTime().before(DateUtils.now())){
                //发送时间小于当前时间
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"发送时间小于当前时间");
            }
            if(addReq.getSendTime().before(activity.getStartTime())){
                //活动还没有开始
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"发送时间不能小于秒杀活动开始时间");
            }
        }

        //根据任务中记录的发送对象信息查询出客户列表
        List<CustomerAndVehicleReq> customerList = analyseCustomer(addReq, customerIds);
        if(CollectionUtils.isEmpty(customerList)){
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"发送对象不能为空");
        }
        //如果是优惠券定向营销，需要判断券额度
        if("0".equals(addReq.getMarketingMethod().toString())) {
            Long availableAccount = couponService.getCouponAvailableAccount(coupon.getId(), addReq.getStoreId());

            if(availableAccount >=0 && availableAccount < customerList.size()) {//如果是限量优惠券，需要判断剩余额度
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"优惠券数量不足");
            }
//            //状态禁用的优惠券无法创建定向营销
//            if(coupon.getStatus().equals(0)) {
//                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"优惠券已被禁用");
//            }
        }


        String currentUser = UserContextHolder.getUser()==null?"system":UserContextHolder.getUserName();
        String sendObject = "";
        if(StringUtils.isNotEmpty(addReq.getCustomerGroupIds())) {
            String[] groupIds = addReq.getCustomerGroupIds().split(",");
            List<Long> groupList = new ArrayList<>();
            for(int i=0; i<groupIds.length ;i++){
                groupList.add(Long.valueOf(groupIds[i]));
            }
           /* CalculateCustomerCountReq req = new CalculateCustomerCountReq();
            req.setGroupList(groupList);
            req.setTenantId(addReq.getTenantId());
            req.setStoreId(addReq.getStoreId());
            List<CustomerGroupDto> groups = customerGroupService.getCustomerGroupDto(req);*/
            StoreCustomerGroupRelationExample storeCustomerGroupRelationExample = new StoreCustomerGroupRelationExample();
            StoreCustomerGroupRelationExample.Criteria criteria = storeCustomerGroupRelationExample.createCriteria();
            criteria.andGroupIdIn(groupList);
            criteria.andStoreIdEqualTo(addReq.getStoreId());
            List<StoreCustomerGroupRelation> groups = storeCustomerGroupRelationMapper.selectByExample(storeCustomerGroupRelationExample);
            if(CollectionUtils.isEmpty(groups)) {
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"请选择客群");
            }
            sendObject = StringUtils.join(groups.stream().map(x->x.getGroupName()).collect(Collectors.toList()),",");
        }else {
            sendObject = "指定客户";
        }

        //构造发送任务和发送记录
        CustomerMarketing customerMarketing = new CustomerMarketing();
        BeanUtils.copyProperties(addReq, customerMarketing);
        customerMarketing.setCouponMessageFlag(addReq.isMessageFlag());
        customerMarketing.setCreateTime(DateUtils.now());
        customerMarketing.setCreateUser(currentUser);
        customerMarketing.setUpdateTime(DateUtils.now());
        customerMarketing.setUpdateUser(currentUser);
        customerMarketing.setCustomerGroupId(addReq.getCustomerGroupIds());
        customerMarketing.setCustomerId(addReq.getCustomerIds());
        customerMarketing.setMarketingMethod(addReq.getMarketingMethod());

        customerMarketing.setSendTime(addReq.getSendTime());
        customerMarketing.setRemark(addReq.getRemark());
        customerMarketing.setSendObject(sendObject);//客户群名称
        customerMarketing.setTaskType(Byte.valueOf("0"));
        customerMarketing.setIsDelete(Byte.valueOf("0"));

        //原有字段共用，存放活动相关信息
        if(coupon != null && activity == null) {
            //营销活动模板配置 https://www.yuntongxun.com/member/smsCount/getSmsConfigInfo，存入在message_template_local表
            MessageTemplateLocal messageTemplateLocal = messageTemplateLocalService.getTemplateLocalById(SMSTypeEnum.MARKETING_COUPON.templateCode(),addReq.getStoreId());
            if(messageTemplateLocal==null){
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"不存在优惠券营销短信模板");
            }
            customerMarketing.setMessageTemplate(messageTemplateLocal.getTemplateName());
            //存的是本地的message模板，发送短信时需要单独查询
            customerMarketing.setMessageTemplateId(messageTemplateLocal.getId());
            customerMarketing.setCouponId(coupon.getId().toString());
            customerMarketing.setCouponCode(coupon.getCode());
            customerMarketing.setCouponTitle(coupon.getTitle());
        }else if(coupon == null && activity != null){
            //营销活动模板配置 https://www.yuntongxun.com/member/smsCount/getSmsConfigInfo，存入在message_template_local表
            MessageTemplateLocal messageTemplateLocal = messageTemplateLocalService.getTemplateLocalById(SMSTypeEnum.MARKETING_ACTIVITY.templateCode(),addReq.getStoreId());
            if(messageTemplateLocal==null){
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"不存在活动营销短信模板");
            }
            customerMarketing.setMessageTemplate(messageTemplateLocal.getTemplateName());
            //存的是本地的message模板，发送短信时需要单独查询
            customerMarketing.setMessageTemplateId(messageTemplateLocal.getId());
            customerMarketing.setCouponId(activity.getId().toString());
            customerMarketing.setCouponCode(activity.getActivityCode());
            customerMarketing.setCouponTitle(activity.getActivityTitle());
        }else if (secKill != null) {
            //营销活动模板配置 https://www.yuntongxun.com/member/smsCount/getSmsConfigInfo，存入在message_template_local表
            //秒杀活动模板复用报名活动模板
            MessageTemplateLocal messageTemplateLocal = messageTemplateLocalService.getTemplateLocalById(SMSTypeEnum.MARKETING_ACTIVITY.templateCode(),addReq.getStoreId());
            if(messageTemplateLocal==null){
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"不存在秒杀活动营销短信模板");
            }
            customerMarketing.setMessageTemplate(messageTemplateLocal.getTemplateName());
            //存的是本地的message模板，发送短信时需要单独查询
            customerMarketing.setMessageTemplateId(messageTemplateLocal.getId());
            customerMarketing.setCouponId(secKill.getId());
//            customerMarketing.setCouponCode(activity.getActivityCode());
            customerMarketing.setCouponTitle(secKill.getActivityTitle());
        }

        //messageData
        customerMarketing.setMessageDatas(getMessageData(addReq.getStoreId(),addReq.getMarketingMethod(),addReq.getCouponOrActiveId(),addReq.getOriginUrl()));
        insert(customerMarketing);

        //写入记录表并将状态设为未发送
        List<MarketingSendRecord> records = new ArrayList();
        for(CustomerAndVehicleReq customerAndVehicleReq : customerList){
            MarketingSendRecord marketingSendRecord = new MarketingSendRecord();
            marketingSendRecord.setMarketingId(customerMarketing.getId().toString());
            marketingSendRecord.setCustomerId(customerAndVehicleReq.getCustomerId());
            marketingSendRecord.setCustomerName(customerAndVehicleReq.getCustomerName());
            marketingSendRecord.setPhoneNumber(customerAndVehicleReq.getCustomerPhone());
            marketingSendRecord.setSendType(Byte.valueOf("0"));
            marketingSendRecord.setSendTime(customerMarketing.getSendTime());
            marketingSendRecord.setStoreId(customerMarketing.getStoreId());
            marketingSendRecord.setTenantId(customerMarketing.getTenantId());
            marketingSendRecord.setCreateTime(new Date());
            marketingSendRecord.setCreateUser(currentUser);
            records.add(marketingSendRecord);
        }
        iMarketingSendRecordService.batchInsertMarketingSendRecord(records);

        //如果是优惠券定向营销，需要占用优惠券额度
        if("0".equals(addReq.getMarketingMethod().toString())){
            Coupon couponEntity = new Coupon();
            BeanUtils.copyProperties(coupon, couponEntity);
            couponService.setOccupyNum(couponEntity, customerList.size());
        }
        //占用短信额度
        MessageQuantity select = new MessageQuantity();
        select.setTenantId(addReq.getTenantId());
        select.setStoreId(addReq.getStoreId());
        iMessageQuantityService.setStoreOccupyQuantity(select,Long.valueOf(customerList.size()), currentUser, true);
    }

    /**
     * 获取发送对象中的真实用户信息
     * @param addReq
     * @return
     */
    private List<CustomerAndVehicleReq> analyseCustomer(MarketingAddReq addReq,List<String> customerIdList){
        //根据任务中记录的发送对象信息查询出客户列表
        List<CustomerAndVehicleReq> customeList = new ArrayList();

        List<String> customerIds = new ArrayList<>();
        //客群客户
        if (StringUtils.isNotBlank(addReq.getCustomerGroupIds())){
           /* String[] groupIds = addReq.getCustomerGroupIds().split(",");
            List<Long> groupList = new ArrayList<>();
            for(int i=0; i<groupIds.length ;i++){
                groupList.add(Long.valueOf(groupIds[i]));
            }
            CalculateCustomerCountReq req = new CalculateCustomerCountReq();
            req.setGroupList(groupList);
            req.setStoreId(addReq.getStoreId());
            req.setTenantId(addReq.getTenantId());
            customerIds = customerGroupService.calculateCustomerCount(req);*/
            customerIds  = customerIdList;
            if(CollectionUtils.isEmpty(customerIds)) {
                return customeList;
            }

        }else if(StringUtils.isNotBlank(addReq.getCustomerIds())){
            //指定客户
            String[] strArray = addReq.getCustomerIds().split(",");
            for(int i=0;i<=strArray.length-1;i++){
                customerIds.add(strArray[i]);
            }
        }

        BaseIdsReqVO baseIdsReqVO = new BaseIdsReqVO();
        baseIdsReqVO.setId(customerIds);
        baseIdsReqVO.setStoreId(addReq.getStoreId());
        baseIdsReqVO.setTenantId(addReq.getTenantId());
        List<CustomerDTO> customerDTOS = customerClient.getCustomerByIds(baseIdsReqVO).getData();
        for(CustomerDTO customerDTO : customerDTOS){
            CustomerAndVehicleReq cavReq = new CustomerAndVehicleReq();
            //客户详情
            cavReq.setCustomerId(customerDTO.getId());
            cavReq.setCustomerName(customerDTO.getName());
            cavReq.setCustomerPhone(customerDTO.getPhoneNumber());
            customeList.add(cavReq);
        }
        return customeList;
    }


}
