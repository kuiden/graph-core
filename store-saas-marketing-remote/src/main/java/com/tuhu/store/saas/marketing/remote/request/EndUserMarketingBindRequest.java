/*
 * Copyright 2020 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.remote.request;

import lombok.Data;

/**
 * @author xiesisi
 * @date 2020/8/1417:58
 */
@Data
public class EndUserMarketingBindRequest {
    private String userType = "endUser";
    private String clientType = "end_user_client";
    private String phone;
    private Long storeId;
    private Long tenantId;

}