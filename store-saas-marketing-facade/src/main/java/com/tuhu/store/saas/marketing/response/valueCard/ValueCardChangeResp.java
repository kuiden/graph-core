package com.tuhu.store.saas.marketing.response.valueCard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wangyuqing
 * @since 2020/10/19 10:48
 */
@Data
public class ValueCardChangeResp {
    private Long id;

    /**
     * 储值卡id
     */
    private Long cardId;

    private Long storeId;

    private Long tenantId;

    @ApiModelProperty("变更单号")
    private String changeNo;

    @ApiModelProperty("关联业务订单id")
    private String orderId;

    @ApiModelProperty("关联业务单号")
    private String orderNo;

    @ApiModelProperty("关联营收单号")
    private String finNo;

    @ApiModelProperty("销售人员ID")
    private String salesmanId;

    @ApiModelProperty("销售人员姓名")
    private String salesmanName;

    @ApiModelProperty("本金变动")
    private BigDecimal changePrincipal;

    @ApiModelProperty("赠金变动")
    private BigDecimal changePresent;

    @ApiModelProperty("变更后账户余额")
    private BigDecimal amount;

    @ApiModelProperty("变更类型（0退款 1消费 2充值 3取消退款 4取消消费 5取消充值）")
    private Integer changeType;

    @ApiModelProperty("0未生效 1已生效")
    private Boolean status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @ApiModelProperty("备注")
    private String remark;

    /**
     * 创建人id
     */
    private String createUserId;

    /**
     * 更新人id
     */
    private String updateUserId;

    @ApiModelProperty("创建人姓名")
    private String createUserName;

}
