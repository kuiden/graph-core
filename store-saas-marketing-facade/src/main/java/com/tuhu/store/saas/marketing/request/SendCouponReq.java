package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 送券请求
 */
@Data
@ToString
@ApiModel(value = "优惠券活动送券对象")
public class SendCouponReq implements Serializable {
    private static final long serialVersionUID = -443348730858775054L;
    /**
     * 操作人用户ID
     */
    private String userId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 领取类型：
     * 0：主动在线领取
     * 1：手动发券
     * 2：定向营销发券
     */
    private Integer receiveType;

    /**
     * 优惠券编码集合
     */
    @NotNull(message = "优惠券编码不能为空")
    private List<String> codes;

    /**
     * 要送券的客户ID集合
     */
    @NotNull(message = "客户ID不能为空")
    private List<String> customerIds;


    /**
     * 优惠券送出张数
     */
    private Map<String,Integer> count;
}
