/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xuechaofu
 * @date 2018/11/1913:46
 */
@Data
public class CustomerAndVehicleReq implements Serializable {

    private static final long serialVersionUID = -6191579006811950413L;
    /**
     * 顾客id
     */
    private String customerId;
    /**
     * 顾客车牌号
     */
    private String licensePlateNumber;

    /**
     * 下次保养时间
     */
    private String nextDate;

    /**
     * 车辆id
     */
    private String vehicleId;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 客户手机号
     */
    private String customerPhone;
}
