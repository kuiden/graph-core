package com.tuhu.store.saas.marketing.dataobject;

import lombok.Data;

import java.util.Date;

@Data
public class MarketingSendRecord {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 定向营销表ID
     */
    private String marketingId;

    /**
     * 客户群ID
     */
    private String customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 车牌号
     */
    private String licensePlateNo;

    /**
     * 发送状态 0、未发送 1、已发送 2、发送失败
     */
    private Byte sendType;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否删除
     */
    private Byte isDelete;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

}
