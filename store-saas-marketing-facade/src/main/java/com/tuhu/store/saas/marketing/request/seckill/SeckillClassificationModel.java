package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
@Data
@ApiModel(value = "活动类型")
public class SeckillClassificationModel {

    /**
     * 主键
     */
    @ApiModelProperty(value = "id主键 如果=0 则是新增 如果>0 则是修改 ",required = true,dataType ="int32",example="1")
    private Integer id;
    /**
     * 类目名称
     */
    @NotBlank(message = "类目名称不能为空")
    @ApiModelProperty(value = "类目名称 ",required = true,dataType ="string",example="to be number one ")
    private String name;

    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级  正序排序",required = true,dataType ="int32",example="1")
    private Integer priority;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id",dataType ="int64",example="2010")
    private Long tenantId;

    @ApiModelProperty(value = "秒杀活动引用数目", dataType = "int32", example = "1")
    private Integer classificaReferNum;
    /**
     * 创建人
     */

    private String createUser;
    /**
     * 修改人
     */

    private String updateUser;
    /**
     * 创建时间
     */

    private Date createTime;
    /**
     * 修改时间
     */

    private Date updateTime;
    /**
     * 删除标识 0未删除 1删除
     */

    private Boolean isDelete;

}
