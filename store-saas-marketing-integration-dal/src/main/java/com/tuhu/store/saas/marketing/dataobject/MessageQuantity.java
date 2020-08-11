package com.tuhu.store.saas.marketing.dataobject;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageQuantity implements Serializable {
    private static final long serialVersionUID = 5859349019469858370L;
    private String id;

    /**
    * 默认初始容量
    */
    private Long initialQuantity;

    private Long quantity;

    /**
    * 剩余容量
    */
    private Long remainderQuantity;

    /**
     * 占用容量
     */
    private Long occupyQuantity;

    /**
    * 门店id
    */
    private Long storeId;

    /**
    * 租户id
    */
    private Long tenantId;

    /**
    * 描述
    */
    private String description;

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

    private Boolean isDelete;


}
