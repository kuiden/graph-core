package com.tuhu.store.saas.marketing.response.seckill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wangyuqing
 * @since 2020/12/7 11:41
 */
@Data
public class SeckillRecordListResp {

    @ApiModelProperty(value = "车牌号")
    private String vehicleNumber;

    @ApiModelProperty(value = "购买数量")
    private Long quantity;

    @ApiModelProperty(value = "购买时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

}
