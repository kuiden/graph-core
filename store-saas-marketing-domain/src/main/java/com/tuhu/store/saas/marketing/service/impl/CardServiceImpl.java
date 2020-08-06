package com.tuhu.store.saas.marketing.service.impl;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.dataobject.CardTemplate;
import com.tuhu.store.saas.marketing.dataobject.CardTemplateItem;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CardTemplateMapper;
import com.tuhu.store.saas.marketing.request.card.CardTemplateItemModel;
import com.tuhu.store.saas.marketing.request.card.CardTemplateModel;
import com.tuhu.store.saas.marketing.request.card.CardTemplateReq;
import com.tuhu.store.saas.marketing.service.ICardService;
import com.tuhu.store.saas.marketing.service.ICardTemplateItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class CardServiceImpl implements ICardService {

    @Autowired
    private CardTemplateMapper cardTemplateMapper;

    @Autowired
    private ICardTemplateItemService itemService;

    @Override
    public Long saveCardTemplate(CardTemplateModel req, String userId) {
        log.info("CardServiceImpl-> addCardTemplate req={}", req);
        if (cardTemplateMapper.checkCardTemplateName(req.getCardName().trim(), req.getId() == null ? 0 : req.getId(), req.getTenantId(), req.getStoreId()) > 0)
            throw new StoreSaasMarketingException("卡名称不能重复");
        boolean isUpdate = req.getId() != null && req.getId() > 0 ? true : false;
        CardTemplate cardTemplate = this.convertorToCardTemplate(req);
        if (isUpdate) {
            cardTemplate.setUpdateUser(userId);
            cardTemplate.setUpdateTime(new Date());
        } else {
            cardTemplate.setCreateUser(userId);
            cardTemplate.setCreateTime(new Date());
        }
        int count = isUpdate ? cardTemplateMapper.updateCardTemplate(cardTemplate) : cardTemplateMapper.insertCardTemplate(cardTemplate);
        if (count > 0) {
            if (isUpdate) itemService.delByCardTemplateId(cardTemplate.getId());
            if (!itemService.addCardTemplateItemList(cardTemplate.getId(), cardTemplate.getCardTemplateItemList())) {
                throw new StoreSaasMarketingException("商品或服务初始化失败");
            }
        }
        return cardTemplate.getId();
    }

    @Override
    public CardTemplateModel getCardTemplateById(Long id, Long tenantId, Long storeId) {
        log.info("getCardTemplateById-> req  id {}  tenantId{}  storeId {}", id, tenantId, storeId);
        CardTemplateModel result = new CardTemplateModel();
        CardTemplate cardTemplateEntity = cardTemplateMapper.getCardTemplateById(id, tenantId, storeId);
        if (cardTemplateEntity != null) {
            BeanUtils.copyProperties(cardTemplateEntity, result);
            result.setCardTemplateItemModelList(itemService.getCardTemplateItemListByCardTemplateId(id));
            if (!cardTemplateEntity.getForever()) {
                //计算卡的优先期 时
                Date now = new Date(System.currentTimeMillis());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, result.getExpiryPeriod());
                result.setExpiryDate(cal.getTime());
            }
        }

        return result;
    }


    @Override
    public PageInfo<CardTemplateModel> getCardTemplatePageInfo(CardTemplateReq req) {
        log.info("CardServiceImpl-> addCardTemplate req={}", req);
        PageInfo<CardTemplateModel> result = new PageInfo<>();
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<CardTemplate> cardTemplates = cardTemplateMapper.selectPage(req.getStatus(), req.getQuery(), req.getTenantId(), req.getStoreId());
        if (CollectionUtils.isNotEmpty(cardTemplates)) {
            PageInfo<CardTemplate> cardTemplatePageInfo = new PageInfo<>(cardTemplates);
            List<CardTemplateModel> resultArray = new ArrayList<>();
            for (CardTemplate x : cardTemplates) {
                CardTemplateModel model = new CardTemplateModel();
                BeanUtils.copyProperties(x, model);
                resultArray.add(model);
            }
            result.setList(resultArray);
            result.setTotal(cardTemplatePageInfo.getTotal());
        }
        return result;
    }

    private CardTemplate convertorToCardTemplate(CardTemplateModel cardTemplateModelReq) {
        CardTemplate cardTemplate = new CardTemplate();
        BeanUtils.copyProperties(cardTemplateModelReq, cardTemplate);
        cardTemplate.setUpdateUser(cardTemplateModelReq.getCreateUser());
        cardTemplate.setIsDelete(false);
        cardTemplateModelReq.setDiscountAmount(cardTemplateModelReq.getActualAmount().subtract(cardTemplateModelReq.getFaceAmount()));
        List<CardTemplateItem> cardTemplateList = new ArrayList<>();
        cardTemplate.setDiscountAmount(BigDecimal.ZERO);
        cardTemplate.setActualAmount(BigDecimal.ZERO);
        cardTemplate.setFaceAmount(BigDecimal.ZERO);
        if (CollectionUtils.isNotEmpty(cardTemplateModelReq.getCardTemplateItemModelList())) {
            for (CardTemplateItemModel cardTemplateItemModel : cardTemplateModelReq.getCardTemplateItemModelList()) {
                CardTemplateItem cardTemplateItem = convertorToCardTemplateItem(cardTemplateModelReq.getCreateUser(), cardTemplateModelReq.getStoreId(), cardTemplateModelReq.getTenantId(), cardTemplateItemModel);
                BigDecimal quantity = new BigDecimal(cardTemplateItem.getMeasuredQuantity() == null ? 0 : cardTemplateItem.getMeasuredQuantity());
                //计算单次项目总优惠
                cardTemplateItem.setDiscountAmount(cardTemplateItem.getPrice().subtract(cardTemplateItem.getFaceAmount()).multiply(quantity));
                //计算单次项目 总单价
                cardTemplateItem.setActualAmount(cardTemplateItem.getFaceAmount().multiply(quantity));
                //计算总项实额
                cardTemplate.setActualAmount(cardTemplate.getActualAmount().add(cardTemplateItem.getActualAmount()));
                //计算总项面值
                cardTemplate.setFaceAmount(cardTemplate.getFaceAmount().add(cardTemplateItem.getPrice().multiply(quantity)));
                //计算总优惠额度
                cardTemplate.setDiscountAmount(cardTemplate.getDiscountAmount().add(cardTemplateItem.getDiscountAmount()));
                cardTemplateList.add(cardTemplateItem);
            }
        }
        cardTemplate.setCardTemplateItemList(cardTemplateList);
        return cardTemplate;
    }

    private CardTemplateItem convertorToCardTemplateItem(String userId, Long storeId, Long tenantId, CardTemplateItemModel cardTemplateItemModel) {
        CardTemplateItem cardTemplateItem = new CardTemplateItem();
        BeanUtils.copyProperties(cardTemplateItemModel, cardTemplateItem);
        cardTemplateItem.setType(cardTemplateItemModel.getType());
        cardTemplateItem.setCreateTime(new Date());
        cardTemplateItem.setUpdateTime(new Date());
        cardTemplateItem.setStoreId(storeId);
        cardTemplateItem.setTenantId(tenantId);
        cardTemplateItem.setCreateUser(userId);
        cardTemplateItem.setUpdateUser(userId);
        cardTemplateItem.setIsDelete(false);
        return cardTemplateItem;
    }

}
