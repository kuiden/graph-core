package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @time 2020-12-04
 * @auther kudeng
 */
@Data
@ApiModel(value = "秒杀活动模板详情")
public class EditSecKillTempReq implements Serializable {

    private static final long serialVersionUID = 1l;

    @ApiModelProperty("秒杀活动模板id")
    @NotBlank(message = "模板id不能为空")
    private String id;

    @ApiModelProperty(value = "活动名称", required = true)
    @NotBlank(message = "活动名称不能为空")
    @Size(max = 50, message = "活动标题最多可输入50个汉字")
    private String activityTitle;

    @ApiModelProperty(value = "活动头图url", required = true)
    @NotBlank(message = "活动头图不能为空")
    private String picUrl;

    @ApiModelProperty("活动规则")
    @NotBlank(message = "活动规则不能为空")
    @Size(max = 1000, message = "活动规则最多可输入1000个汉字")
    private String activityIntroduce;

    @ApiModelProperty("活动分类id")
    private Integer classificationId;

    @ApiModelProperty("模板状态，0：禁用，1：启用")
    private Integer status;

    @ApiModelProperty("手动排序字段")
    private Integer sort;

    @ApiModelProperty("删除标识 0未删除 1删除")
    private Integer isDelete;

    @ApiModelProperty(value = "秒杀活动模板明细", required = true)
    @NotNull(message = "至少填写一项商品或服务")
    private List<EditSeckillTempItemReq> editTempItemList;


}
