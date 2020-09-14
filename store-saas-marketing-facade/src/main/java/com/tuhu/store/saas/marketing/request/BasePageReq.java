package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 基础分页信息
 */
@Data
public class BasePageReq {
    /**
     * 当前页码，默认当前页
     */
    @ApiModelProperty(value = "页码（0开始）", example = "1")
    private Integer pageNum = 0;

    /**
     * 分页长度,默认10
     */
    @ApiModelProperty(value = "分页长度,默认10", example = "10")
    private Integer pageSize = 10;
}
