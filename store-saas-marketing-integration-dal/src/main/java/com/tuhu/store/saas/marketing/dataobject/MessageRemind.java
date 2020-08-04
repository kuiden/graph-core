package com.tuhu.store.saas.marketing.dataobject;

import lombok.Data;

import java.util.Date;

@Data
public class MessageRemind {

    private String id;

    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户联系电话
     */
    private String phoneNumber;

    /**
     * 消息模板ID
     */
    private String messageTemplateId;

    /**
     * 模板编码
     */
    private String messageTemplateCode;

    /**
     * 尝试次数
     */
    private Integer tryTime;

    /**
     * 短信平台模板id
     */
    private String templateId;

    /**
     * 短信模板变量
     */
    private String datas;

    /**
     * 状态
     */
    private String status;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Boolean isDelete;

    /**
     * 是否提醒
     */
    private Boolean remind;

    /**
     * 车辆id
     */
    private String vehicleId;

    /**
     * 到期时间
     */
    private Date expiryDate;

    /**
     * 短信来源
     */
    private String source;

    /**
     * 短信来源ID
     */
    private String sourceId;

    /**
     * 短信平台返回结果
     */
    private String statusMessage;

    /**
     * 消息内容
     */
    private String messageContent;

}
