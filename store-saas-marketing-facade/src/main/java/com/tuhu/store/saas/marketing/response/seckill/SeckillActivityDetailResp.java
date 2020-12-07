package com.tuhu.store.saas.marketing.response.seckill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wangyuqing
 * @since 2020/12/4 16:16
 */
@Data
public class SeckillActivityDetailResp implements Serializable {

    @ApiModelProperty("活动id")
    private String id;

    @ApiModelProperty("活动头图")
    private String headImage;

    @ApiModelProperty("有效期类型")
    private Integer cadCardExpiryDateType;

    @ApiModelProperty("相关次卡有效期时间")
    private Date cadCardExpiryDateTime;

    @ApiModelProperty("相关次卡有效天数")
    private Date cadCardExpiryDateDay;

    @ApiModelProperty("销售数量类型")
    private Integer sellNumberType;

    @ApiModelProperty("总销售数量  -1不限")
    private Integer totalNumber;

    @ApiModelProperty(value = "售出个数")
    private Integer salesNumber = 0;

    @ApiModelProperty("单人销售数量类型")
    private Integer soloSellNumberType;

    @ApiModelProperty("单人销售数量 -1不限")
    private Integer soloSellNumber;

    @ApiModelProperty("原价")
    private BigDecimal originalPrice;

    @ApiModelProperty("现价")
    private BigDecimal newPrice;

    @ApiModelProperty("租户id")
    private Long tenantId;

    @ApiModelProperty("门店id")
    private Long storeId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("上下架状态 0:未上架 1:上架 9:下架")
    private Integer status;

    @ApiModelProperty("活动状态")
    private String statusName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("活动标题")
    private String activityTitle;

    @ApiModelProperty("卡模板ID")
    private String cadCardTemplateId;

    @ApiModelProperty("咨询热线")
    private String phoneNumber;

    @ApiModelProperty("活动规则")
    private String activityRule;

    @ApiModelProperty("门店介绍")
    private String storeIntroduction;

    @ApiModelProperty("客户已购数量")
    private Integer buyNumber = 0;

    @ApiModelProperty("活动项目")
    private List<ActivityDetailItem> items;

    @ApiModelProperty("门店信息")
    private StoreInfo storeInfo;


    @Data
    public static class ActivityDetailItem implements Serializable {
        @ApiModelProperty("商品名称")
        private String goodsName;

        @ApiModelProperty("商品展示名称")
        private String showName;

        @ApiModelProperty("商品数量")
        private Integer itemQuantity;

        @ApiModelProperty("商品原价格")
        private Long originalPrice;
    }

    @Data
    public static class StoreInfo implements Serializable {
        @ApiModelProperty("门店名称")
        private String storeName;

        @ApiModelProperty("门店名称")
        private String address;

        @ApiModelProperty("营业时间起")
        private Date openingEffectiveDate;

        @ApiModelProperty("营业时间止")
        private Date openingExpiryDate;

        @ApiModelProperty("经度")
        private Double lon;

        @ApiModelProperty("纬度")
        private Double lat;

        @ApiModelProperty("手机号码")
        private String mobilePhone;

        @ApiModelProperty("门店照片")
        private String [] imagePaths;
    }


}
