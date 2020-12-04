package com.tuhu.store.saas.marketing.response.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 秒杀活动
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-03
 */
@ApiModel(value = "SeckillActivityStatisticsResp", description = "秒杀活动数据")
@Data
public class SeckillActivityStatisticsResp {
    @ApiModelProperty(value = "秒杀活动主键id")
    private String seckillActivityId;

    @ApiModelProperty(value = "活动名称")
    private String activityTitle;

    @ApiModelProperty(value = "今日成交，取当日成功支付购买的份数，注意不是支付笔数")
    private Integer todayDeal = 0;

    @ApiModelProperty(value = "总成交，取当前活动成功支付购买的份数，注意不是支付笔数")
    private Integer totalDeal = 0;

    @ApiModelProperty(value = "获取新客，通过此活动任意渠道页面登录且登录时在门店同步注册的手机号数量")
    private Integer newCustomers = 0;

    @ApiModelProperty(value = "新客转化率，此活动中有购买行为的新客手机号数除以新客手机号数，四舍五入最多保留一位小数")
    private BigDecimal newCustomersConversionRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "唤醒老客，通过此活动任意渠道页面登录且登录时未在门店同步注册的手机号数量")
    private Integer oldCustomer = 0;

    @ApiModelProperty(value = "老客转化率，此活动中有购买行为的老客手机号数除以老客手机号数，四舍五入最多保留一位小数")
    private BigDecimal oldCustomerConversionRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "总收入（元）")
    private BigDecimal totalAmount = BigDecimal.ZERO;

}
