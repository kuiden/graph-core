package com.tuhu.store.saas.marketing.remote.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Create by wangshuai2 on 2018/12/19 11:04
 */
@Data
public class VehicleReq implements Serializable {

    private static final long serialVersionUID = 3103057670957934106L;
    @ApiModelProperty(value = "客户id")
    private String customerId;

    @ApiModelProperty(value = "车辆id")
    private String id;
    @ApiModelProperty(value = "汽车图片")
    private String carPhoto;

    @ApiModelProperty(value = "汽车品牌类型")
    private String brandType;
    @ApiModelProperty(value = "汽车品牌")
    private String brand;
    @ApiModelProperty(value = "汽车名")
    private String carName;

    @ApiModelProperty(value = "汽车销售名")
    private String saleName;

    @ApiModelProperty(value = "车牌省份标识")
    @NotNull(message = "车牌省份标识不能为空")
    private String licensePlateFlag;

    @ApiModelProperty(value = "车牌")
    @Size(max = 7)
    @NotNull(message = "车牌号不能为空")
    private String licensePlateNumber;

    @ApiModelProperty(value = "车辆分类")
    private String vehicleType;

    @ApiModelProperty(value = "车型")
    private String vehicleModel;

    @ApiModelProperty(value = "行驶里程")
    private Integer drivenDistance;

    @ApiModelProperty(value = "VIN号")
    private String vin;

    @ApiModelProperty(value = "颜色")
    private String color;

    @ApiModelProperty(value = "车辆备注")
    private String remark;

    private String tid;

    private String productId;

    @ApiModelProperty(value = "发动机型号")
    private String engine;

    @ApiModelProperty(value = "年检日期")
    private Date lastMaintenanceDate;

    @ApiModelProperty(value = "下次保养日期")
    private Date nextMaintenanceDate;

    @ApiModelProperty(value = "保养里程")
    private Integer mileage;

    @ApiModelProperty(value = "保险公司")
    @Size(max = 50)
    private String insuranceCompany;

    @ApiModelProperty(value = "保险生效时间")
    private Date effectiveDate;

    @ApiModelProperty(value = "保险到期日")
    private Date expiryDate;

    @ApiModelProperty(value = "保险描述")
    private String description;

    @ApiModelProperty(value = "排量")
    private String paiLiang;

    @ApiModelProperty("年份")
    private String nian;

    @ApiModelProperty("平均价")
    private String avgPrice;

    private String tireSize;

    /**
     * 年检日期
     */
    private Date annualInspectionDate;

    /**
     * 商城车辆主键id
     */
    @ApiModelProperty("商城车辆主键id")
    private String mallVehicleId;
    /**
     * 是否已删除
     */
    @ApiModelProperty("是否有效")
    private Boolean isDelete=false;

    /**
     * 车辆编码
     */
    private String vehicleNo;


}
