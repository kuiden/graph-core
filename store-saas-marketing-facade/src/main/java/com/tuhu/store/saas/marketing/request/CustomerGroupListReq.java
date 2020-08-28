package com.tuhu.store.saas.marketing.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class CustomerGroupListReq extends BaseReq implements Serializable {

    private Integer pageSize = 1;

    private Integer pageNum = 10;

    private String query;

    private String status;
}
