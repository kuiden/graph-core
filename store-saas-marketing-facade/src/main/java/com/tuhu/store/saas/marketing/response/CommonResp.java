package com.tuhu.store.saas.marketing.response;

import java.io.Serializable;

/**
 * 通用结果
 *
 * @param <T>
 */
public class CommonResp<T> implements Serializable {

    private static final long serialVersionUID = -4514766599947160035L;

    private int code = 1000;
    private String message;
    private T data;

    private boolean success = true;

    public CommonResp() {
    }

    public CommonResp(T data) {
        this.data = data;
    }

    public CommonResp(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        if (1000 != code) {
            this.success = false;
        }
    }

    public static CommonResp systemErr(String message) {
        return new CommonResp(5000, message, null);
    }

    public static CommonResp failed(int code, String message) {
        return new CommonResp(code, message, null);
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
