package com.tuhu.store.saas.marketing.response;


import com.github.pagehelper.Page;
import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/17
 * Time: 14:27
 * Description:
 */
@Data
public class CouponPageResp implements Serializable {

    private static final long serialVersionUID = 7388252667574322263L;
    private Page<CouponItemResp> couponItemResps;

    private PageInfo pageInfo;
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