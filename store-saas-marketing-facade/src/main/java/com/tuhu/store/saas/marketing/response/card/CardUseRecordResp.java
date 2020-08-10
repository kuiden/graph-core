package com.tuhu.store.saas.marketing.response.card;

import lombok.Data;

import java.util.List;

/**
 * @author wangyuqing
 * @since 2020/8/7 17:16
 */
@Data
public class CardUseRecordResp {

    /*
     * 消费时间
     */
    private String time;

    /*
     * 服务项目列表
     */
    private List<CardItemResp> item;

    /*
     * 工单id
     */
    private String serviceOrderId;

}
