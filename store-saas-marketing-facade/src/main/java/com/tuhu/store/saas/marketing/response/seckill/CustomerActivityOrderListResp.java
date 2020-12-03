package com.tuhu.store.saas.marketing.response.seckill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wangyuqing
 * @since 2020/12/3 16:58
 */
@Data
public class CustomerActivityOrderListResp implements Serializable {

    @ApiModelProperty("活动id")
    private String seckillActivityId;

    //活动标题

    //活动图片

    @ApiModelProperty("活动内实付金额")
    private BigDecimal amount;

}
