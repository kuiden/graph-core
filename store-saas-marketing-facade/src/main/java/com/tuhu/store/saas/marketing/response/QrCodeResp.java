package com.tuhu.store.saas.marketing.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @time 2020-08-03
 * @auther kudeng
 */
@Data
@ApiModel("获取二维码图片出参")
public class QrCodeResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("二维码图片url")
    private String url;

}
