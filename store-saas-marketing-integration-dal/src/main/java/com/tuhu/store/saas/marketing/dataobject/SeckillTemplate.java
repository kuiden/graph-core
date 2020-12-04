package com.tuhu.store.saas.marketing.dataobject;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 秒杀活动基础模板表
 * </p>
 *
 * @author kudeng
 * @since 2020-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("seckill_template")
public class SeckillTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;
    /**
     * 租户id
     */
    @TableField("tenant_id")
    private Long tenantId;
    /**
     * 活动标题
     */
    @TableField("activity_title")
    private String activityTitle;
    /**
     * 活动头图url
     */
    @TableField("pic_url")
    private String picUrl;
    /**
     * 活动规则
     */
    @TableField("activity_introduce")
    private String activityIntroduce;
    /**
     * 活动分类id
     */
    @TableField("classification_id")
    private String classificationId;
    /**
     * 模板引用次数
     */
    @TableField("refence_number")
    private Integer refenceNumber;
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
     * 模板状态，0：禁用，1：启用
     */
    private Integer status;
    /**
     * 删除标识 0未删除 1删除
     */
    @TableField("is_delete")
    private Integer isDelete;
    /**
     * 手动排序字段
     */
    private Integer sort;


    public static final String ID = "id";

    public static final String TENANT_ID = "tenant_id";

    public static final String ACTIVITY_TITLE = "activity_title";

    public static final String PIC_URL = "pic_url";

    public static final String ACTIVITY_INTRODUCE = "activity_introduce";

    public static final String CLASSIFICATION_ID = "classification_id";

    public static final String REFENCE_NUMBER = "refence_number";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_USER = "update_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String STATUS = "status";

    public static final String IS_DELETE = "is_delete";

    public static final String SORT = "sort";

}
