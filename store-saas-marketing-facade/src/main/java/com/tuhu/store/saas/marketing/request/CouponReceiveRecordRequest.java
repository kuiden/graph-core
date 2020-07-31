package com.tuhu.store.saas.marketing.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/14
 * Time: 15:34
 * Description:抵用券领取记录req
 */

@Data
public class CouponReceiveRecordRequest implements Serializable {
    private static final long serialVersionUID = 1952538538504754596L;
    //    @NotNull(message = "抵用券ID不能为空")
    private Long couponId;
    @NotNull(message = "抵用券code不能为空")
    private String couponCode;
    /*
    领取类型：0：主动在线领取 1：手动发券 2：营销发券
     */
    private Integer receiveType ;

    /*
    客户列表搜索关键词，手机号码、姓名
     */
    private String searchKey;


    private Long storeId;
    /**
     * 使用状态 0:未使用 1：已使用
     */
    private Byte useStatus;
    private Integer pageNum=1;
    private Integer pageSize=15;
}