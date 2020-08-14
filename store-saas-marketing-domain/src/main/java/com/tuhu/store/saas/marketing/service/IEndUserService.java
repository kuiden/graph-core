package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.dataobject.EndUser;
import java.util.List;

/**
 * 车主信息服务类
 */
public interface IEndUserService {


    /**
     * 根据门店客户ID查找绑定的C端用户信息
     *
     * @param customerId
     * @return
     */
    List<EndUser> findByCustomerId(String customerId);


}
