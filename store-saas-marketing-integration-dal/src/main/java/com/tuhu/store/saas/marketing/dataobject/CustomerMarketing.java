package com.tuhu.store.saas.marketing.dataobject;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerMarketing {
    /**
    * 表主键ID
    */
    private Long id;

    /**
    * 任务状态 0、待发送 1、已发送 2、已取消 3、发送失败
    */
    private Byte taskType;

    /**
    * 营销方式 0、优惠券关怀 1、短信营销
    */
    private Byte marketingMethod;

    /**
    * 客户群组表ID
    */
    private String customerGroupId;

    /**
    * 发送时间 精确到年月日时
    */
    private Date sendTime;

    /**
    * 优惠券标题
    */
    private String couponTitle;

    /**
    * 优惠券发送短信标记 0、否 1、是
    */
    private Boolean couponMessageFlag;

    /**
    * 短信模板表ID
    */
    private String messageTemplateId;

    /**
    * 短信模板名称
    */
    private String messageTemplate;

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
    private Byte isDelete;

    /**
    * 门店ID
    */
    private Long storeId;

    /**
    * 租户ID
    */
    private Long tenantId;

    /**
    * 发送对象（客群名称）
    */
    private String sendObject;

    /**
    * 券ID
    */
    private String couponId;

    /**
    * 短信模板变量
    */
    private String messageDatas;

    /**
    * 券编码
    */
    private String couponCode;

    /**
    * 备注
    */
    private String remark;

    /**
    * 客户ID，多个用逗号分隔
    */
    private String customerId;

    /**
     * 客户人数
     */
    private Long customerNum;

}
