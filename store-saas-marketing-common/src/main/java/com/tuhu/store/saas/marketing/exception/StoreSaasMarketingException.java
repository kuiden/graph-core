package com.tuhu.store.saas.marketing.exception;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.store.saas.marketing.enums.MarketingBizErrorCodeEnum;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class StoreSaasMarketingException extends BizException {

    private static final long serialVersionUID = 5244963021300594790L;
    private int code;
    private String message;

    public StoreSaasMarketingException() {
        super();
    }

    public StoreSaasMarketingException(int code, String message) {
        super(MarketingBizErrorCodeEnum.OPERATION_FAILED, message);
        this.code = code;
        this.message = message;
    }

    public StoreSaasMarketingException(String message) {
        super(MarketingBizErrorCodeEnum.OPERATION_FAILED, message);
        this.message = message;
    }

    public StoreSaasMarketingException(BizErrorCodeEnum bizErrorCodeEnum) {
        super(bizErrorCodeEnum);
        this.code = super.getErrorCode().getCode();
        this.message = bizErrorCodeEnum.getDesc();
    }

    public StoreSaasMarketingException(BizErrorCodeEnum bizErrorCodeEnum, String message) {
        super(bizErrorCodeEnum, message);
        this.code = super.getErrorCode().getCode();
        this.message = super.getErrorCode().getDesc();
    }


    public StoreSaasMarketingException(MarketingBizErrorCodeEnum marketingBizErrorCodeEnum, String message) {
        super(marketingBizErrorCodeEnum, message);
        this.code = marketingBizErrorCodeEnum.getCode();
        this.message = message;
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
