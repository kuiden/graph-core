package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ToString
@ApiModel(value = "定向营销客群对象")
public class CustomerGroupReq implements Serializable {
    @ApiModelProperty("定向营客群ID")
    private Long id;
    @ApiModelProperty("客群名称")
    private String consumerGroupName;
    @ApiModelProperty("门店ID")
    private Long storeId;
    @ApiModelProperty("无消费记录天数")
    private Long noConsumerDay;
    @ApiModelProperty("有消费记录天数")
    private Long hasConsumerDay;
    @ApiModelProperty("消费次数近几天")
    private Long consumerTimeDay;
    @ApiModelProperty("近几天消费最少次数")
    private Long consumerLeastTime;
    @ApiModelProperty("近几天消费最多次数")
    private Long consumerMaxTime;
    @ApiModelProperty("消费金额近几天")
    private  Long consumerAmountDay;
    @ApiModelProperty("近几天最小消费金额")
    private Long consumerLeastAmount;
    @ApiModelProperty("近几天最大消费金额")
    private Long consumerMaxAmount;
    @ApiModelProperty("消费服务近几天")
    private Long consumerServeDay;
    @ApiModelProperty("近几天消费服务列表")
    private List<String> consumerServeList;
    @ApiModelProperty("创建时间大于天数")
    private Long createDateStart;
    @ApiModelProperty("创建时间小于天数")
    private Long createDateEnd;
    @ApiModelProperty("生日开始月份")
    private Long brithdayStart;
    @ApiModelProperty("生日结束月份")
    private Long brithdayEnd;
    @ApiModelProperty("保养开始时间")
    private Date maintenanceDateStart;
    @ApiModelProperty("保养结束时间")
    private Date maintenanceDateEnd;
    private Long tenantId;
    private String createUser;
    private String updateUser;
}
