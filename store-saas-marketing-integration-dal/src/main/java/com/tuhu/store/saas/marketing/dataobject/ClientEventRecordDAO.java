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
 * 门店事件记录表
 * </p>
 *
 * @author kudeng
 * @since 2020-08-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("client_event_record")
public class ClientEventRecordDAO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("event_type")
    private String eventType;
    @TableField("content_type")
    private String contentType;
    @TableField("content_value")
    private String contentValue;
    @TableField("open_id")
    private String openId;
    @TableField("customer_id")
    private String customerId;
    @TableField("store_id")
    private String storeId;
    @TableField("create_time")
    private Date createTime;
    @TableField("event_count")
    private Integer eventCount;
    @TableField("update_time")
    private Date updateTime;


    public static final String ID = "id";

    public static final String EVENT_TYPE = "event_type";

    public static final String CONTENT_TYPE = "content_type";

    public static final String CONTENT_VALUE = "content_value";

    public static final String OPEN_ID = "open_id";

    public static final String CUSTOMER_ID = "customer_id";

    public static final String STORE_ID = "store_id";

    public static final String CREATE_TIME = "create_time";

    public static final String EVENT_COUNT = "event_count";

    public static final String UPDATE_TIME = "update_time";

}
