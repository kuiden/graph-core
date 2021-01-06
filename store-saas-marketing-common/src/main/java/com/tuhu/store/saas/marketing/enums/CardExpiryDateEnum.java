package com.tuhu.store.saas.marketing.enums;

import lombok.Getter;
import org.bouncycastle.asn1.cmc.IdentityProofV2;

/**
 * @time 2020-12-08
 * @auther kudeng
 */
@Getter
public enum CardExpiryDateEnum {

    EXPIRE_MONTH(0, "有效月数"),
    FOREVER(1, "永久有效"),
    EXPIRE_DAY(2, "有效天数"),
    EXPIRE_DATE(3, "截止日期");

    private Integer code;
    private String desc;

    CardExpiryDateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CardExpiryDateEnum getEnumByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (CardExpiryDateEnum cardExpiryDateEnum : CardExpiryDateEnum.values()) {
            if (cardExpiryDateEnum.getCode().equals(code)) {
                return cardExpiryDateEnum;
            }
        }
        return null;
    }
}
