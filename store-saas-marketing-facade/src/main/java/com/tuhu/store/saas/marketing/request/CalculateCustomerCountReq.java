package com.tuhu.store.saas.marketing.request;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class CalculateCustomerCountReq extends  BaseReq  implements Serializable {
    private List<Long>  groupList;
}
