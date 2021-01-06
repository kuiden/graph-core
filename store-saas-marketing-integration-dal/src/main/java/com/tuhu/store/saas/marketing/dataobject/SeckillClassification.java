package com.tuhu.store.saas.marketing.dataobject;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.Version;

import com.tuhu.store.saas.marketing.request.seckill.SeckillClassificationModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 秒杀活动分类表
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("seckill_classification")
public class SeckillClassification implements Serializable {

    private static final long serialVersionUID = 1L;

    public SeckillClassification() {
    }

    public SeckillClassification(SeckillClassificationModel model ,boolean isInsert) {
        this.id = model.getId();
        this.name = model.getName();
        this.priority = model.getPriority();
        this.tenantId = model.getTenantId();
        this.isDelete = 0;
        if (isInsert){
            this.createTime = new Date(System.currentTimeMillis());
            this.createUser = model.getCreateUser();
        }
        this.updateUser = model.getCreateUser();
        this.updateTime = new Date(System.currentTimeMillis());

    }

    public SeckillClassificationModel toModel() {
        SeckillClassificationModel result = new SeckillClassificationModel();
        result.setTenantId(this.tenantId);
        result.setId(this.id);
        result.setName(this.name);
        result.setPriority(this.priority);
        return result;
    }

    /**
     * 主键
     */
    private Integer id;
    /**
     * 类目名称
     */
    @TableField("name")
    private String name;

    /**
     * 优先级
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 租户id
     */
    @TableField("tenant_id")
    private Long tenantId;
    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;
    /**
     * 修改人
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 删除标识 0未删除 1删除
     */
    @TableField("is_delete")
    private Integer isDelete;


    public static final String ID = "id";

    public static final String TENANT_ID = "tenant_id";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_USER = "update_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String IS_DELETE = "is_delete";

    public static final String NAME = "name";

    public static final String PRIORITY = "priority";


}
