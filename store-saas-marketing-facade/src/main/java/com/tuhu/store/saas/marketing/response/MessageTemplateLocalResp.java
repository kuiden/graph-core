/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xuechaofu
 * @date 2018/11/1516:02
 */
@Data
public class MessageTemplateLocalResp implements Serializable {
    private static final long serialVersionUID = -2049704332730185582L;
    private String id;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板类型
     */
    private String templateType;

    /**
     *  备注
     */
    private String comment;

    /**
     *  原始模板ID
     */
    private Long originalTemplateId;

    /**
     * 模板内容
     */
    private String templateContent;
}
