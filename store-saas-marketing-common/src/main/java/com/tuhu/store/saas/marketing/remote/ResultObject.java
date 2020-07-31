package com.tuhu.store.saas.marketing.remote;

/**
 * restfulApi 对外返回对象
 * Created by wxy on 2018/7/1 0001.
 */
public class ResultObject {
    private int code = 1000;
    private String message;
    private Object data;

    public ResultObject() {
    }

    public ResultObject(Object data) {
        this.data = data;
    }

    public ResultObject(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultObject(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
