package com.tuhu.store.saas.marketing.response.valueCard;

import com.tuhu.store.saas.marketing.request.valueCard.ValueCardRuleReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wangyuqing
 * @since 2020/10/19 10:11
 */
@Data
public class QueryValueCardRuleResp {

    @ApiModelProperty("最低起充金额")
    private BigDecimal conditionLimit;

    private List<ValueCardRuleReq> ruleList;

}
