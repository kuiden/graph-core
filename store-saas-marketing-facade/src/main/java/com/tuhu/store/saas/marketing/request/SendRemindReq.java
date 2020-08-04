/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author xuechaofu
 * @date 2018/11/1510:04
 */
@Data
public class SendRemindReq implements Serializable {

    private static final long serialVersionUID = -4978687590789236039L;

    /**
     * 客户id列表
     */
    private List<CustomerAndVehicleReq> customerList;

    /**
     * 消息模板id
     */
    @NotNull(message = "请选择消息模板!")
    private String messageTemplateId;

    private Long storeId;

    private Long tenantId;

    private String userId;

    private Long companyId;

    private String datas;//模板数据

    /**
     * 短信来源
     */
    private String source;

    /**
     * 短信来源ID
     */
    private String sourceId;
}
