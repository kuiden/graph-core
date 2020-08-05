package com.tuhu.store.saas.marketing.controller;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.constant.CustomerGroupConstant;
import com.tuhu.store.saas.marketing.dataobject.MarketingSendRecord;
import com.tuhu.store.saas.marketing.dataobject.StoreCustomerGroupRelation;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.request.CustomerGroupListReq;
import com.tuhu.store.saas.marketing.request.CustomerGroupReq;
import com.tuhu.store.saas.marketing.request.card.CardTemplateModel;
import com.tuhu.store.saas.marketing.response.CustomerGroupResp;
import com.tuhu.store.saas.marketing.response.GoodsResp;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupDto;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupRuleAttributeDto;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupRuleDto;
import com.tuhu.store.saas.marketing.service.ICustomerGroupService;
import com.tuhu.store.saas.marketing.service.customergroup.CustomerGroupFilterFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/customer/group")
@Api(value = "客群相关api")
@Slf4j
public class CustomerGroupApi extends BaseApi{
    @Autowired
    private ICustomerGroupService iCustomerGroupService;

    @RequestMapping(value = "/saveCustomerGroup", method = RequestMethod.POST)
    public BizBaseResponse saveCustomerGroup(@RequestBody CustomerGroupReq req) {
        if (req == null) {
            return new BizBaseResponse(BizErrorCodeEnum.PARAM_ERROR, "参数验证失败");
        }
        if (StringUtils.isEmpty(req.getConsumerGroupName())) {
            return new BizBaseResponse(BizErrorCodeEnum.PARAM_ERROR, "客群名称不能为空");
        }
        req.setStoreId(super.getStoreId());
        req.setCreateUser(this.getUserId());
        iCustomerGroupService.saveCustomerGroup(req);
        return new BizBaseResponse(1);
    }

    @RequestMapping(value = "/getCustomerGroup", method = RequestMethod.POST)
    public BizBaseResponse getCustomerGroup(@RequestBody CustomerGroupReq req) {
        if (req == null) {
            return new BizBaseResponse(BizErrorCodeEnum.PARAM_ERROR, "参数验证失败");
        }
        if (req.getId()==null) {
            return new BizBaseResponse(BizErrorCodeEnum.PARAM_ERROR, "客群ID不能为空");
        }
        req.setStoreId(super.getStoreId());
        req.setCreateUser(this.getUserId());
        CustomerGroupResp customerGroupResp = null;
        try {
            customerGroupResp = iCustomerGroupService.getCustomerGroupDetail(req);
        } catch (ParseException e) {
            log.error("查询客群详情出错",e);
            throw new StoreSaasMarketingException("查询客群详情出错");
        }
        return new BizBaseResponse(customerGroupResp);
    }

    @RequestMapping(value = "/getCustomerGroupList", method = RequestMethod.POST)
    public BizBaseResponse<PageInfo<StoreCustomerGroupRelation>> getCustomerGroupList(@RequestBody CustomerGroupListReq req) {
        req.setStoreId(this.getStoreId());
        req.setTenantId(this.getTenantId());
        return new BizBaseResponse(iCustomerGroupService.getCustomerGroupList(req));
    }


    @RequestMapping(value = "/computerData", method = RequestMethod.POST)
    public  BizBaseResponse computerData(@RequestBody CustomerGroupReq req) {
        req.setStoreId(this.getStoreId());
        req.setTenantId(this.getTenantId());

        CustomerGroupDto customerGroupDto = new CustomerGroupDto();
        customerGroupDto.setStoreId(this.getStoreId());

        List<CustomerGroupRuleDto> customerGroupRuleReqList = new ArrayList<>();


       /* CustomerGroupRuleDto customerGroupRuleDto = new CustomerGroupRuleDto();
        List<CustomerGroupRuleAttributeDto> attributeReqList = new ArrayList<>();
        CustomerGroupRuleAttributeDto customerGroupRuleAttributeDto = new CustomerGroupRuleAttributeDto();
        customerGroupRuleAttributeDto.setAttribute(CustomerGroupConstant.RECENT_DAYS);
        customerGroupRuleAttributeDto.setAttributeValue("7");
        attributeReqList.add(customerGroupRuleAttributeDto);
        customerGroupRuleDto.setCgRuleFactor(CustomerGroupConstant.NO_CONSUMER_BEHAVIOR_FACTOR);
        customerGroupRuleDto.setAttributeReqList(attributeReqList);
*/

        CustomerGroupRuleDto customerGroupRuleDto1 = new CustomerGroupRuleDto();
        List<CustomerGroupRuleAttributeDto> attributeReqList1 = new ArrayList<>();
        CustomerGroupRuleAttributeDto customerGroupRuleAttributeDto1 = new CustomerGroupRuleAttributeDto();
        customerGroupRuleAttributeDto1.setAttribute(CustomerGroupConstant.RECENT_DAYS);
        customerGroupRuleAttributeDto1.setAttributeValue("100");
        attributeReqList1.add(customerGroupRuleAttributeDto1);
        customerGroupRuleDto1.setCgRuleFactor(CustomerGroupConstant.HAS_CONSUMER_FACTOR);
        customerGroupRuleDto1.setAttributeReqList(attributeReqList1);

        CustomerGroupRuleDto customerGroupRuleDto2 = new CustomerGroupRuleDto();
        List<CustomerGroupRuleAttributeDto> attributeReqList2 = new ArrayList<>();
        CustomerGroupRuleAttributeDto customerGroupRuleAttributeDto2 = new CustomerGroupRuleAttributeDto();
        customerGroupRuleAttributeDto2.setAttribute(CustomerGroupConstant.RECENT_DAYS);
        customerGroupRuleAttributeDto2.setAttributeValue("100");

        CustomerGroupRuleAttributeDto customerGroupRuleAttributeDto3 = new CustomerGroupRuleAttributeDto();
        customerGroupRuleAttributeDto3.setAttribute(CustomerGroupConstant.LEAST_TIME);
        customerGroupRuleAttributeDto3.setAttributeValue("5");

        attributeReqList2.add(customerGroupRuleAttributeDto2);
        attributeReqList2.add(customerGroupRuleAttributeDto3);
        customerGroupRuleDto2.setCgRuleFactor(CustomerGroupConstant.CONSUMER_TIME_FACTOR);
        customerGroupRuleDto2.setAttributeReqList(attributeReqList2);

        //customerGroupRuleReqList.add(customerGroupRuleDto);
        customerGroupRuleReqList.add(customerGroupRuleDto1);
        customerGroupRuleReqList.add(customerGroupRuleDto2);
        customerGroupDto.setCustomerGroupRuleReqList(customerGroupRuleReqList);
        List<String> strings = CustomerGroupFilterFactory.createFilter(customerGroupDto).filterProcess();
        return new BizBaseResponse(strings);
    }

    @RequestMapping(value = "/calculateCustomerCount", method = RequestMethod.POST)
    public  BizBaseResponse calculateCustomerCount(@RequestBody CustomerGroupReq req) {
        CustomerGroupDto customerGroupDto = new CustomerGroupDto();
        customerGroupDto.setStoreId(this.getStoreId());

        List<CustomerGroupRuleDto> customerGroupRuleReqList = new ArrayList<>();
    }


}
