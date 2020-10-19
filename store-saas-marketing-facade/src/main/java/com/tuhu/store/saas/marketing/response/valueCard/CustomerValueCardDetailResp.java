package com.tuhu.store.saas.marketing.response.valueCard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wangyuqing
 * @since 2020/10/19 10:39
 */
@Data
public class CustomerValueCardDetailResp {
    private Long cardId;

    private String customerId;

    @ApiModelProperty("客户姓名")
    private String customerName;

    @ApiModelProperty("客户手机号")
    private String customerPhone;

    @ApiModelProperty("总余额")
    private BigDecimal amount;

    @ApiModelProperty("本金余额")
    private BigDecimal principalAmount;

    @ApiModelProperty("赠送余额")
    private BigDecimal presentAmount;

    @ApiModelProperty("首充时间")
    private Date createTime;

    @ApiModelProperty("最近用卡时间")
    private Date updateTime;

    @ApiModelProperty("累计充值金额")
    private BigDecimal rechargeAmount;

    @ApiModelProperty("累计充值次数")
    private BigDecimal rechargeCount;

    @ApiModelProperty("累计消费金额")
    private BigDecimal consumptionAmount;

    @ApiModelProperty("累计消费次数")
    private BigDecimal consumptionCount;

}
