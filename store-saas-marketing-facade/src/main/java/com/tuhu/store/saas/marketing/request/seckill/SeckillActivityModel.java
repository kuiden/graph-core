package com.tuhu.store.saas.marketing.request.seckill;

import com.tuhu.store.saas.marketing.request.card.CardTemplateItemModel;
import com.tuhu.store.saas.marketing.request.card.CardTemplateModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApiModel(value = "秒杀活动模型")
@Data
public class SeckillActivityModel implements Serializable {

    /**
     * 检查模型是否完整
     *
     * @return
     */
    public String checkModel(SeckillActivityModel entity, boolean isInsert) {
        String result = "";
        //检查必要字段是否为空
        if (StringUtils.isEmpty(this.activityTitle)) {
            result = "活动标题为空";
            return result;
        }
        if (this.startTime == null || this.endTime == null) {
            result = " 开始或者结束时间不能为空";
            return result;
        }
        if (this.startTime.getTime() < this.endTime.getTime()) {
            result = "开始时间不能小于结束时间";
            return result;
        }
        if (this.items == null || this.items.size() == 0) {
            result = "请完善商品/服务信息";
            return result;
        }
        if (this.originalPrice == null) {
            result = "原价不能为空";
            return result;
        }
        if (this.newPrice == null) {
            result = "现价不能为空";
            return result;
        }
        if (StringUtils.isEmpty(this.rulesInfo)) {
            result = "活动规则不能为空";
            return result;
        }

        if (this.cadCardExpiryDateType == 2 && (this.cadCardExpiryDateTime == null
                || this.cadCardExpiryDateTime.getTime() < System.currentTimeMillis())) {
            result = "请输入有效的截至日期";
            return result;
        }

        if (this.sellNumberType == 1 && (this.sellNumber == null || this.sellNumber <= 0)) {
            result = "限购数量输入错误";
            return result;
        }
        //在修改时需要检查当前是否能修改
        if (!isInsert) {
            if (entity == null) {
                result = "数据查询失败";
                return result;
            }
            if (this.storeId != entity.getStoreId() || this.tenantId != entity.getTenantId()) {
                result = "数据越权";
                return result;
            }
            if (entity.getStartTime().getTime() > System.currentTimeMillis()) {
                //开始时间大于当前时间不能修改 表示活动已经开始或者已经结束
                result = "当前活动已经开始或者结束 不能修改";
                return result;
            }
            if (entity.getStatus() != Integer.valueOf(0)) {
                result = "当前活动不是处于未上架状态 无法修改";
                return result;
            }
            if (entity.getIsDelete() == Integer.valueOf(1)) {
                result = "当前活动处于已经删除状态 不允许修改";
                return result;
            }
        }
        return result;
    }

    /**
     * 转换卡模板model
     *
     * @return
     */
    public CardTemplateModel toCardTemplateModel() {
        CardTemplateModel result = new CardTemplateModel();
        if (!StringUtils.isEmpty(this.getTemplateId())) {
            result.setId(Long.valueOf(this.getTemplateId()));
        }
        result.setStatus("ENABLE");
        result.setCardName(this.activityTitle);
        result.setStoreId(this.storeId);
        result.setTenantId(this.tenantId);
        result.setDescription("秒杀活动创建的卡模板");
        byte b1 = 2;
        result.setType(b1);
        byte b = 0;
        result.setIsShow(b);
        switch (this.cadCardExpiryDateType) {
            case 1:
                result.setForever(Boolean.TRUE);
                result.setExpiryDate(null);
                result.setExpiryPeriod(Integer.valueOf(0));
                result.setCardTypeCode("COUNTING_CARD");
                break;
            case 2:
                result.setForever(Boolean.FALSE);
                result.setExpiryDate(this.cadCardExpiryDateTime);
                result.setExpiryPeriod(Integer.valueOf(0));
                result.setCardTypeCode("COUNTING_CARD");
                break;
            case 3:
                result.setForever(Boolean.FALSE);
                result.setExpiryDate(null);
                result.setExpiryPeriod(Integer.valueOf(0));
                result.setCardTypeCode("TIMIN_CARD");
                break;
            default:
                break;
        }
        List<CardTemplateItemModel> cardItems = new ArrayList<>(this.items.size());
        for (SeckillActivityItemModel item : this.items) {
            CardTemplateItemModel cardItem = new CardTemplateItemModel();
            cardItem.setServiceItemCode(item.getGoodsCode());
            cardItem.setGoodsId(item.getGoodsId());
            cardItem.setServiceItemName(item.getGoodsName());
            cardItem.setType(item.getGoodsType().getType());
            cardItem.setPrice(item.getOriginalPrice());
            cardItem.setFaceAmount(item.getNewPrice());
            cardItem.setMeasuredQuantity(item.getItemQuantity());
            cardItems.add(cardItem);
        }
        result.setCardTemplateItemModelList(cardItems);
        return result;
    }

