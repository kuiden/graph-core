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
 * 秒杀活动基础模板表
 * </p>
 *
 * @author kudeng
 * @since 2020-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("seckill_template_item")
public class SeckillTemplateItem implements Serializable {

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
     * 秒杀模板id
     */
    @TableField("seckill_template_id")
    private String seckillTemplateId;
    /**
     * 商品服务名称
     */
    @TableField("goods_name")
    private String goodsName;
    /**
     * 项目类型，0-商品，1-服务
     */
    @TableField("goods_type")
    private Integer goodsType;
    /**
     * 商品服务数量
     */
    @TableField("item_quantity")
    private Integer itemQuantity;
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


    public static final String ID = "id";

    public static final String TENANT_ID = "tenant_id";

    public static final String SECKILL_TEMPLATE_ID = "seckill_template_id";

    public static final String GOODS_NAME = "goods_name";

    public static final String GOODS_TYPE = "goods_type";

    public static final String ITEM_QUANTITY = "item_quantity";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_USER = "update_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String IS_DELETE = "is_delete";

}
