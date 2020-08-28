package com.tuhu.store.saas.marketing.request.card;

import com.tuhu.store.saas.marketing.request.BaseReq;
import lombok.Data;

import java.io.Serializable;

@Data
public class CardTemplateReq extends BaseReq implements Serializable {

    private Integer pageSize = 1;

    private Integer pageNum = 10;

    private String query;

    private String status;
}
