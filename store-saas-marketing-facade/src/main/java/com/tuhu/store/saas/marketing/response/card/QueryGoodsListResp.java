package com.tuhu.store.saas.marketing.response.card;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: lixinlei
 * @date: 2018/11/5 14:22
 */
@Getter
@Setter
@ToString
public class QueryGoodsListResp implements Serializable {


    @ApiModelProperty("产品id")
    private String productId;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("商品id")
    private String goodsId;

    @ApiModelProperty("商品编码")
    private String goodsCode;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("品牌编码")
    private String brandCode;

    @ApiModelProperty("商品品牌")
    private String brandName;

    @ApiModelProperty("单位id")
    private String uomId;

    @ApiModelProperty("商品单位")
    private String unit;

    @ApiModelProperty("商品采购价")
    private Long purchasePrice;

    @ApiModelProperty("商品销售价")
    private Long sellPrice;

    @ApiModelProperty("商品规格")
    private String specification;

    @ApiModelProperty("占用库存")
    private BigDecimal occupyNum;

    @ApiModelProperty("可用库存")
    private BigDecimal usedNum;

    @ApiModelProperty("库存总数量（可用库存+占用库存）")
    private BigDecimal totalNum;

    @ApiModelProperty("入库价格")
    private Long price;

    @ApiModelProperty("库存金额")
    private Long inventoryPrice;

    @ApiModelProperty("业务分类")
    private String businessCategory;

    @ApiModelProperty("业务分类名称")
    private String businessCategoryName;

    @ApiModelProperty("商品类别第一级")
    private String firstCategoryCode;

    @ApiModelProperty("商品类别第二级")
    private String secondCategoryCode;

    @ApiModelProperty("商品类别第三级")
    private String thirdCategoryCode;

    @ApiModelProperty("商品类别第四级")
    private String fourCategoryCode;

    @ApiModelProperty("商品类别第五级")
    private String fiveCategoryCode;

    @ApiModelProperty("商品类别第一级名称")
    private String firstCategoryName;

    @ApiModelProperty("商品类别第二级名称")
    private String secondCategoryName;

    @ApiModelProperty("商品类别第三级名称")
    private String thirdCategoryName;

    @ApiModelProperty("商品类别第四级名称")
    private String fourCategoryName;

    @ApiModelProperty("商品类别第五级名称")
    private String fiveCategoryName;

    /**
     * 商品来源:
     * LOCAL:本地来源
     * CLOUD:云商品来源
     * OTHER:其他
     */
    @ApiModelProperty("商品来源,LOCAL:本地来源,CLOUD:JV商品来源")
    private String goodsSource;

    /**
     * 税率id
     */
    @ApiModelProperty("税率ID")
    private Long taxRateId;
    /**
     * 税率
     */
    @ApiModelProperty("税率")
    private Double taxRate;

    /**
     * 仓库ID
     */
    @ApiModelProperty("仓库ID")
    private String warehouseId;

    /**
     * 仓库ID
     */
    @ApiModelProperty("仓库ID")
    private String warehouseName;

    /**
     * 库位ID
     */
    @ApiModelProperty("库位ID")
    private Long locationId;

    /**
     * 库位编码
     */
    @ApiModelProperty("库位编码")
    private String locationCode;


    /**
     * 是否是大桶油
     */
    private Boolean isLargeBarrelOil;

}
