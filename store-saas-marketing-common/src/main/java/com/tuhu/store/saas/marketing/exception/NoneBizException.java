package com.tuhu.store.saas.marketing.exception;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.store.saas.marketing.enums.MarketingBizErrorCodeEnum;
import lombok.Getter;

/**
 * 非业务异常
 */
@Getter
public class NoneBizException extends BizException {
    private static final long serialVersionUID = -5532982887667351094L;

    private int code;
    private String message;

    public NoneBizException() {
        super();
    }

    public NoneBizException(int code, String message) {
        super(MarketingBizErrorCodeEnum.OPERATION_FAILED, message);
        this.code = code;
        this.message = message;
    }

    public NoneBizException(String message) {
        super(MarketingBizErrorCodeEnum.OPERATION_FAILED, message);
        this.message = message;
    }

    public NoneBizException(BizErrorCodeEnum bizErrorCodeEnum) {
        super(bizErrorCodeEnum);
        this.code = super.getErrorCode().getCode();
        this.message = bizErrorCodeEnum.getDesc();
    }

    public NoneBizException(BizErrorCodeEnum bizErrorCodeEnum, String message) {
        super(bizErrorCodeEnum, message);
        this.code = super.getErrorCode().getCode();
        this.message = super.getErrorCode().getDesc();
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
