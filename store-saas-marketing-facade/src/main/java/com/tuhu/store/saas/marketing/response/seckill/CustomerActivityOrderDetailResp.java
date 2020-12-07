package com.tuhu.store.saas.marketing.response.seckill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wangyuqing
 * @since 2020/12/7 16:19
 */
@Data
public class CustomerActivityOrderDetailResp implements Serializable {

    @ApiModelProperty("活动详情")
    private SeckillActivityDetailResp activityInfo;

    @ApiModelProperty("购买记录")
    private List<PurchaseRecord> recordList;

    @ApiModelProperty("标识当前客户有可使用的次卡")
    private Boolean hasCard = false;

    @Data
    public static class PurchaseRecord implements Serializable {

        @ApiModelProperty(value = "报名客户id")
        private String customerId;

        @ApiModelProperty(value = "报名客户名称")
        private String customerName;

        @ApiModelProperty(value = "购买人手机号码")
        private String buyerPhoneNumber;

        @ApiModelProperty(value = "使用人手机号码")
        private String userPhoneNumber;

        @ApiModelProperty(value = "使用人客户id")
        private String userCustomerId;

        @ApiModelProperty(value = "单价")
        private BigDecimal unitPrice;

        @ApiModelProperty(value = "购买数量")
        private Long quantity;

        @ApiModelProperty(value = "购买金额")
        private BigDecimal expectAmount;

        @ApiModelProperty(value = "购买时间")
        private Date paymentTime;

        @ApiModelProperty(value = "有效期时间")
        private Date effectiveTime;

        @ApiModelProperty(value = "状态")
        private String statusName;
    }

}
