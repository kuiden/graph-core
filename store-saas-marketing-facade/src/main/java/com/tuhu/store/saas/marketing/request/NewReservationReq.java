package com.tuhu.store.saas.marketing.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/4 16:27
 */
@Data
public class NewReservationReq implements Serializable {
    private static final long serialVersionUID = -6270134858919379753L;

    private Long tenantId;

    private Long storeId;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date estimatedArriveTime;

    /**
     * 预约备注
     */
    private String description;

    /**
     * 验证码
     */
    private String verificationCode;

    /**
     * 预约创建终端(0:H5 1:b端  2:c端)
     */
    private Integer teminal;

}

