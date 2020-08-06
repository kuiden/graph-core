package com.tuhu.store.saas.marketing.remote.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Create by wangshuai2 on 2018/12/19 11:05
 */
@Data
public class CustomerReq implements Serializable {
    private static final long serialVersionUID = 2832011735562046610L;

    private String id;
    /**
     * 车主姓名
     */
    private String name;
    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 性别
     */
    private String gender;


    private Date birthday;
    /**
     * 客户类型
     */
    private String customerType;

    /**
     * 驾照类型(下拉选择;默认C1;A1、A2、A3、B1、B2、C1、C2、C3、C4)
     */
    private String driverLicenseType;

    /**
     * 驾照到期日
     */
    private Date driverLicenseExpiryDate;

    /**
     * 驾照照片
     */
    private String driverLicensePhoto;

    /**
     * 客户来源
     */
    @ApiModelProperty(value = "客户来源:ZRJD(自然进店);WLYL(网络引流);WBDL(外部导入);QT(其他)")
    private String customerSource;

    /**
     * 驾照号码
     */
    private String driverLicenseNumber;

    @ApiModelProperty(value = "省")
    private Long provinceId;

    @ApiModelProperty(value = "省名")
    private String provinceName;

    @ApiModelProperty(value = "市")
    private Long cityId;

    @ApiModelProperty(value = "市名")
    private String cityName;

    @ApiModelProperty(value = "区")
    private Long countyDistrictId;

    @ApiModelProperty(value = "区名")
    private String countyDistrictName;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "客户备注")
    private String customerRemark;

    private Date updateTime;

    @ApiModelProperty(value = "大客户，false(不是大客户)，true(是大客户)")
    private Boolean isVip;

    /**
     * 是否做版本校验
     */
    private Boolean judgeVersion = false;
    /**
     * 商城用户id
     */
    @ApiModelProperty(value = "商城用户id")
    private String mallUserId;

    /**
     * 客户编码
     */
    private String customerNo;


}
