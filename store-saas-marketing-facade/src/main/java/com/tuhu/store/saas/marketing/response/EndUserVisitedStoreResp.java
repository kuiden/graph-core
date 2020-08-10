package com.tuhu.store.saas.marketing.response;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户浏览过的门店信息
 */
public class EndUserVisitedStoreResp implements Serializable {
    private static final long serialVersionUID = 122976895699931914L;
    private Long id;
    private String storeName;
    private Byte statusType;
    private String imagePath;
    private String address;
    private String lineTelephone;
    private Double lon;
    private Double lat;
    private Date lastTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Byte getStatusType() {
        return statusType;
    }

    public void setStatusType(Byte statusType) {
        this.statusType = statusType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLineTelephone() {
        return lineTelephone;
    }

    public void setLineTelephone(String lineTelephone) {
        this.lineTelephone = lineTelephone;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
