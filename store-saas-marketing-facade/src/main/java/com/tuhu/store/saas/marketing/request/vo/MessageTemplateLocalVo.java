package com.tuhu.store.saas.marketing.request.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageTemplateLocalVo  implements Serializable {

    private Long storeId;

    private String templateCode;
}
