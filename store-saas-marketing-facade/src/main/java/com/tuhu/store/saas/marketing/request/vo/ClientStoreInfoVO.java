/*
 * Copyright 2020 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.request.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author xiesisi
 * @date 2020/8/1211:47
 */
@Data
public class ClientStoreInfoVO {
    /**
     * 门店名称
     */
    private String storeName;
    /**
     *  门店地址
     */
    private String address;
    /**
     * 营业时间起
     */
    private Date openingEffectiveDate;
    /**
     * 营业时间止
     */
    private Date openingExpiryDate;
    /**
     * 经度
     */
    private Double lon;
    /**
     * 纬度
     */
    private Double lat;
    /**
     * 手机号码
     */
    private String mobilePhone;

}