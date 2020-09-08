package com.tuhu.store.saas.marketing.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class ComputeMarktingCustomerForReportResp  implements Serializable {
    /**
     * 分组名称 order 工单  cardConsumption  次卡消费记录
     */
    private String groupName;

    /**
     * 产生记录时间
     */
    private Date date;

    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 核销时间
     */
    private  Date useTime;
}
