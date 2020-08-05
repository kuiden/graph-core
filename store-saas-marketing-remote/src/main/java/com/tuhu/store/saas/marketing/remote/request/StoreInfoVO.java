package com.tuhu.store.saas.marketing.remote.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jiangyuhang
 * @date 2018/11/1615:56
 */
@Data
public class StoreInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 门店id
     */
    private Long storeId;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 租户id
     */
    private Long tanentId;
}
