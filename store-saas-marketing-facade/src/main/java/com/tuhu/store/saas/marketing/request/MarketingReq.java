package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "定向营销任务列表对象")
public class MarketingReq implements Serializable {

    private static final long serialVersionUID = 5083772444238781144L;
    private Long id;

    @NotNull(message = "pageNum不能为空")
    @Min(0)
    private Integer pageNum = 0;

    @NotNull(message = "pageSize不能为空")
    @Min(1)
    private Integer pageSize = 10;

    private Long storeId;

    private Long tenantId;

    private Boolean isHide = Boolean.FALSE;

}
