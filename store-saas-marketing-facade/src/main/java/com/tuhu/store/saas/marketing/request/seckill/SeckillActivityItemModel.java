package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "活动商品详细信息")
public class SeckillActivityItemModel implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "ID", dataType = "String", example = "1")
    private String id;
    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;
    /**
     * 门店id
     */
    @ApiModelProperty(value = "门店id", hidden = true)
    private Long storeId;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人", hidden = true)
    private String createUser;
    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人", hidden = true)
    private String updateUser;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createTime;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间", hidden = true)
    private Date updateTime;
    /**
     * 删除标识 0未删除 1删除
     */
    @ApiModelProperty(value = "删除标识 0未删除 1删除", example = "0")
    private Integer isDelete;
    /**
     * 秒杀活动id
     */
    @ApiModelProperty(value = "秒杀活动id", example = "123123")
    private String seckillActivityId;
    /**
     * 商品ID
     */
    @ApiModelProperty(value = "商品ID", required = true, example = "123312")
    private String goodsId;
    /**
     * 商品编码code
     */
    @ApiModelProperty(value = "商品编码code", required = true, example = "FW0001")
    private String goodsCode;
    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称", required = true, example = "商品名称")
    private String goodsName;
    /**
     * 商品类型，goods类型（1：服务,:2：商品）
     */
    @ApiModelProperty(value = "商品类型，goods类型（1：服务,:2：商品）", required = true, example = "商品类型，goods类型（1：服务,:2：商品）")
    private SeckillActivityItemTypeEnum goodsType;
    /**
     * 商品数量
     */
    @ApiModelProperty(value = "商品数量", required = true, example = "商品数量")
    private Integer itemQuantity;
    /**
     * 商品原单价
     */
    @ApiModelProperty(value = "商品原单价", required = true, example = "商品原单价")
    private BigDecimal originalPrice;
    /**
     * 商品实际单价
     */
    @ApiModelProperty(value = "商品实际单价", example = "商品实际单价")
    private BigDecimal newPrice;
    /**
     * 展示名称
     */
    @ApiModelProperty(value = "展示名称", required = true, example = "展示名称")
    private String showName;
}
