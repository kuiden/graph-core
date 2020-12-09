package com.tuhu.store.saas.marketing.request.card;

import com.tuhu.store.saas.marketing.request.BaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CardTemplateReq extends BaseReq implements Serializable {

    private Integer pageSize = 10;

    private Integer pageNum = 0;

    private String query;

    @ApiModelProperty(value = "模板状态", dataType = "String", required = false, example = "ENABLE")
    private String status;

    private Byte isShow;

    @ApiModelProperty(value = "卡类型 1次卡 2 活动创建的卡模板", dataType = "Byte", required = false, example = "1")
    private Byte type;
}
