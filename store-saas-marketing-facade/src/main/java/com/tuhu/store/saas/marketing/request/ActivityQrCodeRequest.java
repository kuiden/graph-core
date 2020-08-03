package com.tuhu.store.saas.marketing.request;

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
public class ActivityQrCodeRequest implements Serializable {
    @NotNull(message = "活动id不能为空")
    private Long activityId;
    /**
     * 参数"activityId=222"
     */
    private String scene;
    /**
     * 小程序页面路径
     */
    private String path;
    /**
     * 二维码宽度
     */
    private Long width;
}