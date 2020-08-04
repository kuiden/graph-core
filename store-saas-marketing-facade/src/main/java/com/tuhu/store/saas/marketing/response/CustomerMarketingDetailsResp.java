package com.tuhu.store.saas.marketing.response;

import com.tuhu.store.saas.marketing.response.dto.MarketingSendRecordDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: ZhangXiao
 * @Description:
 * @Date: Created in 2019/5/24
 * @ProjectName: saas-crm
 * @Version: 1.0.0
 */
@Data
public class CustomerMarketingDetailsResp implements Serializable {

    private static final long            serialVersionUID = -2269336564964009098L;
    /**
     * 发送记录list
     */
    private List<MarketingSendRecordDTO> recordList;

    private Long id;
    /**
     * 任务状态 0、待发送 1、已发送 2、已取消 3、发送失败
     */
    private Byte taskType;
    /**
     * 营销方式 0、优惠券关怀 1、短信营销
     */
    private Byte marketingMethod;
    /**
     * 优惠券发送短信标记 0、否 1、是
     */
    private Byte couponMessageFlag;
    /**
     * 发送对象
     */
    private String sendObject;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 客户群组表ID
     */
    private String customerGroupId;
    /**
     * 优惠券标题
     */
    private String couponTitle;
    /**
     * 券编ID
     */
    private  String couponId;
    /**
     * 优惠券编码
     */
    private String couponCode;
    /**
     * 备注
     */
    private String remark;
    /**
     * 短信模板名称
     */
    private String messageTemplate;
    /**
     * 短信模板表ID
     */
    private String messageTemplateId;
    /**
     * 短信模板内容
     */
    private String messageTemplateContent;
    /**
     * 短信模板变量
     */
    private String messageDatas;
    /**
     * 发送人数
     */
    private String sendNumber;
    /**
     * 消费额
     */
    private String consumption;
    /**
     * 开单数
     */
    private String orderNumber;
    /**
     * 开单消费额
     */
    private String orderConsumption;
    /**
     * 开卡数
     */
    private String cardNumber;
    /**
     * 开卡消费额
     */
    private String cardConsumption;
    /**
     * 送券数
     */
    private String sendCouponNumber;
    /**
     * 已用数（优惠券）
     */
    private String usedCouponNumber;
    /**
     * 用券工单金额
     */
    private String couponOrderAmount;


}
