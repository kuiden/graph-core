package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/6/5
 * Time: 10:37
 * Description:
 */
@Data
@ApiModel("获取二维码图片入参")
public class ActivityQrCodeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("活动id")
    @NotNull(message = "活动id不能为空")
    private Long activityId;
    /**
     * 参数"activityId=222"
     */
    private String scene;
    @ApiModelProperty("页面路径")
    private String path;

    @ApiModelProperty("二维码宽度")
    private Long width;
}