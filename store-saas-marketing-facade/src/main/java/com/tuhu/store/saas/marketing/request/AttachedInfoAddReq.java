package com.tuhu.store.saas.marketing.request;

import com.tuhu.store.saas.marketing.request.seckill.AttachedInfoTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "营销信息添加实体")
@Data
public class AttachedInfoAddReq implements Serializable {

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容",required = true,example = "内容")
    private String content;
    /**
     * 类型
     */
    @ApiModelProperty(value = "类型 SECKILLACTIVITYRULESINFO：秒杀活动规则 SECKILLACTIVITYSTOREINFO 秒杀活动门店类型 ",
            required = true,example = "类型 SECKILLACTIVITYRULESINFO：秒杀活动规则 SECKILLACTIVITYSTOREINFO 秒杀活动门店类型")
    private AttachedInfoTypeEnum type;
}
