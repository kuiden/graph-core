package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author wangyuqing
 * @since 2020/12/3 15:45
 */
@Data
public class SeckillActivityDetailReq implements Serializable {

    @ApiModelProperty("活动id")
    @NotNull(message = "活动id不能为空")
    private String seckillActivityId;

    private Long storeId;

    private Long tenantId;

    private String customerId;

    private String customerPhoneNumber;

    //页码
    private Integer pageNum = 1;

    //页量
    private Integer pageSize = 10;

    //交易单id
    private String tradeOrderId;

    //秒杀订单id
    private String seckillRegistrationRecordId;

}
