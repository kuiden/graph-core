package com.tuhu.store.saas.marketing.response.seckill;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @time 2020-12-04
 * @auther kudeng
 */
@Data
@ApiModel(value = "秒杀活动模板详情")
public class SeckillTempDetailResp implements Serializable {

    private static final long serialVersionUID = 1l;

    @ApiModelProperty("秒杀活动模板id")
    private String id;

    @ApiModelProperty("活动名称")
    private String activityTitle;

    @ApiModelProperty("活动头图url")
    private String picUrl;

    @ApiModelProperty("活动规则")
    private String activityIntroduce;

    @ApiModelProperty("活动分类id")
    private Integer classificationId;

    @ApiModelProperty("活动分类名称")
    private String classificationName;

    @ApiModelProperty("模板状态，0：禁用，1：启用")
    private Integer status;

    @ApiModelProperty("手动排序字段")
    private Integer sort;

    @ApiModelProperty("删除标识 0未删除 1删除")
    private Integer isDelete;

    @ApiModelProperty("秒杀活动明细")
    private List<SeckillTempItemResp> tempItemList;

}
