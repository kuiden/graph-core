package com.tuhu.store.saas.marketing.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/15
 * Time: 14:29
 * Description:
 */
@Data
public class SendCouponRequest {
    @NotNull(message = "抵用券Code不能为空")
    private List<String>  couponCodeList;
    @NotNull(message = "客户ID不能为空")
    private List<String> customerIdList;
    private String sendUser;
    /*
    0：主动在线领取 1：手动发券 2：营销发券
     */
    private int receiveType;

}