package com.tuhu.store.saas.marketing.remote.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 车主用户访问门店记录
 */
public class EndUserVisitedStoreReq implements Serializable {
    private static final long serialVersionUID = 6559622989267227955L;
    /**
     * 门店ID
     */
    private String storeId;
    /**
     * 访问次数
     */
    private Long count;
    /**
     * 最后访问时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastTime;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
