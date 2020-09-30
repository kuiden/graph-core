package com.tuhu.store.saas.marketing.request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author wangxiang2
 */
@ApiModel(value="queryCardToCommissionReq 对象", description="queryCardToCommissionReq 对象")
@Data
public class QueryCardToCommissionReq implements Serializable {

    private static final long serialVersionUID = -806257626563429876L;

    /**
     *开始时间（创建时间）
     */
    @ApiModelProperty(value = "开始时间（创建时间）")
    private Date startTime;


    /**
     *结束时间（创建时间）
     */
    @ApiModelProperty(value = "结束时间（创建时间）")
    private Date endTime;

}

