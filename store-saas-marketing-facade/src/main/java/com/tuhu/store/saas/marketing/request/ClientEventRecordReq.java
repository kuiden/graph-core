package com.tuhu.store.saas.marketing.request;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * C端用户事件请求
 */
@Data
@ToString
public class ClientEventRecordReq implements Serializable {
    private static final long serialVersionUID = -445563267634672563L;

    /**
     * 事件类型:visit,registered,login,wechatForward
     */
    private List<String> eventTypes;

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
}
