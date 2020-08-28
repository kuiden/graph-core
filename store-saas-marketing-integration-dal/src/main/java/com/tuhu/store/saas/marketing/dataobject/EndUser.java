package com.tuhu.store.saas.marketing.dataobject;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 车主信息
 * </p>
 *
 * @author someone
 * @since 2020-08-03
 */
@TableName("end_user")
public class EndUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    /**
     * 微信小程序openId
     */
    @TableField("open_id")
    private String openId;
    /**
     * 客户类型:person(个人);company(公司);government(政府单位)other(其他);
     */
    @TableField("customer_type")
    private String customerType;
    /**
     * 客户名称
     */
    private String name;
    /**
     * 手机号码
     */
    @TableField("phone_number")
    private String phoneNumber;
    /**
     * 密码
     */
    private String password;
    /**
     * 微信头像图片链接
     */
    @TableField("head_image")
    private String headImage;
    /**
     * 微信昵称
     */
    @TableField("nick_name")
    private String nickName;
    /**
     * 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     */
    private String gender;
    /**
     * 用户所在的省份
     */
    private String province;
    /**
     * 用户所在城市
     */
    private String city;
    /**
     * 用户所在国家
     */
    private String country;
    /**
     * 用户的语言，简体中文为zh_CN
     */
    private String language;
    /**
     * 客户来源:ZRJD(自然进店);WLYL(网络引流);WBDL(外部导入);QT(其他)
     */
    @TableField("customer_source")
    private String customerSource;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否删除:0(未删除);1(已删除)
     */
    @TableField("is_delete")
    private Integer isDelete;
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
     * 创建日期
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 更新日期
     */
    @TableField("update_time")
    private Date updateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCustomerSource() {
        return customerSource;
    }

    public void setCustomerSource(String customerSource) {
        this.customerSource = customerSource;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "EndUserDAO{" +
        "id=" + id +
        ", openId=" + openId +
        ", customerType=" + customerType +
        ", name=" + name +
        ", phoneNumber=" + phoneNumber +
        ", password=" + password +
        ", headImage=" + headImage +
        ", nickName=" + nickName +
        ", gender=" + gender +
        ", province=" + province +
        ", city=" + city +
        ", country=" + country +
        ", language=" + language +
        ", customerSource=" + customerSource +
        ", remark=" + remark +
        ", isDelete=" + isDelete +
        ", createUser=" + createUser +
        ", updateUser=" + updateUser +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
