package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 优惠券活动新增请求
 */
@Data
@ToString
@ApiModel(value = "添加优惠券活动对象")
public class AddCouponReq implements Serializable {
    private static final long serialVersionUID = -5189301310890203692L;

    /**
     * 创建用户ID
     */
    private String userId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 优惠券类型：0：满减 ，代金券 1：满折，折扣券
     */
    @NotNull(message = "优惠券类型不能为空")
    private Integer type;

    /**
     * 有效期类型: 0：指定有效期时间 1：结束时间-相对时间
     */
    @NotNull(message = "有效期类型不能为空")
    private Integer validityType;

    /**
     * 优惠券名称
     */
    @NotBlank(message = "优惠券名称不能为空")
    @Size(max = 30, message = "优惠券名称只能在30个字符以内")
    private String title;

    /**
     * 优惠券使用条件金额
     */
    @NotNull(message = "使用门槛不能为空")
    private BigDecimal conditionLimit;

    /**
     * 代金券优惠金额
     */
    private BigDecimal contentValue;

    /**
     * 折扣券折扣数
     */
    private BigDecimal discountValue;

    /**
     * 使用开始时间
     */
    private Date useStartTime;

    /**
     * 使用结束时间
     */
    private Date useEndTime;

    /**
     * validity_type=1时,相对领取时间天数
     */
    private Integer relativeDaysNum;

    /**
     * 发放总量
     */
    private Long grantNumber;

    /**
     * 优惠券状态,0:禁用  1:启用
     */
    @NotNull(message = "券状态不能为空")
    private Integer status;

    /**
     * 是否允许领取 0：不允许 1：允许
     */
    @NotNull(message = "允许领券值不能为空")
    private Integer allowGet;

    /**
     * 适用范围类型 0：不限 1：限定商品 2：限定分类
     */
    @NotNull(message = "适用范围不能为空")
    private Integer scopeType;

    /**
     * 券说明
     */
    private String remark;

    @Valid
    private List<CouponScopeCategoryReq> categories;

}
