package com.tuhu.store.saas.marketing.service.customergroup;

import java.util.List;

/**
 * 过滤因子接口过滤处理客户id
 */
public interface CustomerGroupFactorFilter {

    /**
     * 过滤客群获取客户id
     * @return
     */
    public List<String> filterProcess();
}
