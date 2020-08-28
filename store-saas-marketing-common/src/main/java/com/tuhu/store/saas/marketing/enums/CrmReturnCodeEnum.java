package com.tuhu.store.saas.marketing.enums;
/**
 * @Author: WangKun
 * @Description:
 * @Date: Created in 2018/9/21 下午2:33
 * @ProjectName: saas-crm
 * @Version: 1.0.0
 */
public enum CrmReturnCodeEnum {

    DATA_NOT_EXIST(4000, "数据不存在"),
    REQUEST_ARG_IS_EMPTY(4000, "请求参数为空"),
    REQUST_FORMAT_ERROR(4000, "请求参数格式错误"),
    SERVER_ERROR(5000, "服务异常"),
    MOBILE_FORMAT_ERROR(4000, "手机号码格式不匹配"),
    DATA_HAS_EXIST(4000, "数据已存在"),
	PHONE_NUMVER_ALREDAY_EXISTS(4000,"手机号已存在"),
    CUSTOMER_ID_MISSING(4001,"用户信息为空！"),
	;

    private int code;

    private String desc;

    CrmReturnCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
