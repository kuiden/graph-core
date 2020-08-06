package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.enums.CardOrderStatusEnum;
import com.tuhu.store.saas.marketing.enums.CardStatusEnum;
import com.tuhu.store.saas.marketing.enums.PaymentStatusEnum;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.*;
import com.tuhu.store.saas.marketing.remote.order.StoreReceivingClient;
import com.tuhu.store.saas.marketing.request.card.AddCardOrderReq;
import com.tuhu.store.saas.marketing.service.ICardOrderService;
import com.tuhu.store.saas.marketing.util.CardOrderRedisCache;
import com.tuhu.store.saas.order.vo.finance.receiving.AddReceivingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author wangyuqing
 * @since 2020/8/4 16:41
 */
@Service
@Slf4j
public class ICardOrderServiceImpl implements ICardOrderService {

    @Autowired
    private CrdCardMapper crdCardMapper;

    @Autowired
    private CrdCardOrderMapper crdCardOrderMapper;

    @Autowired
    private CrdCardItemMapper crdCardItemMapper;

    @Autowired
    private CardTemplateMapper cardTemplateMapper;

    @Autowired
    private CardTemplateItemMapper cardTemplateItemMapper;

    @Autowired
    private StoreReceivingClient storeReceivingClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CardOrderRedisCache cardOrderRedisCache;

    public static final String cardOrderRedisPrefix = "CARDORDER:KKD:NO:";

    @Override
    @Transactional
    public Long addCardOrder(AddCardOrderReq req) {
        log.info("开卡接口请求参数：{}", JSONObject.toJSON(req));

        //新增次卡
        CrdCard crdCard = new CrdCard();
        BeanUtils.copyProperties(req, crdCard);
        Long templateId = req.getCardTemplateId();
        CardTemplate cardTemplate = cardTemplateMapper.getCardTemplateById(templateId,req.getTenantId(),req.getStoreId());
        if (null == cardTemplate){
            throw new MarketingException("无此卡模板数据");
        }
        if ("DISABLE".equals(cardTemplate.getStatus())){
            throw new MarketingException("卡模板已停用");
        }
        crdCard.setCustomerGender(req.getCustomerGender() ? "1" : "0");
        crdCard.setDiscountAmount(cardTemplate.getDiscountAmount());
        crdCard.setCardCategoryCode(cardTemplate.getCardCategoryCode());
        crdCard.setCardTypeCode(cardTemplate.getCardTypeCode());
        crdCard.setStatus(CardStatusEnum.INACTIVATED.getEnumCode());
        crdCard.setDescription(cardTemplate.getDescription());
        crdCard.setCardName(cardTemplate.getCardName());
        crdCard.setActualAmount(cardTemplate.getActualAmount());
        crdCard.setFaceAmount(cardTemplate.getFaceAmount());
        crdCardMapper.insertSelective(crdCard);

        //新增次卡关联的商品和服务
        List<CardTemplateItem> cardTemplateItems = cardTemplateItemMapper.selectCardTemplateItemList(templateId);
        for (CardTemplateItem item : cardTemplateItems){
            CrdCardItem crdCardItem = new CrdCardItem();
            BeanUtils.copyProperties(item,crdCardItem);
            crdCardItem.setCardId(crdCard.getId());
            crdCardItem.setCardName(crdCard.getCardName());
            crdCardItem.setId(null);
            crdCardItemMapper.insertSelective(crdCardItem);
        }

        //新增开卡单
        CrdCardOrder crdCardOrder = new CrdCardOrder();
        BeanUtils.copyProperties(req,crdCardOrder);
        crdCardOrder.setCardId(crdCard.getId());
        crdCardOrder.setCardName(cardTemplate.getCardName());
        crdCardOrder.setAmount(cardTemplate.getFaceAmount());
        crdCardOrder.setActualAmount(cardTemplate.getActualAmount());
        crdCardOrder.setDiscountAmount(cardTemplate.getDiscountAmount());
        crdCardOrder.setStatus(CardOrderStatusEnum.WAIT_OPENED_CARD.getEnumCode());
        crdCardOrder.setPaymentStatus(PaymentStatusEnum.PAYMENT_NOT.getEnumCode());
        crdCardOrder.setCardStatus(CardStatusEnum.INACTIVATED.getEnumCode());
        //生成开卡单号
        String code = cardOrderRedisCache.getCode(cardOrderRedisPrefix,req.getStoreId());
        crdCardOrder.setOrderNo(getCardOrderNumber(code, req.getStoreNo()));
        crdCardOrderMapper.insertSelective(crdCardOrder);

        //新增待收记录
        AddReceivingVO addReceivingVO = new AddReceivingVO();
        addReceivingVO.setOrderId(crdCardOrder.getOrderNo());
        addReceivingVO.setOrderNo(crdCardOrder.getOrderNo());
        addReceivingVO.setOrderDate(crdCardOrder.getCreateTime());
        addReceivingVO.setBusinessCategoryCode("CARD_ORDER");
        addReceivingVO.setBusinessCategoryName("开卡单");
        addReceivingVO.setPayerId(req.getCustomerId());
        addReceivingVO.setPayerName(req.getCustomerName());
        addReceivingVO.setPayerPhoneNumber(req.getCustomerPhoneNumber());
        addReceivingVO.setAmount(crdCardOrder.getActualAmount().multiply(new BigDecimal(100)).longValue());
        addReceivingVO.setDiscountAmount(0L);
        addReceivingVO.setActualAmount(addReceivingVO.getAmount());
        addReceivingVO.setPayedAmount(0L);
        addReceivingVO.setStatus("INIT");
        addReceivingVO.setPaymentStatus("UNRECEIVABLE");
        addReceivingVO.setStoreId(req.getStoreId());
        addReceivingVO.setTenantId(req.getTenantId());
        addReceivingVO.setStoreNo(req.getStoreNo());
        addReceivingVO.setCreateTime(new Date());

        Boolean result = storeReceivingClient.addReceiving(addReceivingVO).getData();
        if (null == result || !result){
            throw new MarketingException("创建待收记录失败");
        }
        return crdCardOrder.getCardId();
    }

    private String getCardOrderNumber(String cardOrderCode, String storeNo) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmm");
        return "KXS" + storeNo + formatter.format(currentTime) + cardOrderCode;
    }

}
