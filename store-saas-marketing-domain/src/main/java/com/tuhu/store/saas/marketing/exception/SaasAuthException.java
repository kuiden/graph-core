package com.tuhu.store.saas.marketing.exception;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import lombok.Getter;

@Getter
public class SaasAuthException extends BizException {

    private static final long serialVersionUID = 5244963021300594790L;
    private int code;
    private String message;

    public SaasAuthException() {
        super();
    }

    public SaasAuthException(String message) {
        super(BizErrorCodeEnum.OPERATION_FAILED, message);
        this.message = message;
    }

    public SaasAuthException(BizErrorCodeEnum bizErrorCodeEnum) {
        super(bizErrorCodeEnum);
        this.code = super.getErrorCode().getCode();
        this.message = bizErrorCodeEnum.getDesc();
    }

    public SaasAuthException(BizErrorCodeEnum bizErrorCodeEnum, String message) {
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
