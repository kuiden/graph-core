package com.tuhu.store.saas.marketing.dataobject;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 秒杀报名记录表
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("seckill_registration_record")
public class SeckillRegistrationRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;
    /**
     * 秒杀活动单号
     */
    @TableField("order_no")
    private String orderNo;
    /**
     * 门店id
     */
    @TableField("store_id")
    private Long storeId;
    /**
     * 租户id
     */
    @TableField("tenant_id")
    private Long tenantId;
    /**
     * 秒杀活动主键id
     */
    @TableField("seckill_activity_id")
    private String seckillActivityId;
    /**
     * 秒杀活动名称
     */
    @TableField("seckill_activity_name")
    private String seckillActivityName;
    /**
     * 报名客户id
     */
    @TableField("customer_id")
    private String customerId;
    /**
     * 报名客户名称
     */
    @TableField("customer_name")
    private String customerName;
    /**
     * 是否新客户 0:否 1:是
     */
    @TableField("is_new_customer")
    private Integer isNewCustomer;
    /**
     * 购买人手机号码
     */
    @TableField("buyer_phone_number")
    private String buyerPhoneNumber;
    /**
     * 使用人手机号码
     */
    @TableField("user_phone_number")
    private String userPhoneNumber;

    /**
     * 使用人客户id
     */
    @TableField("user_customer_id")
    private String userCustomerId;
    /**
     * 车牌号
     */
    @TableField("vehicle_number")
    private String vehicleNumber;
    /**
     * 单价
     */
    @TableField("unit_price")
    private BigDecimal unitPrice;
    /**
     * 购买数量
     */
    private Long quantity;
    /**
     * 应付金额
     */
    @TableField("expect_amount")
    private BigDecimal expectAmount;
    /**
     * 报名时间
     */
    @TableField("register_time")
    private Date registerTime;
    /**
     * 秒杀活动有效期
     */
    @TableField("effective_time")
    private Date effectiveTime;
    /**
     * 付款时间
     */
    @TableField("payment_time")
    private Date paymentTime;
    /**
     * 使用状态 0:初始化 1:使用中 2:已过期
     */
    @TableField("usage_status")
    private Integer usageStatus;
    /**
     * 支付状态 0:未支付 1:成功 2:失败 3:作废
     */
    @TableField("pay_status")
    private Integer payStatus;
    /**
     * 收款方式,枚举:WeChat 微信扫码
     */
    @TableField("payment_mode_code")
    private String paymentModeCode;
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

    public static final String ORDER_NO = "order_no";

    public static final String STORE_ID = "store_id";

    public static final String TENANT_ID = "tenant_id";

    public static final String SECKILL_ACTIVITY_ID = "seckill_activity_id";

    public static final String SECKILL_ACTIVITY_NAME = "seckill_activity_name";

    public static final String CUSTOMER_ID = "customer_id";

    public static final String CUSTOMER_NAME = "customer_name";

    public static final String IS_NEW_CUSTOMER = "is_new_customer";

    public static final String BUYER_PHONE_NUMBER = "buyer_phone_number";

    public static final String USER_PHONE_NUMBER = "user_phone_number";

    public static final String VEHICLE_NUMBER = "vehicle_number";

    public static final String UNIT_PRICE = "unit_price";

    public static final String QUANTITY = "quantity";

    public static final String EXPECT_AMOUNT = "expect_amount";

    public static final String REGISTER_TIME = "register_time";

    public static final String EFFECTIVE_TIME = "effective_time";

    public static final String PAYMENT_TIME = "payment_time";

    public static final String USAGE_STATUS = "usage_status";

    public static final String PAY_STATUS = "pay_status";

    public static final String PAYMENT_MODE_CODE = "payment_mode_code";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_USER = "update_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String IS_DELETE = "is_delete";

}
