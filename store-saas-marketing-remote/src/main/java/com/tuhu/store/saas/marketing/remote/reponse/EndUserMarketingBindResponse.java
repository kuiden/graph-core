/*
 * Copyright 2020 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.remote.reponse;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xiesisi
 * @date 2020/8/1418:15
 */
@Data
public class EndUserMarketingBindResponse implements Serializable {

    private static final long serialVersionUID = 611139761051642830L;

    //用户基本信息
    private String id;
    private String name;
    private String phoneNumber;
    private String nickName;

    //token信息
    private String access_token;
    private String token_type;
}