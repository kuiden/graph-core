package com.tuhu.store.saas.marketing.dataobject;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 秒杀活动明细表
 * </p>
 *
 * @author zhaijingtao
 * @since 2020-12-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("seckill_activity_item")
public class SeckillActivityItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;
    /**
     * 租户id
     */
    @TableField("tenant_id")
    private Long tenantId;
    /**
     * 门店id
     */
    @TableField("store_id")
    private Long storeId;
    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;
    /**
     * 修改人
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 删除标识 0未删除 1删除
     */
    @TableField("is_delete")
    private Integer isDelete;
    /**
     * 秒杀活动id
     */
    @TableField("seckill_activity_id")
    private String seckillActivityId;
    /**
     * 商品ID
     */
    @TableField("goods_id")
    private String goodsId;
    /**
     * 商品编码code
     */
    @TableField("goods_code")
    private String goodsCode;
    /**
     * 商品名称
     */
    @TableField("goods_name")
    private String goodsName;
    /**
     * 商品类型，goods类型（0："商品,:1：服务）
     */
    @TableField("goods_type")
    private Integer goodsType;
    /**
     * 商品数量
     */
    @TableField("item_quantity")
    private Integer itemQuantity;
    /**
     * 商品原单价
     */
    @TableField("original_price")
    private Long originalPrice;
    /**
     * 商品实际单价
     */
    @TableField("new_price")
    private Long newPrice;
    /**
     * 展示名称
     */
    @TableField("show_name")
    private String showName;


    public static final String ID = "id";

    public static final String TENANT_ID = "tenant_id";

    public static final String STORE_ID = "store_id";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_USER = "update_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String IS_DELETE = "is_delete";

    public static final String SECKILL_ACTIVITY_ID = "seckill_activity_id";

    public static final String GOODS_ID = "goods_id";

    public static final String GOODS_CODE = "goods_code";

    public static final String GOODS_NAME = "goods_name";

    public static final String GOODS_TYPE = "goods_type";

    public static final String ITEM_QUANTITY = "item_quantity";

    public static final String ORIGINAL_PRICE = "original_price";

    public static final String NEW_PRICE = "new_price";

    public static final String SHOW_NAME = "show_name";

}
