package com.tuhu.store.saas.marketing.request.valueCard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * @author wangyuqing
 * @since 2020/10/19 9:56
 */
@Data
public class ValueCardRuleReq {

    private Long id;

    @ApiModelProperty("满额")
    private BigDecimal amount;

    @ApiModelProperty("赠额")
    private BigDecimal presentAmount;
}
