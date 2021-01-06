package com.tuhu.store.saas.marketing.remote.request;

import com.tuhu.springcloud.common.util.Money;
import lombok.Data;

/**
 * @author wangxiang2
 */
@Data
public class CashierRequestVO {
    /**
     * tag:支付产品编码表获得
     * 支付产品类别编码:如100，101，102，103等
     */
    private String productCategory;
    /**
     * tag:支付产品编码表获得
     * 支付类型：如PAY,REFUND,WITHDRAW
     */
    private String paymentType;
    /**
     * 外部业务流水号 productCategory+paymentType+outBizNo联合唯一
     */
    private String outBizNo;
    /**
     * tag:支付产品编码表获得
     * 终端类型，标志发起支付请求的不同终端:Android,iOS,PC,MOBILE等
     */
    private String terminalType;
    /**
     * 来源系统编码，支付请求发起系统英文名称
     */
    private String sourceSystemCode;
    /**
     * tag:支付产品编码表获得
     * 支付请求的发起平台如：10，11，12
     */
    private String requestedPlatformCode;
//    /**
//     * 商品展示网址
//     */
//    private String productShowUrl;
    /**
     * 订单描述
     */
    private String orderDesc;
    /**
     * 订单金额，单位分
     */
    private Money orderAmount;
    /**
     * 支付金额，单位分
     */
    private Money payAmount;
    /**
     * 支付发起时间，System.currentTimeMillis()
     */
    private long payTime;
    /**
     * 商品描述，多商品拼接描述
     */
    private String productName;
    /**
     * 产品订单包含的产品子项，可为空
     */
//    private List<ProductItemDTO> productItemList;
    /**
     * 用户来源：汽配龙用户，快修云用户等
     */
    private String userSource;
    /**
     * 客户端用户的id,途虎用户id
     */
    private String userId;
    /**
     * 虚拟商品为true，实体类商品为false或空
     */
    private String virtual;
    /**
     * 支付完成后会跳地址，前端回调
     */
    private String returnUrl;
    /**
     * 支付完成异步通知地址，后端回调，支付状态以此为准
     */
    private String notifyUrl;
    //    /**
//     * 订单类型
//     */
//    private String orderCategory;
//    /**
//     * 不支持的支付方式，用英文逗号隔开
//     */
//    private String unsupportedPay;
//    /**
//     * 支持的支付方式，用英文逗号隔开
//     */
    private String supportedPay;

    /**
     * 企业id
     */
    private String companyId;

//    /**
//     * 订单生成时间 时间戳
//     */
//    private String orderStartTime;
//    /**
//     * 订单国企时间，时间戳
//     */
//    private String orderExpireTime;
//    /**
//     * 合并支付的订单信息
//     */
//    private List<BizOrderInfoDTO> bizOrderInfos;
}