    /**
     * 初始化阶段
     */
    public SeckillActivityModel init() {
        if (this.cadCardExpiryDateType == null || this.cadCardExpiryDateType < 0) {
            this.cadCardExpiryDateType = 3;
        }
        if (this.cadCardExpiryDateType == 3 && (this.cadCardExpiryDateDay == null || this.cadCardExpiryDateDay <= 0)) {
            this.cadCardExpiryDateDay = 365;
        }
        if (this.sellNumberType == null || this.sellNumberType <= 0) {
            this.sellNumberType = 2;
        }
        if (this.soloSellNumberType == null || this.soloSellNumberType <= 0) {
            this.soloSellNumberType = 1;
        }
        if (this.soloSellNumberType == 1 && (this.soloSellNumber == null || this.soloSellNumber <= 0)) {
            this.soloSellNumber = 1;
        }
        BigDecimal totalOriginalPrice = BigDecimal.ZERO;
        for (SeckillActivityItemModel item : this.items) {
            totalOriginalPrice.add(item.getOriginalPrice().multiply(new BigDecimal(item.getItemQuantity())));
        }
        this.originalPrice  = totalOriginalPrice;
        return this;
    }

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键ID 新增时不传", dataType = "String", example = "1123")
    private String id;
    /**
     * 活动头图
     */
    @ApiModelProperty(value = "活动头图URL", dataType = "String", example = "http://www.baidu.com/images1/sss.img")
    private String headImage;
    /**
     * 有效期类型
     */
    @ApiModelProperty(value = "有效期类型 1永久有效 2截至日期，3有效天数", dataType = "int32", example = "3")
    private Integer cadCardExpiryDateType;
    /**
     * 相关次卡有效期时间
     */
    @ApiModelProperty(value = "次卡模板有效日期 当有效期类型为截至日期时有效 ", dataType = "DateTime", example = "2020-12-03 14:53:00")
    private Date cadCardExpiryDateTime;
    /**
     * 相关次卡有效天数
     */
    @ApiModelProperty(value = "次卡模板有效天数 当有效期类型为有效天数时有效 ", dataType = "int32", example = "1")
    private Integer cadCardExpiryDateDay;
    /**
     * 销售数量类型
     */
    @ApiModelProperty(value = "销售数量类型 1限制数量 2不限制数量", dataType = "int32", example = "2")
    private Integer sellNumberType;
    /**
     * 销售数量
     */
    @ApiModelProperty(value = "销售数量限制 只有当销售数量为 限制数量时有效 ", dataType = "int32", example = "-1")
    private Integer sellNumber;
    /**
     * 单人销售数量类型
     */
    @ApiModelProperty(value = "单人销售数量类型 1限制数量 2不限制数量 ", dataType = "int32", example = "2")
    private Integer soloSellNumberType;
    /**
     * 单人销售数量 -1不限
     */
    @ApiModelProperty(value = "单人销售数量 只有当单人销售数量类型为 限制数量时有效 ", dataType = "int32", example = "-1")
    private Integer soloSellNumber;
    /**
     * 原价
     */
    @ApiModelProperty(value = "原价", dataType = "BigDecimal", example = "222.222")
    private BigDecimal originalPrice;
    /**
     * 现价
     */
    @ApiModelProperty(value = "现价", dataType = "BigDecimal", example = "222.222")
    private BigDecimal newPrice;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 门店id
     */
    private Long storeId;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 修改人
     */
    private String updateUser;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 删除标识 0未删除 1删除
     */
    @ApiModelProperty(value = "删除标识", dataType = "Integer", example = "0")
    private Integer isDelete;
    /**
     * 上下架状态 0:未上架 1:上架 9:下架
     */
    @ApiModelProperty(value = "上下架状态", dataType = "int32", example = "0:未上架 1:上架 9:下架")
    private Integer status;
    /**
     * 活动开始时间
     */
    @ApiModelProperty(value = "活动开始时间", dataType = "DateTime", example = "2020-12-03 14:53:00", required = true)
    private Date startTime;
    /**
     * 活动结束时间
     */
    @ApiModelProperty(value = "活动结束时间", dataType = "DateTime", example = "2020-12-04 14:53:00", required = true)
    private Date endTime;
    /**
     * 活动标题
     */
    @ApiModelProperty(value = "活动标题", dataType = "String", example = "叶子大帝666", required = true)
    private String activityTitle;

    /**
     * 活动模板ID
     */
    @ApiModelProperty(value = "活动模板ID", dataType = "String", example = "叶子大帝666")
    private String templateId;

    /**
     * 卡模板ID
     */
    @ApiModelProperty(value = "卡模板ID", dataType = "String", example = "叶子大帝666")
    private String cadCardTemplateId;

    /**
     * 活动商品详情
     */
    @ApiModelProperty(value = "活动商品/服务详情", dataType = "[]")
    private List<SeckillActivityItemModel> items;

    @ApiModelProperty(value = "规则信息", dataType = "String", example = "规则信息")
    private String rulesInfo;

    @ApiModelProperty(value = "门店描述", dataType = "String", example = "门店描述")
    private String storeInfo;

    /**
     * 模板信息
     */
    private Object templateInfo;

    /**
     * 次卡信息
     */
    private Object cadCardTemplateInfo;


}
