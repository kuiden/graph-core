package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.dataobject.CardTemplateItem;
import com.tuhu.store.saas.marketing.request.card.CardTemplateItemModel;

import java.util.List;

public interface ICardTemplateItemService {

    /**
     * 添加卡模板服务项目
     * @param cardTemplateId
     * @param cardTemplateItemList
     */
    Boolean addCardTemplateItemList(Long cardTemplateId, List<CardTemplateItem> cardTemplateItemList);

    Integer delByCardTemplateId (Long id);

    List<CardTemplateItemModel> getCardTemplateItemListByCardTemplateId(Long id);
}
