package com.tuhu.store.saas.marketing.controller.mini;

import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.remote.EndUser;


public class BaseEndUserApi {

    public EndUser getEndUser() {
        return EndUserContextHolder.getUser();
    }

    public String getCustomerId() {
        return EndUserContextHolder.getCustomerId();
    }


    public String getUserId() {
        return EndUserContextHolder.getUserId();
    }

    public Long getStoreId() {
        return EndUserContextHolder.getStoreId();
    }

    public Long getTenantId() {
        return EndUserContextHolder.getTenantId();
    }

}
