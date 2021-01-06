package com.tuhu.store.saas.marketing.response;

import com.tuhu.store.saas.marketing.request.seckill.AttachedInfoTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "返回实体")
@Data
public class AttachedInfoResp implements Serializable {


    /**
     * 主键
     */
    @ApiModelProperty("ID")
    private String id;
    /**
     * 外键ID
     */
    @ApiModelProperty("外键ID")
    private String foreignKey;
    /**
     * 内容
     */
    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("标题")
    private String title;
    /**
     * 类型
     */
    @ApiModelProperty("内容")
    private AttachedInfoTypeEnum type;
    /**
     * 租户id
     */
    @ApiModelProperty("租户id")
    private Long tenantId;
    /**
     * 门店id
     */
    @ApiModelProperty("门店id")
    private Long storeId;
    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createUser;
    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String updateUser;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date updateTime;
}
