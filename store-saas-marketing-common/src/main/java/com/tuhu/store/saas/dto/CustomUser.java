package com.tuhu.store.saas.dto;

import lombok.Data;

@Data
public class CustomUser extends BaseUser {

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

}
