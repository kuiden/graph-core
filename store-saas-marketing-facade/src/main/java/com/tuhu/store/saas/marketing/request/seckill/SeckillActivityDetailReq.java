package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wangyuqing
 * @since 2020/12/3 15:45
 */
@Data
public class SeckillActivityDetailReq implements Serializable {

    @ApiModelProperty("活动id")
    private String seckillActivityId;

    private Long storeId;

    private Long tenantId;

}
