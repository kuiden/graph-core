package com.tuhu.store.saas.marketing.remote.reponse;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jiangyuhang
 * @date 2018/11/1615:55
 */
@Data
public class StoreInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店门头图片
     */
    private String imagePath;

    /**
     * 省份ID
     */
    private String provinceId;

    /**
     * 城市ID
     */
    private String cityId;

    /**
     * 区ID
     */
    private String regionId;

    /**
     * 门店地址
     */
    private String address;

    /**
     * 负责人
     */
    private String principalName;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 固定电话
     */
    private String lineTelephone;

    /**
     * 手机
     */
    private String mobilePhone;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 网址
     */
    private String website;

    /**
     * 传真
     */
    private String fax;

    /**
     * qq账号
     */
    private String qqAccount;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 邮编
     */
    private String post;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 营业时间起
     */
    private Date openingEffectiveDate;

    /**
     * 营业时间止
     */
    private Date openingExpiryDate;

    /**
     * 备注
     */
    private String memo;

    /**
     *门店照片，最多5张，多个逗号分隔
     */
    private  String imagePaths;

    /**
     *   c端预约电话
     */
    private String clientAppointPhone;

    /**
     *c端展示标签id,多个逗号分隔
     */
    private String clientTag;

    /**
     *c端展示常规服务项目spu_code，多个以逗号分隔
     */
    private String clientCommonService;

    /**
     * 门店地址经度
     */
    private Double lon;

    /**
     * 门店地址维度
     */
    private Double lat;

    /**
     * 微信小程序二维码图片链接
     */
    private String weixinQrUrl;
}
