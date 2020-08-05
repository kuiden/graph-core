package com.tuhu.store.saas.marketing.dataobject;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerGroupRule {

    private Long id;

    private String cgRuleFactor;

    private String cgRuleName;

    private String attributeName;

    private String attributeValue;

    private String compareOperator;

    private String status;

    private Long groupId;

    private Long storeId;

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
