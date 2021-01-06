package com.tuhu.store.saas.marketing.enums;

import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import lombok.Getter;

/**
 * 抢购平台
 *
 * @author wangxiang2
 */

@Getter
public enum ShoppingPlatformEnum {
    H5("H5", "H5页面抢购"),
    WECHAT_APPLET("WECHAT_APPLET", "微信小程序抢购");

    private String code;
    private String description;

    ShoppingPlatformEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ShoppingPlatformEnum getFromName(String name) {
        for (ShoppingPlatformEnum initStatusEnum : values()) {
            if (initStatusEnum.getCode().equals(name)) {
                return initStatusEnum;
            }
        }
        throw new StoreSaasMarketingException("PaymentStatusEnum not exists.PaymentStatusEnum name is " + name);
    }

    public static ShoppingPlatformEnum getByCode(String code) {
        for (ShoppingPlatformEnum initStatusEnum : values()) {
            if (initStatusEnum.getDescription().equals(code)) {
                return initStatusEnum;
            }
        }
        throw new StoreSaasMarketingException("PaymentStatusEnum not exists.PaymentStatusEnum code is " + code);
    }
}
