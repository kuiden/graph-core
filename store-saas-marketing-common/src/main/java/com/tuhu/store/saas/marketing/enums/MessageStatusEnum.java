/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.enums;

import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xuechaofu
 * @date 2018/11/1513:41
 */
public enum MessageStatusEnum {
    MESSAGE_WAIT("message_wait", "发送等待"),
    MESSAGE_SUCCESS("message_success", "发送成功"),
    MESSAGE_FAIL("message_fail", "发送失败");

    private String code;
    private String desc;

    MessageStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    MessageStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return name();
    }

    public String getDesc() {
        return this.desc;
    }

    public static MessageStatusEnum getFromName(String name) {
        for (MessageStatusEnum messageStatusEnum: values()) {
            if (StringUtils.equals(messageStatusEnum.getCode(), name)) {
                return messageStatusEnum;
            }
        }
        throw new StoreSaasMarketingException("MessageStatusEnum not exists.MessageStatusEnum name is " + name);
    }

    public static MessageStatusEnum getByCode(String code) {
        for (MessageStatusEnum messageStatusEnum : values()) {
            if (messageStatusEnum.getCode().equals(code)) {
                return messageStatusEnum;
            }
        }
        throw new StoreSaasMarketingException("MessageStatusEnum not exists.MessageStatusEnum code is " + code);
    }
}
