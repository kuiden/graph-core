package com.tuhu.store.saas.marketing.request.card;

import com.tuhu.store.saas.marketing.request.BaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: yanglanqing
 * @Date: 2020/11/17 19:57
 */
@Data
public class QueryByCustomerIdReq extends BaseReq implements Serializable {

    @ApiModelProperty(value = "客户id (能拿到客户id就传)")
    private String customerId;

    @ApiModelProperty(value = "客户手机号", dataType = "String")
    private String customerPhoneNumber;

    /*
     * 关键字搜索
     */
    @ApiModelProperty(value = "关键字搜索")
    private String search;

    /*
     * 类型 1服务 2商品
     */
    @ApiModelProperty(value = "类型 1服务 2商品")
    private Integer type = 1;
}
