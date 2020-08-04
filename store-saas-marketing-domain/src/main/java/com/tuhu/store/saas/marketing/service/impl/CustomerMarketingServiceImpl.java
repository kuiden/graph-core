package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketingExample;
import com.tuhu.store.saas.marketing.dataobject.MessageQuantity;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CustomerMarketingMapper;
import com.tuhu.store.saas.marketing.request.MarketingAddReq;
import com.tuhu.store.saas.marketing.request.MarketingReq;
import com.tuhu.store.saas.marketing.request.MarketingUpdateReq;
import com.tuhu.store.saas.marketing.service.ICustomerMarketingService;
import com.tuhu.store.saas.marketing.service.IMessageQuantityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private IMessageQuantityService iMessageQuantityService;

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

}
