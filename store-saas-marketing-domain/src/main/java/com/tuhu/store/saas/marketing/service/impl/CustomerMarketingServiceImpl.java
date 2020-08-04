package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdsReqVO;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.enums.SMSTypeEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ActivityMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CustomerMarketingMapper;
import com.tuhu.store.saas.marketing.po.Activity;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.response.ActivityResp;
import com.tuhu.store.saas.marketing.service.*;
import com.tuhu.store.saas.marketing.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    private ActivityMapper activityMapper;

    @Autowired
    private IActivityService activityService;

    @Autowired
    private IMessageQuantityService iMessageQuantityService;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private IMessageQuantityService messageQuantityService;

    @Autowired
    private IMessageTemplateLocalService messageTemplateLocalService;

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
        customerMarketingExample.setOrderByClause("send_time desc");
        PageHelper.startPage(req.getPageNum() + 1, req.getPageSize());
        List<CustomerMarketing> customerMarketingList = customerMarketingMapper.selectByExample(customerMarketingExample);

        pageInfo.setList(customerMarketingList);
        pageInfo.setStartRow(req.getPageNum());
        pageInfo.setPageSize(req.getPageSize());
        log.info("{} -> 返回响应: {}", funName, JSONObject.toJSONString(pageInfo));

        return pageInfo;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketingAddReq addMarketingCustomer(MarketingAddReq addReq) {
        String funName = "定向营销任务新增";
        log.info("{} -> 请求参数: {}", funName, JSONObject.toJSONString(addReq));
        checkCommonParams(addReq);
        String marketingMethod = addReq.getMarketingMethod().toString();
        if(marketingMethod.equals("0")){
            //营销发优惠卷
            //TODO
        }else if(marketingMethod.equals("1")){
            //营销发送活动
            addMarketing4Activity(addReq);
        }
        return addReq;
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
        if(req.getMarketingMethod().equals(Byte.valueOf("0"))){
            //优惠券营销
            //TODO
        }else if(req.getMarketingMethod().equals(Byte.valueOf("1"))){
            //活动营销
            ActivityResp activityResp = activityService.getActivityDetailById(Long.valueOf(req.getCouponOrActiveId()),req.getStoreId());
            if (null == activityResp) {
                //禁止查询非本门店的营销活动
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"活动不存");
            }
            MessageTemplateLocal messageTemplateLocal = messageTemplateLocalService.getTemplateLocalById(SMSTypeEnum.MARKETING_ACTIVITY.templateCode(),req.getStoreId());
            if(messageTemplateLocal==null){
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"不存在活动营销短信模板");
            }
            //算出活动价和原价
            BigDecimal activityPrice = activityResp.getActivityPrice();
            BigDecimal srcPrice = new BigDecimal(123);
//            List<ActivityItemResp> activityItemResps = activityResp.getItems();
//            for(ActivityItemResp activityItemResp : activityItemResps){
//
//            }
            //短信模板占位符是从{1}开始，所以此处增加一个空串占位{0}
            //【云雀智修】车主您好，{1}，本店{2}邀请您参加{3}活动，点击查看详情：{4}
            String template = messageTemplateLocal.getTemplateContent();
            return MessageFormat.format(template,"","1000抵3600","15623675847","新春美容","http://www.baidu.com");
        }
        return null;
    }

    public static void main(String[] agrs){
        String s = "【云雀智修】车主您好，{1}，本店{2}邀请您参加{3}活动，点击查看详情：{4}";
        System.out.println(MessageFormat.format(s,"","123","231","232","233"));
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
    private void checkCommonParams(MarketingAddReq addReq) {
        int cNum = 0;
        if (addReq.getCustomerGroupId()!=null&&!"".equals(addReq.getCustomerGroupId())) {
            log.info("客群客户数量");
//            CustomerGroupParam customerGroupParam = new CustomerGroupParam();
//            customerGroupParam.setId(Long.valueOf(addReq.getCustomerGroupId()));
//            customerGroupParam.setStoreId(addReq.getStoreId());
//            customerGroupParam.setTenantId(addReq.getTenantId());
//            List<Customer> customerList = iMarketingCustomerGroupService.getCustomerByCustomerGroupParam(customerGroupParam);
//            cNum = customerList.size();
        }else if(addReq.getCustomerIds()!=null&&!"".equals(addReq.getCustomerIds())){
            log.info("指定用户数量");
            String[] strArray = addReq.getCustomerIds().split(",");
            cNum = strArray.length;
        }
        //短信可用数量
        MessageQuantity req = new MessageQuantity();
        req.setStoreId(addReq.getStoreId());
        req.setTenantId(addReq.getTenantId());
        req.setCreateUser(UserContextHolder.getUser()==null?"system":UserContextHolder.getUserName());
        MessageQuantity messageQuantity = iMessageQuantityService.selectQuantityByTenantIdAndStoreId(req);
        int mqNum = Integer.parseInt(messageQuantity.getRemainderQuantity().toString());
        if(mqNum<cNum){
            log.warn("storeId:{} has not enough Sms,need:{},has:{}",addReq.getStoreId(),cNum,mqNum);
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"短信余额不足");
        }
    }

    /**
     * 添加活动定向营销
     * @param addReq
     */
    private void addMarketing4Activity(MarketingAddReq addReq){
        Long activityId = Long.valueOf(addReq.getCouponOrActiveId());
        Activity activity = activityMapper.selectByPrimaryKey(activityId);
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
        String currentUser = UserContextHolder.getUser()==null?"system":UserContextHolder.getUserName();
        String sendObject = "";
        //TODO 查询客户群的名称
        //构造发送任务和发送记录
        CustomerMarketing customerMarketing = new CustomerMarketing();
        BeanUtils.copyProperties(addReq, customerMarketing);
        customerMarketing.setCouponMessageFlag(addReq.isMessageFlag());
        customerMarketing.setCreateTime(DateUtils.now());
        customerMarketing.setCreateUser(currentUser);
        customerMarketing.setUpdateTime(DateUtils.now());
        customerMarketing.setUpdateUser(currentUser);
        customerMarketing.setCustomerGroupId(addReq.getCustomerGroupId());
        customerMarketing.setCustomerId(addReq.getCustomerIds());
        customerMarketing.setMarketingMethod(addReq.getMarketingMethod());
        //营销活动模板配置 https://www.yuntongxun.com/member/smsCount/getSmsConfigInfo，存入在message_template_local表
        MessageTemplateLocal messageTemplateLocal = messageTemplateLocalService.getTemplateLocalById(SMSTypeEnum.MARKETING_ACTIVITY.templateCode(),addReq.getStoreId());
        if(messageTemplateLocal==null){
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"不存在活动营销短信模板");
        }
        customerMarketing.setMessageTemplate(messageTemplateLocal.getTemplateName());
        //存的是本地的message模板，发送短信时需要单独查询
        customerMarketing.setMessageTemplateId(messageTemplateLocal.getId());
        customerMarketing.setSendTime(addReq.getSendTime());
        customerMarketing.setRemark(addReq.getRemark());
        customerMarketing.setSendObject(sendObject);//客户群名称
        customerMarketing.setTaskType(Byte.valueOf("0"));
        //messageData
