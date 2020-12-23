package com.tuhu.store.saas.marketing.request.seckill;

import lombok.Data;
import java.io.Serializable;


@Data
public class SeckillRecordUpdateReq implements Serializable {
    private Long storeId;

    private Long tenantId;

    private String id;
    /**
     * 待收单id
     */
    private String finReceivingId;
    /**
     * 流水id
     */
    private String finPaymentJournalId;
    /**
     * 源单ID
     */
    private String orderId;

    private String Status;

}
