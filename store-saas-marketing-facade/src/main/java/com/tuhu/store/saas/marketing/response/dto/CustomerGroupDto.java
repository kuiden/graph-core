package com.tuhu.store.saas.marketing.response.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ToString
public class CustomerGroupDto  implements Serializable {

    private Long id;
    private String groupName;

    private String groupDesc;

    private Long storeId;

    private Long tenantId;

    private List<CustomerGroupRuleDto> customerGroupRuleReqList;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
