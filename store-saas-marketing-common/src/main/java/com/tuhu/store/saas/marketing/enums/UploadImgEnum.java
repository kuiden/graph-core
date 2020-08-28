package com.tuhu.store.saas.marketing.enums;

/**
 * 上传图片，文件索引名枚举值
 * @author liuya
 * @date 2020/3/17
*/
public enum UploadImgEnum   {

    USER("user", "C端用户相关文件"),
    MERCHANT("merchant", "商户相关文件"),
    PRODUCT("product", "商品相关文件"),
    PROMOTION("promotion", "促销活动相关文件"),
    ORDER("order", "订单相关文件"),
    AFTERSALES("aftersales", "售后相关文件"),
    FINANCE("finance", "财务相关文件"),
    OTHERS("others", "其他文件"),;

    private String code;
    private String desc;

    private UploadImgEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }
}
