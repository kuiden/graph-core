package com.tuhu.store.saas.marketing.remote.reponse;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author wangyuqing
 * @since 2020/8/10 11:19
 */
@Data
public class CardUseRecordDTO implements Serializable {

    /**
     * 卡Id
     */
    private String cardId;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 工单Id
     */
    private String serviceOrderId;

    /**
     * 使用时间
     */
    private Date useTime;

    /**
     * 租户Id
     */
    private Long  tenantId;

    /**
     * 门店Id
     */
    private Long storeId;

    /**
     * 客户Id
     */
    private String customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 服务项集合
     */
    private List<ServiceOrderItem> serviceOrderItems;

    @Data
    public static class ServiceOrderItem{

        /**
         * 服务项Id
         */
        private String itemId;

        /**
         *产品类型编码:服务(SERVICE);货物(GOODS)
         */
        private String typeCode;

        /**
         *服务id/商品id
         */
        private String goodsId;

        /**
         * 服务名称/商品名称
         */
        private String itemName;

        /**
         * 数量
         */
        private Integer quantity;

    }

}
