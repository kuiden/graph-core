package com.tuhu.store.saas.marketing.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/13 11:43
 */
@Data
public class ActivityTemplateRequest implements Serializable {
    private static final long serialVersionUID = 3593829274389776225L;

    /**
     * 模板名称搜索
     */
    private String searchKey;

    /**
     * 模板状态，0：禁用，1：启用
     */
    private Boolean status;

    /**
     * 是否B平台的查询
     */
    private Boolean forB;
}
