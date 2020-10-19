package com.tuhu.store.saas.marketing.request.valueCard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wangyuqing
 * @since 2020/10/19 9:44
 */
@Data
public class AddValueCardRuleReq {

    @ApiModelProperty("最低起充金额")
    private BigDecimal conditionLimit;

    private List<ValueCardRuleReq> ruleList;

    private Long storeId;

    private Long tenantId;

}
