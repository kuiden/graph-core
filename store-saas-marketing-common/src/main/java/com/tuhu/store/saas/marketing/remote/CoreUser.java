package com.tuhu.store.saas.marketing.remote;

import lombok.Data;

@Data
public class CoreUser extends BaseUser {

    private Integer systemCode;

    private Long storeId;

    private Long companyId;

    private String companyName;

    private Integer companyType;

    private String openId;

    private Long id;

    /**
     * 门店用户ID
     */
    private String storeUserId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店对应的用户名称
     */
    private String nickName;

    /***
     * 门店编号
     */
    private String storeNo;

}
