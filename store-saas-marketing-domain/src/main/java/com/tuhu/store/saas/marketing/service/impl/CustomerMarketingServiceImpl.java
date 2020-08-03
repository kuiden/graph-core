package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdsReqVO;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketingExample;
import com.tuhu.store.saas.marketing.dataobject.MessageQuantity;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ActivityMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CustomerMarketingMapper;
import com.tuhu.store.saas.marketing.po.Activity;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.request.CustomerAndVehicleReq;
import com.tuhu.store.saas.marketing.request.MarketingAddReq;
import com.tuhu.store.saas.marketing.request.MarketingReq;
import com.tuhu.store.saas.marketing.request.MarketingUpdateReq;
import com.tuhu.store.saas.marketing.service.ICustomerMarketingService;
import com.tuhu.store.saas.marketing.service.IMessageQuantityService;
import com.tuhu.store.saas.marketing.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class CustomerMarketingServiceImpl  implements ICustomerMarketingService {
    @Autowired
    private CustomerMarketingMapper customerMarketingMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private IMessageQuantityService iMessageQuantityService;

    @Autowired
    private CustomerClient customerClient;

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
        Long activityId = Long.valueOf(addReq.getCouponOrActiveIds());
        Activity activity = activityMapper.selectByPrimaryKey(activityId);
        if (null == activity || !addReq.getStoreId().equals(activity.getStoreId())) {
            //禁止查询非本门店的营销活动
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"活动不存在或者不属于本店");
        }
        if(!activity.getStatus()){
            //活动下架了
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"请将活动上架");
        }
        if(DateUtils.now().after(activity.getEndTime())){
            //活动结束了
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"活动已结束，不能做营销");
        }
        if(DateUtils.now().before(activity.getStartTime())){
            //活动还没有开始
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"发送时间不能小于活动开始时间");
        }
        String currentUser = UserContextHolder.getUser()==null?"system":UserContextHolder.getUserName();
        String sendObject = "";
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
        customerMarketing.setMessageTemplate("活动营销");
        customerMarketing.setMessageTemplateId("123test");
        customerMarketing.setSendTime(addReq.getSendTime());
        customerMarketing.setRemark(addReq.getRemark());
        customerMarketing.setSendObject(sendObject);//客户群名称
        customerMarketing.setTaskType((byte)0);
        insert(customerMarketing);
        //根据任务中记录的发送对象信息查询出客户列表
        List<CustomerAndVehicleReq> customeList = analyseCustomer(addReq);
    }

    private List<CustomerAndVehicleReq> analyseCustomer(MarketingAddReq addReq){
        //根据任务中记录的发送对象信息查询出客户列表
        List<CustomerAndVehicleReq> customeList = new ArrayList();
        //客群客户
        if (StringUtils.isNotBlank(addReq.getCustomerGroupId())){
            //客群接口
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
