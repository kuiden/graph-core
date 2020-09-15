package com.tuhu.store.saas.marketing.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class CustomerCouponDetailResponse {
    private Long id;
    private String title;
    private Integer type;
    private BigDecimal conditionLimit;
    private BigDecimal contentValue;
    private BigDecimal discountValue;
    private Date useStartTime;
    private Date useEndTime;
    private Date createTime;
    private Integer useStatus;
    private Integer status;
    private Integer receiveType;
    private String couponCode;
    private String customerName;
    private String customerId;
    private String phoneNumber;
    private String sendUser;
    private String code;

}
