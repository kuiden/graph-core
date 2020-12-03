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

    @ApiModelProperty(value = "客戶id")
    private String customersId;

    @ApiModelProperty(value = "客户手机")
    private String phone;

    @ApiModelProperty(value = "客户名称")
    private String name;

    @ApiModelProperty(value = "是否新客户 true 新客户 false 非新客户")
    private boolean newCustomers;

    @ApiModelProperty(value = "消费金额（元）")
    private BigDecimal amount = BigDecimal.ZERO;

    @ApiModelProperty(value = "状态 购买N次")
    private String statusName;

    @ApiModelProperty(value = "浏览时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date browseTime;

}
