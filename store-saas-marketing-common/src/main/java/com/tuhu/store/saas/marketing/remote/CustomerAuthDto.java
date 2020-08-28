package com.tuhu.store.saas.marketing.remote;

import lombok.Data;

import java.io.Serializable;

@Data
public class CustomerAuthDto implements Serializable {
    private String userId;
    private String age;
    private String phone;
    private String name;
    private String state;
    private String idCard;
    private String userType;
    private String clientType;
    private String storeId;
    private String companyId;
    private String tenantId;
    private boolean resetFlag;
    private Boolean accountState;
    private String endUserId;
}
