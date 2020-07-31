package com.tuhu.store.saas.marketing.response;


import com.github.pagehelper.Page;
import com.tuhu.store.saas.marketing.po.CustomerCouponPO;
import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/20
 * Time: 15:59
 * Description:
 */
@Data
public class CustomerCouponPageResp implements Serializable {
    private static final long serialVersionUID = -6813261517215702135L;
    private Page<CustomerCouponPO> customerCouponPOS;

    private CustomerCouponPageResp.PageInfo pageInfo;
    @Data
    public static class PageInfo{
        /**
         * 页码，从1开始
         */
        private int pageNum;
        /**
         * 页面大小
         */
        private int pageSize;

        /**
         * 总数
         */
        private long total;
        /**
         * 总页数
         */
        private int pages;

    }
}