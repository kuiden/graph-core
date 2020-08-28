package com.tuhu.store.saas.marketing.remote.reponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: WangKun
 * @Description:
 * @Date: Created in 2018/12/28 11:51
 * @ProjectName: crm
 * @Version: 1.0.0
 */
@Data
public class CustomerDTO implements Serializable {

	private static final long serialVersionUID = -4740717893037845140L;

	private String id;

	private String name;

	private String customerSource;

	private String customerType;

	private String phoneNumber;

	private String gender;

	private Long storeId;

	private Long tenantId;

	private Long provinceId;

	private String provinceName;

	private Long cityId;

	private String cityName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private Date birthday;

//	private Date driverLicenseExpiryDate;

	private String driverLicensePhoto;

	private Long countyDistrictId;

	private String countyDistrictName;

	private String address;

	private String remark;

	private String createUser;

	private String updateUser;

	private String driverLicenseNumber;

	/**
	 * 是否是大客户
	 */
	private Boolean isVip;

	/**
	 *  剩余额度
	 */
	private Long balance;
	/**
	 * 是否商城用户，0：非商城用户 ；1：商城用户
	 */
	private Byte mallUser;

	/**
	 * 商城用户id
	 */
	private String mallUserId;
}
