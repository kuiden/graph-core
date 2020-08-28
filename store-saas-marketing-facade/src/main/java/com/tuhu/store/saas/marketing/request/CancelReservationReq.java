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
public class CancelReservationReq implements Serializable {
    private static final long serialVersionUID = 5131714577428797145L;

    //门店ID
    private Long storeId;

    //预约单id
    private String id;

    /**
     * 取消终端(1:b端 2:c端小程序)
     */
    private Integer teminal;

}
