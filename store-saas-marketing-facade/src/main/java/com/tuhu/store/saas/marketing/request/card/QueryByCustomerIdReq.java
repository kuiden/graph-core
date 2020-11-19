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

    @ApiModelProperty(value = "客户手机号 ", dataType = "String")
    private String customerPhoneNumber;

    /*
     * 关键字搜索
     */
    private String search;
}
