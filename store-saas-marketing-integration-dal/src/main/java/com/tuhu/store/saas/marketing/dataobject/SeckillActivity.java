package com.tuhu.store.saas.marketing.dataobject;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 秒杀活动表
 * </p>
 *
 * @author zhaijingtao
 * @since 2020-12-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("seckill_activity")
public class SeckillActivity implements Serializable {

    public SeckillActivityModel toModel() {
        SeckillActivityModel model = new SeckillActivityModel();
        BeanUtils.copyProperties(this,model);
        return model;
    }

    public SeckillActivity() {
    }

    public SeckillActivity(SeckillActivityModel model) {
        BeanUtils.copyProperties(model,this);
    }

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;
    /**
     * 活动头图
     */
    @TableField("head_image")
    private String headImage;
    /**
     * 有效期类型
     */
    @TableField("cad_card_expiry_date_type")
    private Integer cadCardExpiryDateType;
    /**
     * 相关次卡有效期时间
     */
    @TableField("cad_card_expiry_date_time")
    private Date cadCardExpiryDateTime;
    /**
     * 相关次卡有效天数
     */
    @TableField("cad_card_expiry_date_day")
    private Integer cadCardExpiryDateDay;
    /**
     * 销售数量类型
     */
    @TableField("sell_number_type")
    private Integer sellNumberType;
    /**
     * 销售数量  -1不限
     */
    @TableField("sell_number")
    private Integer sellNumber;
    /**
     * 单人销售数量类型
     */
    @TableField("solo_sell_number_type")
    private Integer soloSellNumberType;
    /**
     * 单人销售数量 -1不限
     */
    @TableField("solo_sell_number")
    private Integer soloSellNumber;
    /**
     * 原价
     */
    @TableField("original_price")
    private BigDecimal originalPrice;
    /**
     * 现价
     */
    @TableField("new_price")
    private BigDecimal newPrice;
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
     * 上下架状态 0:未上架 1:上架 9:下架
     */
    private Integer status;
    /**
     * 活动开始时间
     */
    @TableField("start_time")
    private Date startTime;
    /**
     * 活动结束时间
     */
    @TableField("end_time")
    private Date endTime;
    /**
     * 活动标题
     */
    @TableField("activity_title")
    private String activityTitle;

    /**
     * 模板ID
     */
    @TableField("template_id")
    private String templateId;


    /**
     * 卡模板ID
     */
    @TableField("cad_card_template_id")
    private String cadCardTemplateId;

    /**
     * 微信活动二维码
     */
    @TableField("wx_qr_url")
    private String wxQrUrl;


    public static final String CADCARDTEMPLATEID = "cad_card_template_id";
    public static final String TEMPLATEID = "template_id";

    public static final String ID = "id";

    public static final String HEAD_IMAGE = "head_image";

    public static final String CAD_CARD_EXPIRY_DATE_TYPE = "cad_card_expiry_date_type";

    public static final String CAD_CARD_EXPIRY_DATE_TIME = "cad_card_expiry_date_time";

    public static final String CAD_CARD_EXPIRY_DATE_DAY = "cad_card_expiry_date_day";

    public static final String SELL_NUMBER_TYPE = "sell_number_type";

    public static final String SELL_NUMBER = "sell_number";

    public static final String SOLO_SELL_NUMBER_TYPE = "solo_sell_number_type";

    public static final String SOLO_SELL_NUMBER = "solo_sell_number";

    public static final String ORIGINAL_PRICE = "original_price";

    public static final String NEW_PRICE = "new_price";

    public static final String TENANT_ID = "tenant_id";

    public static final String STORE_ID = "store_id";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_USER = "update_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String IS_DELETE = "is_delete";

    public static final String STATUS = "status";

    public static final String START_TIME = "start_time";

    public static final String END_TIME = "end_time";

    public static final String ACTIVITY_TITLE = "activity_title";

}
