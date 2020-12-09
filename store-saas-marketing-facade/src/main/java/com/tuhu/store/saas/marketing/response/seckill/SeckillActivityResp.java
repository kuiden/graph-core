package com.tuhu.store.saas.marketing.response.seckill;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 秒杀活动分类表
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@Data
@ApiModel(value = "SeckillActivityResp", description = "秒杀活动记录")
public class SeckillActivityResp implements Serializable {
    @ApiModelProperty(value = "秒杀活动id")
    @JsonProperty("seckillActivityId")
    private String id;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "状态名称")
    private String statusName;
    @ApiModelProperty(value = "售出个数")
    private Integer salesNumber = 0;
    @ApiModelProperty(value = "活动标题")
    private String activityTitle;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    @ApiModelProperty(value = "现价")
    private BigDecimal newPrice;
    @ApiModelProperty(value = "原价")
    private BigDecimal originalPrice;
    @ApiModelProperty(value = "活动头图")
    private String headImage;
    @ApiModelProperty(value = "有效期类型")
    private Integer cadCardExpiryDateType;
    @ApiModelProperty(value = "相关次卡有效期时间")
    private Date cadCardExpiryDateTime;
    @ApiModelProperty(value = "相关次卡有效天数")
    private Date cadCardExpiryDateDay;
    @ApiModelProperty(value = "销售数量类型 1限制数量 2不限制数量")
    private Integer sellNumberType;
    @ApiModelProperty(value = "销售数量限制 只有当销售数量为 限制数量时有效")
    private Integer sellNumber;
    @ApiModelProperty(value = "单人销售数量类型 1限制数量 2不限制数量")
    private Integer soloSellNumberType;
    @ApiModelProperty(value = "单人销售数量 只有当单人销售数量类型为 限制数量时有效")
    private Integer soloSellNumber;
    @ApiModelProperty(value = "微信活动二维码")
    private String wxQrUrl;
    @ApiModelProperty(value = "门店名称")
    private String storeName;
    @ApiModelProperty(value = "门店地址")
    private String address;
    @ApiModelProperty(value = "联系电话")
    private String clientAppointPhone;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "营业时间起取HH:mm")
    private Date openingEffectiveDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "营业时间止取HH:mm")
    private Date openingExpiryDate;
    private String templateId;
    private String cadCardTemplateId;
}
