package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/14 14:55
 */
@Data
public class ChangeSortAcTemplateReq implements Serializable {
    private static final long serialVersionUID = -9093864599502447208L;

    @ApiModelProperty(value = "活动模板id",required = true)
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "是否上移操作",required = true)
    private Boolean isUpGrade;

}
