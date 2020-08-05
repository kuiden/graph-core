package com.tuhu.store.saas.marketing.controller;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.dataobject.MarketingSendRecord;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.request.CustomerGroupReq;
import com.tuhu.store.saas.marketing.response.CustomerGroupResp;
import com.tuhu.store.saas.marketing.response.GoodsResp;
import com.tuhu.store.saas.marketing.service.ICustomerGroupService;
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



}
