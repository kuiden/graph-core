package com.tuhu.store.saas.marketing.response.card;

import lombok.Data;

/**
 * @author wangyuqing
 * @since 2020/8/7 16:31
 */
@Data
public class CardItemResp {

    /*
     * 服务项目id
     */
    private Long id;

    /**
     * 服务项名称
     */
    private String serviceItemName;

    /**
     * 次数
     */
    private Integer measuredQuantity;

    /**
     * 已使用次数
     */
    private Integer usedQuantity;

    /*
     * 剩余次数
     */
    private Integer remainQuantity;

    /**
     * 商品类型1：服务 2：商品
     */
    private Byte type;

}
