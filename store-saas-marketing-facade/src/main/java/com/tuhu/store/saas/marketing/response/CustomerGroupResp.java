package com.tuhu.store.saas.marketing.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ToString
public class CustomerGroupResp implements Serializable {

    private Long id;
    //客群名称
    private String consumerGroupName;
    //门店ID
    private Long storeId;
    //无消费记录天数
    private Long noConsumerDay;
    //有消费记录天数
    private Long hasConsumerDay;
    //近今天消费次数
    private Long consumerTimeDay;
    //近今天最少消费次数
    private Long consumerLeastTime;
    //近几天最多消费次数
    private Long consumerMaxTime;
    //近几天消费金额
    private  Long consumerAmountDay;
    //近几天最小消费金额
    private Long consumerLeastAmount;
    //近几天最大消费金额
    private Long consumerMaxAmount;
    //近几天消费服务
    private Long consumerServeDay;
    //近几天消费服务列表
    private List<GoodsResp> consumerServeList;
    //开始创建时间
    private Date createDateStart;
    //创建时间结束值
    private Date createDateEnd;
    //生日开始时间
    private Date brithdayStart;
    //生日结束时间
    private Date brithdayEnd;
    //保养开始时间
    private Date maintenanceDateStart;
    //保养结束时间
    private Date maintenanceDateEnd;

    private String createUser;
    private String updateUser;
    //服务列表
   // private List<GoodsResp> serverList;

    private Long tenantId;

}
