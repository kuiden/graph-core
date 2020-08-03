package com.tuhu.store.saas.marketing.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/6/4
 * Time: 11:39
 * Description:
 */
@Data
public class ActivityCustomerListRequest implements Serializable {
    private static final long serialVersionUID = -8003984283015675760L;

    private String customerId;
    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 0:  待消费  1：已消费  2：已过期
     */
    private Integer status;
    /**
     * 前台分页插件，第一页从0开始
     */
    private Integer pageNum=0;
    private Integer pageSize=15;
}