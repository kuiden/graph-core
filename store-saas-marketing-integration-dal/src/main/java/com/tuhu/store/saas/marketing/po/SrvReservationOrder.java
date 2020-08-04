package com.tuhu.store.saas.marketing.po;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class SrvReservationOrder {
    private String id;

    /**
     * 预约单号
     */
    private String reservationOrdeNo;

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
     * 预计到店时间
     */
    private Date estimatedArriveTime;

    /**
     * 预约状态:待确认(UNCONFIRMED);已确认(CONFIRMED);已开单(ORDER);已取消(CANCEL)
     */
    private String status;

    /**
     * 预约备注
     */
    private String description;

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
     * 预约创建终端(0:H5 1:b端  2:c端)
     */
    private Integer teminal;

}