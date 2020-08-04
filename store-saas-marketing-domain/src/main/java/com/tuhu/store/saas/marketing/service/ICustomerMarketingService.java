package com.tuhu.store.saas.marketing.service;

import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.request.MarketingAddReq;
import com.tuhu.store.saas.marketing.request.MarketingReq;
import com.tuhu.store.saas.marketing.request.MarketingUpdateReq;

import javax.validation.groups.Default;

/**
 * @Author: WangKun
 * @Description: 客户管理service
 * @Date: Created in 2018/9/21 下午5:27
 * @ProjectName: saas-crm
 * @Version: 1.0.0
 */
public interface ICustomerMarketingService extends Default {

    /**
     * 显示定向营销任务列表
     *
     * @param req
     * @return
     */
    PageInfo<CustomerMarketing> customerMarketingList(MarketingReq req);

    /**
     * 定向营销任务新增
     * @param addReq
     * @return
     */
    public MarketingAddReq addMarketingCustomer(MarketingAddReq addReq);

    /**
     * 更新定向营销任务状态
     * @param addReq
     */
    public void updateMarketingCustomerByTaskType(MarketingUpdateReq addReq);

}
