package com.tuhu.store.saas.marketing.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 优惠券详情
 */
@Data
@ToString
public class CouponResp implements Serializable {
    private static final long serialVersionUID = -4150441340977928595L;

    /**
     * 优惠券活动ID
     */
    private Long id;

    /**
     * 优惠券编码
     */
    private String code;

    /**
     * 加密后的优惠券编码
     */
    private String encryptedCode;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 优惠券类型：0：满减 ，代金券 1：满折，折扣券
     */
    private Integer type;

    /**
     * 有效期类型: 0：指定有效期时间 1：结束时间-相对时间
     */
    private Integer validityType;

    /**
     * 优惠券名称
     */
    private String title;

    /**
     * 优惠券使用条件金额
     */
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
    private Integer status;

    /**
     * 是否允许领取 0：不允许 1：允许
     */
    private Integer allowGet;

    /**
     * 适用范围类型 0：不限 1：限定商品 2：限定分类
     */
    private Integer scopeType;

    /**
     * 券说明
     */
    private String remark;

    /**
     * 优惠券领取页，微信小程序二维码图片链接
     */
    private String weixinQrUrl;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新用户
     */
    private String updateUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 已发放数量
     */
    private Long sendNumber;

    /**
     * 已使用数量
     */
    private Long usedNumber;

    /**
     * 限定的分类
     */
    private List<CouponScopeCategoryResp> categories;
}
