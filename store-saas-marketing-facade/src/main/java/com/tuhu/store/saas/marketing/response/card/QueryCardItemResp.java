package com.tuhu.store.saas.marketing.response.card;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wangyuqing
 * @since 2020/8/15 16:05
 */
@Data
public class QueryCardItemResp implements Serializable {

    private static final long serialVersionUID = -3236820605491032182L;

    /*
     * 服务项目id
     */
    private Long id;

    /**
     * 商品ID
     */
    private String goodsId;

    /**
     * 卡ID
     */
    private Long cardId;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 次数
     */
    private Integer measuredQuantity;

    /**
     * 已使用次数
     */
    private Integer usedQuantity;

    /*
     * 剩余次数
     */
    @ApiModelProperty("可用次数")
    private Integer remainQuantity;

    /**
     * 商品类型1：服务 2：商品
     */
    private Byte type;

    /**
     * 工时
     */
    private Integer laborHour;

    /**
     * 工时单价
     */
    private BigDecimal price;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    private CardGoods goods;

    private CardService service;

    @ApiModelProperty("拥有相同商品的次卡们(临近有效期的在前面)")
    private List<Cards> cards;

    @Data
    public static class CardGoods implements Serializable{
        private static final long serialVersionUID = -4233301077805487419L;
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
         * 仓库ID
         */
        @ApiModelProperty("仓库ID")
        private String warehouseId;

        /**
         * 仓库ID
         */
        @ApiModelProperty("仓库ID")
        private String warehouseName;

    }

    @Data
    public static class CardService implements Serializable{

        private static final long serialVersionUID = -3573123512262491892L;
        private String id;

        private Long spuId;

        private String childCode;

        private String serviceCode;

        private String serviceName;

        private Long hourPrice;

        private Long costHour;

        private Long businessCategory;

        /**
         * 业务分类code
         */
        private String businessCategoryCode;

        /**
         * 业务分类名称
         */
        private String businessCategoryName;

        private String type;

        /**
         * 商品来源:
         LOCAL:本地来源
         CLOUD:云商品来源
         OTHER:其他
         */
        private String goodsSource;

        /**
         * 是否上线（’1’-上线，’0’-未上线）
         */
        private Integer online;

    }

    @Data
    public static class Cards implements Serializable{

        @ApiModelProperty("卡ID")
        private Long cardId;

        @ApiModelProperty("卡名称")
        private String cardName;

        @ApiModelProperty("剩余次数")
        private Integer remainQuantity;

    }
}
