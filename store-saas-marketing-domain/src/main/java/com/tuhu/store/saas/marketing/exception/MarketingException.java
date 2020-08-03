package com.tuhu.store.saas.marketing.exception;

import com.tuhu.store.saas.marketing.remote.exception.MarketingRpcException;

/**
 * @time 2020-08-03
 * @auther kudeng
 */
public class MarketingException extends MarketingRpcException {

    public MarketingException() {
    }

    public MarketingException(String message) {
        super(message);
    }

    public MarketingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarketingException(Throwable cause) {
        super(cause);
    }

}
