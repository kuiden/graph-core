package com.tuhu.store.saas.marketing.response.seckill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 报名客户购买信息
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@Data
@ApiModel(value = "SeckillRegistrationRecordResp", description = "秒杀活动记录")
public class SeckillRegistrationRecordResp {

    @ApiModelProperty(value = "秒杀活动主键id")
    private String seckillActivityId;

    @ApiModelProperty(value = "报名客户id")
    private String customerId;

    @ApiModelProperty(value = "报名客户名称")
    private String customerName;

    @ApiModelProperty(value = "是否新客户 0:否 1:是")
    private Integer isNewCustomer = 0;

    @ApiModelProperty(value = "购买人手机号码")
    private String buyerPhoneNumber;

    @ApiModelProperty(value = "使用人手机号码")
    private String userPhoneNumber;

    @ApiModelProperty(value = "使用人客户id")
    private String userCustomerId;

    @ApiModelProperty(value = "购买数量或次数")
    private Long quantity;

    @ApiModelProperty(value = "购买金额或消费总金额")
    private BigDecimal expectAmount;

    @ApiModelProperty(value = "车牌号")
    private String vehicleNumber;

    @ApiModelProperty(value = "购买时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

    @ApiModelProperty(value = "浏览时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date browseTime;

}
