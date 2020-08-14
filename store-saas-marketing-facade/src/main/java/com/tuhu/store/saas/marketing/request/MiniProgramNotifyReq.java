package com.tuhu.store.saas.marketing.request;


import java.io.Serializable;

/**
 * 小程序模板消息通知请求
 */
public class MiniProgramNotifyReq implements Serializable {
    private static final long serialVersionUID = 710612399882136949L;

    /**
     * 客户端类型
     */
    private String clientType;

    /**
     * 小程序code
     */
//    @NotEmpty(message = "用户小程序code不能为空")
    private String openIdCode;
    /**
     * 小程序openId
     */
    private String openId;

    /**
     * 门店客户ID
     */
    private String customerId;

    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 小程序页面url
     */
    private String page;

    /**
     * 表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id
     */
    private String formId;

    /**
     * 模板内容，不填则下发空模板
     */
    private Object data;
    /**
     * 模板需要放大的关键词，不填则默认无放大
     */
    private String emphasisKeyword;




    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getOpenIdCode() {
        return openIdCode;
    }

    public void setOpenIdCode(String openIdCode) {
        this.openIdCode = openIdCode;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getEmphasisKeyword() {
        return emphasisKeyword;
    }

    public void setEmphasisKeyword(String emphasisKeyword) {
        this.emphasisKeyword = emphasisKeyword;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
