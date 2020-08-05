package com.tuhu.store.saas.marketing.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * C端用户行为事件结果
 */
@Data
@ToString
public class ClientEventRecordResp implements Serializable {
    private static final long serialVersionUID = -6579829525478878909L;

    /**
     * 事件类型:visit,registered,login,wechatForward
     */
    private String eventType;

    /**
     * 主题类型:coupon,store,activity
     */
    private String contentType;

    /**
     * 主题值:couponId,storeId,activityId
     */
    private String contentValue;

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 事件计数
     */
    private Long eventCount;

    /**
     * 用户计数
     */
    private Long userCount;
}
