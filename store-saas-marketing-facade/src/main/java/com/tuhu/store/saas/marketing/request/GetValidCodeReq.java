package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @time 2020-08-10
 * @auther kudeng
 */
@Data
@ApiModel("获取短信验证码请求类")
public class GetValidCodeReq implements Serializable {

    public static final long serialVersionUID = 1L;

    @ApiModelProperty("客户手机号")
    @NotBlank(message = "客户手机号不能为空")
    private String phone;

    @ApiModelProperty("验证码请求类型：0-H5营销活动报名，1-H5领取优惠卷，2-客户预约")
    @NotBlank(message = "验证码请求类型不能为空")
    private Integer codeType;

}
