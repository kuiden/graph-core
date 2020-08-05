package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.request.CustomerGroupReq;
import com.tuhu.store.saas.marketing.response.CustomerGroupResp;

import java.text.ParseException;

public interface ICustomerGroupService {

    void saveCustomerGroup(CustomerGroupReq req);

    CustomerGroupResp getCustomerGroupDetail(CustomerGroupReq req) throws ParseException;
}
