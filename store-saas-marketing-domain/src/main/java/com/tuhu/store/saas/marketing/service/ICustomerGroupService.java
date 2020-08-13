package com.tuhu.store.saas.marketing.service;

import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.dataobject.StoreCustomerGroupRelation;
import com.tuhu.store.saas.marketing.request.CalculateCustomerCountReq;
import com.tuhu.store.saas.marketing.request.CustomerGroupListReq;
import com.tuhu.store.saas.marketing.request.CustomerGroupReq;
import com.tuhu.store.saas.marketing.response.CustomerGroupResp;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupDto;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ICustomerGroupService {

    void saveCustomerGroup(CustomerGroupReq req);

    CustomerGroupResp getCustomerGroupDetail(CustomerGroupReq req) throws ParseException;

    PageInfo<StoreCustomerGroupRelation> getCustomerGroupList(CustomerGroupListReq req);

    List<CustomerGroupDto> getCustomerGroupDto(CalculateCustomerCountReq calculateCustomerCountReq);

    List<String> calculateCustomerCount(CalculateCustomerCountReq req);

    Map<String,List<String>> calculateCustomerCountMap(CalculateCustomerCountReq req);
}
