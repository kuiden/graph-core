package com.tuhu.store.saas.marketing.request.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wangyuqing
 * @since 2020/8/8 9:40
 */
@Data
public class UpdateCardVo implements Serializable {

    private static final long serialVersionUID = 8649700041264235508L;
    private Long storeId;

    private Long tenantId;

    private Long cardId;

    /*
     * goodsId - quantity 次数
     */
    private Map<String, Integer> itemQuantity;

}
