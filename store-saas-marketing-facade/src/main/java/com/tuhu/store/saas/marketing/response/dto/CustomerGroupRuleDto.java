package com.tuhu.store.saas.marketing.response.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CustomerGroupRuleDto implements Serializable {

    private String cgRuleFactor;

    private String cgRuleName;

    private List<CustomerGroupRuleAttributeDto> attributeReqList;

}
