package com.tuhu.store.saas.marketing.request.card;

import com.tuhu.store.saas.marketing.request.BaseReq;
import lombok.Data;

import java.io.Serializable;

@Data
public class CardTemplateReq extends BaseReq implements Serializable {

    private Integer pageSize = 10;

    private Integer pageNum = 0;

    private String query;

    private String status;

    private Byte isShow;
}
