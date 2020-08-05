package com.tuhu.store.saas.marketing.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class KeyResult {

    private long key;

    private Date time;

    public KeyResult(long key, Date time) {
        this.key = key;
        this.time = time;
    }

    public long getKey() {
        return key;
    }

    public Date getTime() {
        return time;
    }

    /**
     * 获取指定长度的key
     */
    public String getKey(int length) {
        String rsStr = this.zeroPadding(length);
        if(rsStr.length() > length){
            throw new RuntimeException("over length - this restriction can be remove as appropriate");
        }
        return rsStr;
    }

    /**
     * 获取指定格式time
     */
    public String getTime(String formatStr) {
        return (new SimpleDateFormat(formatStr)).format(time);
    }

    /**
     * 拼接'0'
     */
    private String zeroPadding(int length){

        String keyStr = String.valueOf(key);

        int leftLength = length - keyStr.length();

        for(int i = 0; i < leftLength; i++){
            keyStr = "0".concat(keyStr);
        }

        return keyStr;
    }
}
