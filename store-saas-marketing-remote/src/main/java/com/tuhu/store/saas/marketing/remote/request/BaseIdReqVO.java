package com.tuhu.store.saas.marketing.remote.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuanwenjun
 * @date 2019/12/5 16:46
 */
@Data
public class BaseIdReqVO implements Serializable {

    private static final long serialVersionUID = 8451170200850480798L;

    private Long storeId;

    private Long tenantId;

    private String id;
}
