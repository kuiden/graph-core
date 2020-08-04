package com.tuhu.store.saas.marketing.po;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class SrvReservationOrder {
    private String id;

    /**
     * 业务分类
     */
    private String businessCategoryCode;

    /**
     * 业务分类名称
     */
    private String businessCategoryName;

    /**
     * 预约单号
     */
    private String reservationOrdeNo;

    /**
     * 工单号
     */
    private String serviceOrderNo;

    /**
     * 车辆ID
     */
    private String vehicleId;

    /**
     * 车型
     */
    private String vehicleModel;

    /**
     * 二级车型
     */

    private String productId;

    /**
     * 车牌号
     */
    private String licensePlateNo;

    /**
     * VIN码
     */
    private String vin;

    /**
     * 行驶里程
     */
    private Integer drivenDistance;

    /**
     * 客户(车主)ID
     */
    private String customerId;

    /**
     * 客户(车主)名称
     */
    private String customerName;

    /**
     * 客户(车主)手机号码
     */
    private String customerPhoneNumber;

    /**
     * 送修人名称
     */
    private String delegaterName;

    /**
     * 送修人手机
     */
    private String delegaterPhoneNumber;

    /**
     * 服务技师ID
     */
    private String technicianId;

    /**
     * 服务技师名称
     */
    private String technicianName;

    /**
     * 预计到店时间
     */
    private Date estimatedArriveTime;

    /**
     * 预约状态:待确认(UNCONFIRMED);已确认(CONFIRMED);已开单(ORDER);已取消(CANCEL)
     */
    private String status;

    /**
     * 合计金额
     */
    private Long amount;

    /**
     * 优惠金额
     */
    private Long discountAmount;

    /**
     * 应收金额
     */
    private Long actualAmount;

    /**
     * 其它费用
     */
    private Long extraAmount;

    /**
     * 预约备注
     */
    private String description;

    /**
     * 预约渠道:ZRJD(门店创建);ZXYY(在线预约);QT(其他)
     */
    private String sourceChannel;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Boolean isDelete;


    /**
     * 预约创建终端(0:pc 1:b端  2:c端)
     */
    private Byte teminal;

}