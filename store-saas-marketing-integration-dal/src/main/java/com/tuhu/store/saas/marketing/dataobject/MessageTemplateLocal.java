package com.tuhu.store.saas.marketing.dataobject;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageTemplateLocal implements Serializable {

    private static final long serialVersionUID = -2330777052947468022L;
    private String id;

    /**
     * 类型：公共=PUBLIC,私有=PRIVATE
     */
    private String type;

    /**
    * 模板编码
    */
    private String templateCode;

    /**
     * 短息平台短信模板id
     */
    private String templateId;

    /**
    * 模板名称
    */
    private String templateName;

    /**
    * 模板类型
    */
    private String templateType;

    /**
    *  备注
    */
    private String comment;

    /**
    *  原始模板ID
    */
    private Long originalTemplateId;

    /**
    * 门店ID
    */
    private Long storeId;

    /**
    * 租户ID
    */
    private Long tenantId;

    /**
    * 创建人
    */
    private String createUser;

    /**
    * 更新人
    */
    private String updateUser;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 更新时间
    */
    private Date updateTime;

    /**
    * 是否删除
    */
    private Boolean isDelete;

    /**
    * 模板内容
    */
    private String templateContent;

}
