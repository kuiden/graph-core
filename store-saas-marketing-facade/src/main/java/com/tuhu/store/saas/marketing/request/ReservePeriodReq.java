package com.tuhu.store.saas.marketing.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


@ApiModel(value="ReservePeriodReq对象", description="ReservePeriodReq对象")
@Data
public class ReservePeriodReq implements Serializable {
    private static final long serialVersionUID = -4133930607518728313L;

    /**
     * 门店id
     */
    @ApiModelProperty(value = "门店id")
    @NotNull(message = "门店id不能为空！")
    private Long storeId;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull(message = "日期不能为空！")
    private Date date;

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private String customerId;
}
