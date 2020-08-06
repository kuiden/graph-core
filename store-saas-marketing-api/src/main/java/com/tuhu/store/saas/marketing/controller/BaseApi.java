package com.tuhu.store.saas.marketing.controller;

import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.remote.CustomUser;

/**
 * @author jiangyuhang
 * @date 2018/10/2418:20
 */
public class BaseApi {
    public CustomUser getUserCore() {
        return UserContextHolder.getUser();
    }


    public Long getTenantId() {
        return UserContextHolder.getTenantId();
    }

    public String getUserId() {
        String userId = UserContextHolder.getStoreUserId();
        if (null != userId) {
            return userId;
        }
        return "";
    }

    public String getAccountId() {
        if (null != this.getUserCore()) {
            return String.valueOf(this.getUserCore().getAccountId());
        } else {
            return null;
        }
    }

    public String getUserName() {
        if (null != this.getUserCore()) {
            return String.valueOf(this.getUserCore().getUsername());
        } else {
            return null;
        }
    }

    public Long getCompanyId() {
        return UserContextHolder.getCompanyId();
    }

    public Long getStoreId() {
        return UserContextHolder.getStoreId();
    }

    public  String getStoreNo(){
        return UserContextHolder.getUser().getStoreNo();
    }

    public String getPhone() {
        CustomUser user = this.getUserCore();
        if (null != user) {
            return user.getAccount();
        }
        return null;
    }

}
