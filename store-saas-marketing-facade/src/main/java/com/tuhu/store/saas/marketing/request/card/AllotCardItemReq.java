package com.tuhu.store.saas.marketing.request.card;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author wangyuqing
 * @since 2020/11/20 13:42
 */
@Data
public class AllotCardItemReq {
    @ApiModelProperty(value = "客户id (能拿到客户id就传)")
    private String customerId;

    @ApiModelProperty(value = "客户手机号")
    private String customerPhoneNumber;

    @ApiModelProperty(value = "商品id - 次数")
    private Map<String,Integer> goodsNumMap;

    @ApiModelProperty(value = "类型 1服务 2商品")
    private Integer type = 1;

    private Long storeId;

    private Long tenantId;
}
