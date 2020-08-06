package com.tuhu.store.saas.marketing.service.impl;

import com.tuhu.store.saas.marketing.dataobject.CardTemplateItem;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CardTemplateItemMapper;
import com.tuhu.store.saas.marketing.request.card.CardTemplateItemModel;
import com.tuhu.store.saas.marketing.service.ICardTemplateItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CardTemplateItemServiceImpl implements ICardTemplateItemService {
    @Autowired
    private CardTemplateItemMapper cardTemplateItemMapper;


    @Override
    public Boolean addCardTemplateItemList(Long cardTemplateId, List<CardTemplateItem> cardTemplateItemList) {
        log.info("CardTemplateItemServiceImpl->addCardTemplateItemList -> req {}{}", cardTemplateId, cardTemplateItemList);
        boolean result = false;
        if (CollectionUtils.isNotEmpty(cardTemplateItemList) && cardTemplateId != null && cardTemplateId > 0) {
            //批量插入时如果大于1000 就需要变成分批次的批量插入 效果最佳
            // if (cardTemplateItemList.size()<= 1001){
            for (CardTemplateItem x : cardTemplateItemList) {
                x.setCardTemplateId(cardTemplateId);
            }
            cardTemplateItemMapper.insertCardTemplateItemList(cardTemplateItemList);
            result = true;
            //}
        }
        return result;
    }

    @Override
    public Integer delByCardTemplateId(Long id) {
        log.info("CardTemplateItemServiceImpl-> delBycardTemplateId->{} ", id);
        return cardTemplateItemMapper.del(id);
    }

    @Override
    public List<CardTemplateItemModel> getCardTemplateItemListByCardTemplateId(Long id) {
        log.info("CardTemplateItemServiceImpl-> getCardTemplateItemListByCardTemplateId->{} ", id);
        List<CardTemplateItemModel> result = null;
        List<CardTemplateItem> cardTemplateItems = cardTemplateItemMapper.selectCardTemplateItemList(id);
        if (CollectionUtils.isNotEmpty(cardTemplateItems)) {
            result = new ArrayList<>();
            for (CardTemplateItem x : cardTemplateItems) {
                CardTemplateItemModel item = new CardTemplateItemModel();
                BeanUtils.copyProperties(x, item);
                result.add(item);
            }
        }
        return result;
    }
}
