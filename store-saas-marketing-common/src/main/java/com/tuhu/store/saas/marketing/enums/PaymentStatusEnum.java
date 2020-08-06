package com.tuhu.store.saas.marketing.enums;

import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import lombok.Getter;

/**
 * @author jiangyuhang
 * @date 2018/11/2210:42
 */
@Getter
public enum PaymentStatusEnum {
    //付款状态:未收款(PAYMENT_NOT);部分收款(PAYMENT_SOME);已结清(PAYMENT_OK)
    PAYMENT_NOT("PAYMENT_NOT", "未收款"),
    PAYMENT_SOME("PAYMENT_SOME", "部分收款"),
    PAYMENT_OK("PAYMENT_OK", "已结清");
    private String enumCode;

    private String description;

    PaymentStatusEnum(String enumCode, String description) {
        this.enumCode = enumCode;
        this.description = description;
    }

    public static PaymentStatusEnum getFromName(String name) {
        for (PaymentStatusEnum initStatusEnum: values()) {
            if (initStatusEnum.getEnumCode().equals(name)) {
                return initStatusEnum;
            }
        }
        throw new StoreSaasMarketingException("PaymentStatusEnum not exists.PaymentStatusEnum name is " + name);
    }

    public static PaymentStatusEnum getByCode(String code) {
        for (PaymentStatusEnum initStatusEnum : values()) {
            if (initStatusEnum.getDescription().equals(code)) {
                return initStatusEnum;
            }
        }
        throw new StoreSaasMarketingException("PaymentStatusEnum not exists.PaymentStatusEnum code is " + code);
    }
}
