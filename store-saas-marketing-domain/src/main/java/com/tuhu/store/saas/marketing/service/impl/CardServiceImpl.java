package com.tuhu.store.saas.marketing.service.impl;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.enums.CardStatusEnum;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CardTemplateMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CrdCardItemMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CrdCardMapper;
import com.tuhu.store.saas.marketing.remote.order.ServiceOrderClient;
import com.tuhu.store.saas.marketing.remote.reponse.CardUseRecordDTO;
import com.tuhu.store.saas.marketing.request.card.CardTemplateItemModel;
import com.tuhu.store.saas.marketing.request.card.CardTemplateModel;
import com.tuhu.store.saas.marketing.request.card.CardTemplateReq;
import com.tuhu.store.saas.marketing.request.card.MiniQueryCardReq;
import com.tuhu.store.saas.marketing.request.vo.UpdateCardVo;
import com.tuhu.store.saas.marketing.response.card.CardItemResp;
import com.tuhu.store.saas.marketing.response.card.CardResp;
import com.tuhu.store.saas.marketing.response.card.CardUseRecordResp;
import com.tuhu.store.saas.marketing.service.ICardService;
import com.tuhu.store.saas.marketing.service.ICardTemplateItemService;
import com.tuhu.store.saas.marketing.util.DataTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class CardServiceImpl implements ICardService {

    @Autowired
    private CardTemplateMapper cardTemplateMapper;

    @Autowired
    private ICardTemplateItemService itemService;

    @Autowired
    private CrdCardItemMapper cardItemMapper;

    @Autowired
    private CrdCardMapper cardMapper;

    @Autowired
    private ServiceOrderClient serviceOrderClient;

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
        PageHelper.startPage(req.getPageNum()+1, req.getPageSize());
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

    @Override
    @Transactional
    public Boolean updateCardQuantity(UpdateCardVo updateCardVo) {
        Boolean ok = true;
        CrdCardItemExample example = new CrdCardItemExample();
        example.createCriteria().andStoreIdEqualTo(updateCardVo.getStoreId())
                .andTenantIdEqualTo(updateCardVo.getTenantId())
                .andCardIdEqualTo(updateCardVo.getCardId());
        List<CrdCardItem> cardItems = cardItemMapper.selectByExample(example);
        Map<String, Integer> itemQuantity = updateCardVo.getItemQuantity();
        Date date = new Date();
        for (CrdCardItem item : cardItems){
            if (itemQuantity.containsKey(item.getGoodsId())){
                //检查更新次数后是否会超过总次数 或 小于0
                Integer quantity = itemQuantity.get(item.getGoodsId()) + item.getUsedQuantity();
                if (quantity.compareTo(item.getMeasuredQuantity()) > 0 || quantity.compareTo(0) < 0){
                    throw new MarketingException("次卡更新失败");
                }
                item.setUsedQuantity(quantity);
                item.setUpdateTime(date);
                Integer result = cardItemMapper.updateByPrimaryKeySelective(item);
                if (result <= 0){
                    ok = false;
                }
            }
        }
        return ok;
    }

    @Override
    public List<CardResp> queryCardRespList(MiniQueryCardReq req) {
        CrdCardExample cardExample = new CrdCardExample();
        CrdCardExample.Criteria criteria = cardExample.createCriteria();
        criteria.andCustomerIdEqualTo(req.getCustomerId())
                .andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId());
        if (null != req.getCustomerPhoneNumber()){
            criteria.andCustomerPhoneNumberEqualTo(req.getCustomerPhoneNumber());
        }
        if (null != req.getCardStatus()){
            criteria.andStatusEqualTo(req.getCardStatus());
        }
        List<CrdCard> cardList = cardMapper.selectByExample(cardExample);

        List<CardResp> cardRespList = new ArrayList<>();
        for (CrdCard card : cardList){
            CardResp resp = new CardResp();
            BeanUtils.copyProperties(card,resp);
            resp.setCardStatus(CardStatusEnum.valueOf(card.getStatus()).getDescription());
            resp.setCardStatusCode(CardStatusEnum.valueOf(card.getStatus()).getEnumCode());
            resp.setForever(card.getForever() == 1 ? true : false);
            if (!resp.getForever()){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                resp.setExpiryDate(dateFormat.format(card.getExpiryDate()));
                dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                resp.setDate(dateFormat.format(card.getExpiryDate()));
                Date date = new Date();
                Date expiryDate = DataTimeUtil.getDateZeroTime(card.getExpiryDate());
                if (date.compareTo(expiryDate) > 0){
                    resp.setCardStatus(CardStatusEnum.EXPIRED.getDescription());
                    resp.setCardStatusCode(CardStatusEnum.EXPIRED.getEnumCode());
                }
            }

            Long remainQuantity = 0L;

            //查询次卡服务项目
            CrdCardItemExample example = new CrdCardItemExample();
            example.createCriteria().andCardIdEqualTo(card.getId())
                    .andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId());
            List<CrdCardItem> cardItems = cardItemMapper.selectByExample(example);
            List<CardItemResp> cardServiceItem = new ArrayList<>();
            List<CardItemResp> cardGoodsItem = new ArrayList<>();
            for (CrdCardItem item : cardItems){
                CardItemResp itemResp = new CardItemResp();
                BeanUtils.copyProperties(item,itemResp);
                itemResp.setRemainQuantity(itemResp.getMeasuredQuantity() - itemResp.getUsedQuantity());
                remainQuantity += itemResp.getRemainQuantity();
                if (item.getType().intValue() == 1){
                    cardServiceItem.add(itemResp);
                } else {
                    cardGoodsItem.add(itemResp);
                }
            }
            resp.setCardServiceItem(cardServiceItem);
            resp.setCardGoodsItem(cardGoodsItem);

            if (remainQuantity.compareTo(0L) <= 0){
                resp.setCardStatus(CardStatusEnum.FINISHED.getDescription());
                resp.setCardStatusCode(CardStatusEnum.FINISHED.getEnumCode());
            }
            if (null == req.getCustomerPhoneNumber() ||
                    resp.getCardStatusCode().equals(CardStatusEnum.ACTIVATED.getEnumCode())){
                cardRespList.add(resp);
            }
        }
        return cardRespList;
    }

    @Override
    public List<CardUseRecordResp> consumptionHistory(Long id) {
        List<CardUseRecordDTO> recordDTOList = serviceOrderClient.getCardUseRecord(id.toString()).getData();
        List<CardUseRecordResp> respList = new ArrayList<>();
        for (CardUseRecordDTO dto : recordDTOList){
            CardUseRecordResp resp = new CardUseRecordResp();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            resp.setTime(dateFormat.format(dto.getUseTime()));
            resp.setServiceOrderId(dto.getServiceOrderId());
            List<CardItemResp> item = new ArrayList<>();
            for (CardUseRecordDTO.ServiceOrderItem serviceOrderItem : dto.getServiceOrderItems()){
                CardItemResp itemResp = new CardItemResp();
                itemResp.setServiceItemName(serviceOrderItem.getItemName());
                itemResp.setUsedQuantity(serviceOrderItem.getQuantity());
                item.add(itemResp);
            }
            resp.setItem(item);
            respList.add(resp);
        }
        return respList;
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
