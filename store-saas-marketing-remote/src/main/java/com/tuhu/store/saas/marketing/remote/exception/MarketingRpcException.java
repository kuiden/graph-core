package com.tuhu.store.saas.marketing.remote.exception;

/**
 * @time 2020-08-03
 * @auther kudeng
 */
public class MarketingRpcException extends RuntimeException {

    public MarketingRpcException() {
    }

    public MarketingRpcException(String message) {
        super(message);
    }

    public MarketingRpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarketingRpcException(Throwable cause) {
        super(cause);
    }

}
