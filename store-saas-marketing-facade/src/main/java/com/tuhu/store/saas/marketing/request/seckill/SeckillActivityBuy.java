package com.tuhu.store.saas.marketing.request.seckill;

import lombok.Data;

import java.util.Date;

@Data
public class SeckillActivityBuy {
    private String activityId;
    private String customerId;
    private String phone;
    private Integer num = 0;
    private Date startTime;
    private Date endTime;
    private Integer totalNum = 0;//总数
    private Integer buyNum = 0;//已购买数量
    private Integer saleNum = 0; //可售数量 = 总数 - 已购买数量
}
