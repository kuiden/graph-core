package com.tuhu.store.saas.marketing.response;

import com.github.pagehelper.Page;
import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/6/4
 * Time: 13:47
 * Description:
 */
@Data
public class ActivityCustomerPageResp implements Serializable {
    private static final long serialVersionUID = 8308872956428268816L;
    private Page<ActivityCustomerItem> activityCustomerItems;

    private ActivityCustomerPageResp.PageInfo pageInfo;
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