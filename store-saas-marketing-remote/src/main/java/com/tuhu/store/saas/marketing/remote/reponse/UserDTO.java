package com.tuhu.store.saas.marketing.remote.reponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/22
 * Time: 15:48
 * Description:
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 866171108910722674L;
    /**
     * 主键
     */
    private String id;

    /**
     * 微信小程序openId
     */
    private String openId;

    /**
     * 名称
     */
    private String username;

    /**
     * 头像链接
     */
    private String headImage;

    /**
     * 密码
     */
    private String password;

    /**
     * 服务技师，默认(是)
     */
    private Boolean technician;

    /**
     * 人事状态 on 在职，off 离职
     */
    private String state;

    /**
     * 账号状态 默认未启用
     */
    private Boolean accountState;

    /**
     * 性别
     */
    private String gender;

    /**
     * 薪水
     */
    private Double salary;

    private Boolean active;

    /**
     * 年纪
     */
    private Integer age;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 身份证号码
     */
    private String idcardNumber;

    /**
     * 生日
     */
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthday;

    /**
     * 入职时间
     */
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date entryDay;

    /**
     * 地址信息
     */
    private String address;

    /**
     * store 门店用户 company 公司用户 tenant 租户用户
     */
    private String userType;

    private Long storeId;

    private Long companyId;

    private Long tenantId;

    /**
     * 密码是否手动设置
     */
    private Boolean resetFlag;

    /**
     * 备注
     */
    private String mark;

    /**
     * 组织机构账号ID
     */
    private Long sysUserId;

    /**
     * 是否删除
     */
    private Boolean isDelete;

    /**
     * 存id
     */
    private String updateUser;

    /**
     * 存id
     */
    private String createUser;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date updateTime;

    /***
     * 门店编号
     */
    private String storeNo;
}
