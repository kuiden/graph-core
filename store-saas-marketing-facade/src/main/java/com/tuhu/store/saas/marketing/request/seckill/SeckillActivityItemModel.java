package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModel;
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
    private String id;
    /**
     * 租户id
     */

    private Long tenantId;
    /**
     * 门店id
     */
    private Long storeId;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 修改人
     */
    private String updateUser;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 删除标识 0未删除 1删除
     */
    private Integer isDelete;
    /**
     * 秒杀活动id
     */
    private String seckillActivityId;
    /**
     * 商品ID
     */
    private String goodsId;
    /**
     * 商品编码code
     */
    private String goodsCode;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品类型，goods类型（1："服务,:2：商品）
     */
    private SeckillActivityItemTypeEnum goodsType;
    /**
     * 商品数量
     */
    private Integer itemQuantity;
    /**
     * 商品原单价
     */
    private BigDecimal originalPrice;
    /**
     * 商品实际单价
     */
    private BigDecimal newPrice;
    /**
     * 展示名称
     */
    private String showName;
}
