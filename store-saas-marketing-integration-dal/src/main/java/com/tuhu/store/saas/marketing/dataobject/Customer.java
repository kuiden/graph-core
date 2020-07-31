package com.tuhu.store.saas.marketing.dataobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * customer
 * @author
 */
@Data
public class Customer implements Serializable {

    public Customer(String id) {
        this.id = id;
    }

    public Customer() {
    }

    private String id;

    /**
     * 客户类型:person(个人);company(公司);government(政府单位)other(其他);
     */
    private String customerType;

    /**
     * 客户名称
     */
    private String name;

    /**
     * 性别:0(女);1(男)
     */
    private String gender;

    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date birthday;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 驾驶证有效期
     */
    private Date driverLicenseExpiryDate;

    /**
     * 驾驶证照片
     */
    private String driverLicensePhoto;

    /**
     * 客户来源:ZRJD(自然进店);WLYL(网络引流);WBDL(外部导入);QT(其他)
     */
    private String customerSource;

    /**
     * 门店id
     */
    private Long storeId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 省
     */
    private Long provinceId;

    private String provinceName;

    /**
     * 市
     */
    private Long cityId;

    private String cityName;

    /**
     * 区
     */
    private Long countyDistrictId;

    private String countyDistrictName;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除:N(未删除);D(已删除)
     */
    private Boolean isDelete;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 驾照号码
     */
    private String driverLicenseNumber;

    /**
     * 驾照类型
     */
    private String driverLicenseType;

    /**
     * 是否是大客户，‘0’不是，‘1’是，默认不是
     */
    private Boolean isVip;

    /**
     * 商城用户id
     */
    private String mallUserId;

    /**
     * 是否商城用户，0：非商城用户 ；1：商城用户
     */
    private Byte mallUser;

    /**
     * 客户编码
     */
    private String customerNo;

    private static final long serialVersionUID = 1L;
}