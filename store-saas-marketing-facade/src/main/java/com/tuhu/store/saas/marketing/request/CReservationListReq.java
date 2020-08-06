package com.tuhu.store.saas.marketing.request;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/5 10:47
 */
@Data
@ToString
public class CReservationListReq implements Serializable {
    private static final long serialVersionUID = 5131714577428797145L;

    //客户id
    private String customerId;

    //门店ID
    private Long storeId;

    //预约单id
    private String id;

}
