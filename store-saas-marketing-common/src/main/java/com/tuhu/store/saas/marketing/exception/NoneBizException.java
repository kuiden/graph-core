package com.tuhu.store.saas.marketing.exception;

import com.tuhu.boot.common.enums.BizEnum;
import com.tuhu.store.saas.marketing.enums.MarketingBizErrorCodeEnum;
import lombok.Getter;

/**
 * 非业务异常
 */
@Getter
public class NoneBizException extends RuntimeException {
    private static final long serialVersionUID = -5532982887667351094L;
    /**
     * 错误代码
     */
    private int code = MarketingBizErrorCodeEnum.SYSTEM_INNER_ERROR.getCode();

    public NoneBizException() {
        super();
    }

    public NoneBizException(String message) {
        super(message);
    }

    public NoneBizException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.code = errorCode;
    }

    public NoneBizException(BizEnum errorCode, String errorMessage, Throwable exception) {
        super(errorMessage);
        this.code = errorCode.getCode();
        super.initCause(exception);
    }

    /**
     * 重新fillInStackTrace方法，不填充异常堆栈
     *
     * @return
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