//        customerMarketing.setMessageDatas();
        insert(customerMarketing);
        //根据任务中记录的发送对象信息查询出客户列表
        List<CustomerAndVehicleReq> customeList = analyseCustomer(addReq);
        if(customeList==null||customeList.size()<=0){
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"客户群客户不能为空");
        }
        //写入记录表并将状态设为未发送
        List<MarketingSendRecord> records = new ArrayList();
        for(CustomerAndVehicleReq customerAndVehicleReq : customeList){
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

        MessageQuantity select = new MessageQuantity();
        select.setStoreId(customerMarketing.getStoreId());
        select.setTenantId(customerMarketing.getTenantId());
        //判断剩余短信数量够不够
        MessageQuantity messageQuantity = messageQuantityService.selectQuantityByTenantIdAndStoreId(select);
        if (messageQuantity.getRemainderQuantity() < records.size()) {
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"短信余额不足");
        }
        //更新门店可用短信的数量
        messageQuantity.setUpdateTime(DateUtils.now());
        messageQuantity.setUpdateUser(currentUser);
        messageQuantity.setRemainderQuantity(messageQuantity.getRemainderQuantity() - records.size());
        messageQuantityService.reduceQuantity(messageQuantity);
    }

    private List<CustomerAndVehicleReq> analyseCustomer(MarketingAddReq addReq){
        //根据任务中记录的发送对象信息查询出客户列表
        List<CustomerAndVehicleReq> customeList = new ArrayList();
        //客群客户
        if (StringUtils.isNotBlank(addReq.getCustomerGroupId())){
            //客群接口
            //TODO 查询客户群的列表
//            CustomerGroupParam customerGroupParam = new CustomerGroupParam();
//            customerGroupParam.setId(Long.valueOf(customerMarketing.getCustomerGroupId()));
//            customerGroupParam.setStoreId(addReq.getStoreId());
//            customerGroupParam.setTenantId(addReq.getTenantId());
//            List<Customer> customerList = iMarketingCustomerGroupService.getCustomerByCustomerGroupParam(customerGroupParam);
//            for(Customer customer : customerList){
//                CustomerAndVehicleReq cavReq = new CustomerAndVehicleReq();
//                //客户详情
//                CustomerDetailResp customerDetailResp = iCustomerService.queryCustomer(customer.getId(),customerMarketing.getTenantId(),customerMarketing.getStoreId());
//                cavReq.setCustomerId(customer.getId());
//                cavReq.setCustomerName(customer.getName());
//                cavReq.setCustomerPhone(customer.getPhoneNumber());
//                customeList.add(cavReq);
//            }
        }else if(StringUtils.isNotBlank(addReq.getCustomerIds())){
            //指定客户
            String[] strArray = addReq.getCustomerIds().split(",");
            List<String> cusIds = Lists.newArrayList();
            for(int i=0;i<=strArray.length-1;i++){
                cusIds.add(strArray[i]);
            }
            BaseIdsReqVO baseIdsReqVO = new BaseIdsReqVO();
            baseIdsReqVO.setId(cusIds);
            baseIdsReqVO.setStoreId(addReq.getStoreId());
            List<CustomerDTO> customerDTOS = customerClient.getCustomerByIds(baseIdsReqVO).getData();
            for(CustomerDTO customerDTO : customerDTOS){
                CustomerAndVehicleReq cavReq = new CustomerAndVehicleReq();
                //客户详情
                cavReq.setCustomerId(customerDTO.getId());
                cavReq.setCustomerName(customerDTO.getName());
                cavReq.setCustomerPhone(customerDTO.getPhoneNumber());
                customeList.add(cavReq);
            }
        }
        return customeList;
    }



}
