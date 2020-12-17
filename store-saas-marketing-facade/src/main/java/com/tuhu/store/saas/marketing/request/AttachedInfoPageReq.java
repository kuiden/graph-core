package com.tuhu.store.saas.marketing.request;

import com.tuhu.store.saas.marketing.request.seckill.AttachedInfoTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("查询实体")
public class AttachedInfoPageReq extends BaseReq implements Serializable {

    @ApiModelProperty("外键ID")
    private String foreignKey;
    @ApiModelProperty(value = "类型 SECKILLACTIVITYRULESINFO：秒杀活动规则 SECKILLACTIVITYSTOREINFO 秒杀活动门店类型 ",
            required = true, example = "类型 SECKILLACTIVITYRULESINFO：秒杀活动规则 SECKILLACTIVITYSTOREINFO 秒杀活动门店类型")
    private AttachedInfoTypeEnum type;

    /**
     * 当前页码，默认当前页
     */
    @ApiModelProperty(value = "页码（0开始）", example = "1")
    private Integer pageNum = 0;

    /**
     * 分页长度,默认10
     */
    @ApiModelProperty(value = "分页长度,默认10", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "查询类容")
    private String query;
}
