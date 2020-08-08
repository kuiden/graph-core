package com.tuhu.store.saas.marketing.dataobject;

import lombok.Data;

import java.util.Date;

@Data
public class StoreCustomerGroupRelation {

    private Long id;

    private String groupName;

    private Long storeId;

    private Long tenantId;

    private Long customerCount;

    private String groupDesc;

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


}
