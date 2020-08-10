package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdReqVO;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.enums.CardOrderStatusEnum;
import com.tuhu.store.saas.marketing.enums.CardStatusEnum;
import com.tuhu.store.saas.marketing.enums.PaymentStatusEnum;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.*;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.remote.order.StoreReceivingClient;
import com.tuhu.store.saas.marketing.request.card.AddCardOrderReq;
import com.tuhu.store.saas.marketing.request.card.ListCardOrderReq;
import com.tuhu.store.saas.marketing.request.card.QueryCardOrderReq;
import com.tuhu.store.saas.marketing.response.card.CardItemResp;
import com.tuhu.store.saas.marketing.response.card.CardOrderDetailResp;
import com.tuhu.store.saas.marketing.response.card.CardOrderResp;
import com.tuhu.store.saas.marketing.service.ICardOrderService;
import com.tuhu.store.saas.marketing.service.ICardService;
import com.tuhu.store.saas.marketing.util.CardOrderRedisCache;
import com.tuhu.store.saas.marketing.util.DataTimeUtil;
import com.tuhu.store.saas.order.vo.finance.receiving.AddReceivingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private CustomerClient customerClient;

    @Autowired
    private ICardService iCardService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CardOrderRedisCache cardOrderRedisCache;

    public static final String cardOrderRedisPrefix = "CARDORDER:KKD:NO:";

    @Override
    @Transactional
    public String addCardOrder(AddCardOrderReq req) {
        log.info("开卡接口请求参数：{}", JSONObject.toJSON(req));

        //获取最新客户信息
        BaseIdReqVO baseIdReqVO = new BaseIdReqVO();
        baseIdReqVO.setId(req.getCustomerId());
        baseIdReqVO.setStoreId(req.getStoreId());
        baseIdReqVO.setTenantId(req.getTenantId());
        CustomerDTO customerDTO = customerClient.getCustomerById(baseIdReqVO).getData();
        if (null == customerDTO){
            throw new MarketingException("未获取到客户信息");
        }
        req.setCustomerName(customerDTO.getName());
        req.setCustomerPhoneNumber(customerDTO.getPhoneNumber());

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
        if (null != customerDTO.getGender()){
            crdCard.setCustomerGender(customerDTO.getGender());
        }
        crdCard.setForever((byte) (req.getForever() ? 1 : 0));
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
        crdCardOrder.setStatus(CardOrderStatusEnum.OPENED_CARD.getEnumCode());
        crdCardOrder.setPaymentStatus(PaymentStatusEnum.PAYMENT_NOT.getEnumCode());
        crdCardOrder.setCardStatus(CardStatusEnum.INACTIVATED.getEnumCode());
        //生成开卡单号
        String code = cardOrderRedisCache.getCode(cardOrderRedisPrefix,req.getStoreId());
        crdCardOrder.setOrderNo(getCardOrderNumber(code, req.getStoreNo()));
        crdCardOrderMapper.insertSelective(crdCardOrder);

        //新增待收记录
        AddReceivingVO addReceivingVO = new AddReceivingVO();
        addReceivingVO.setOrderId(crdCardOrder.getId().toString());
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
        return addReceivingVO.getOrderId();
    }

    private String getCardOrderNumber(String cardOrderCode, String storeNo) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmm");
        return "KXS" + storeNo + formatter.format(currentTime) + cardOrderCode;
    }

    @Override
    public PageInfo<CardOrderResp> getCardOrderList(ListCardOrderReq req) {
        log.info("开卡单列表查询请求参数：{}", JSONObject.toJSON(req));
        PageHelper.startPage(req.getPageNum() + 1, req.getPageSize());
        CrdCardOrderExample cardOrderExample = new CrdCardOrderExample();
        CrdCardOrderExample.Criteria nameCriteria = cardOrderExample.createCriteria();
        CrdCardOrderExample.Criteria phoneCriteria = cardOrderExample.createCriteria();
        //客户姓名、手机号模糊查询
        if (null != req.getCondition()){
            nameCriteria.andCustomerNameLike("%" + req.getCondition() + "%");
            phoneCriteria.andCustomerPhoneNumberLike("%" + req.getCondition() + "%");
        }
        //支付状态 未支付、已结清
        if (null != req.getPaymentStatus()){
            nameCriteria.andPaymentStatusEqualTo(req.getPaymentStatus());
            phoneCriteria.andPaymentStatusEqualTo(req.getPaymentStatus());
        }
        nameCriteria.andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId());
        phoneCriteria.andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId());
        cardOrderExample.or(phoneCriteria);
        cardOrderExample.setOrderByClause("update_time desc");

        List<CrdCardOrder> cardOrderList = crdCardOrderMapper.selectByExample(cardOrderExample);
        PageInfo<CrdCardOrder> cardOrderPageInfo = new PageInfo<>(cardOrderList);

        List<CardOrderResp> cardOrderRespList = new ArrayList<>();

        for (CrdCardOrder item : cardOrderList){
            CardOrderResp cardOrderResp = new CardOrderResp();
            BeanUtils.copyProperties(item,cardOrderResp);
            cardOrderResp.setCardStatus(CardStatusEnum.valueOf(item.getCardStatus()).getDescription());
            cardOrderResp.setCardStatusCode(CardStatusEnum.valueOf(item.getCardStatus()).getEnumCode());
            cardOrderResp.setPaymentStatus(PaymentStatusEnum.valueOf(item.getPaymentStatus()).getDescription());
            cardOrderResp.setPaymentStatusCode(PaymentStatusEnum.valueOf(item.getPaymentStatus()).getEnumCode());
            CrdCard crdCard = crdCardMapper.selectByPrimaryKey(item.getCardId());
            CrdCardItemExample cardItemExample = new CrdCardItemExample();
            cardItemExample.createCriteria().andCardIdEqualTo(item.getCardId())
                    .andStoreIdEqualTo(item.getStoreId()).andTenantIdEqualTo(item.getTenantId());
            List<CrdCardItem> crdCardItems = crdCardItemMapper.selectByExample(cardItemExample);
            cardOrderResp.setForever(crdCard.getForever() == 1 ? true : false);
            cardOrderResp.setCardTemplateId(crdCard.getCardTemplateId());
            cardOrderResp.setCardTypeCode(crdCard.getCardTypeCode());
            //如果卡不是永久有效，则判断卡是否过期
            if (!cardOrderResp.getForever()){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                cardOrderResp.setExpiryDate(dateFormat.format(crdCard.getExpiryDate()));
                Date date = new Date();
                Date expiryDate = DataTimeUtil.getDateZeroTime(crdCard.getExpiryDate());
                if (date.compareTo(expiryDate) > 0){
                    cardOrderResp.setCardStatus(CardStatusEnum.EXPIRED.getDescription());
                    cardOrderResp.setCardStatusCode(CardStatusEnum.EXPIRED.getEnumCode());
                }
            }
            //计算剩余次数
            Long remainQuantity = 0L;
            for (CrdCardItem cardItem : crdCardItems){
                remainQuantity += (cardItem.getMeasuredQuantity() - cardItem.getUsedQuantity());
            }
            if (remainQuantity.compareTo(0L) <= 0){
                cardOrderResp.setCardStatus(CardStatusEnum.FINISHED.getDescription());
                cardOrderResp.setCardStatusCode(CardStatusEnum.FINISHED.getEnumCode());
            }
            cardOrderResp.setRemainQuantity(remainQuantity);
            cardOrderRespList.add(cardOrderResp);
        }
        PageInfo<CardOrderResp> resp = new PageInfo<>(cardOrderRespList);
        resp.setTotal(cardOrderPageInfo.getTotal());

        return resp;
    }

    @Override
    @Transactional
    public void updateCardPaymentStatus(String orderNo, Long storeId, Long tenantId, Long amount) {
        log.info("开卡单号：{}, storeId：{}, tenantId：{}, 金额：{}",orderNo,storeId,tenantId,amount);

        CrdCardOrderExample cardOrderExample = new CrdCardOrderExample();
        cardOrderExample.createCriteria().andOrderNoEqualTo(orderNo)
                        .andStoreIdEqualTo(storeId).andTenantIdEqualTo(tenantId);
        List<CrdCardOrder> crdCardOrders = crdCardOrderMapper.selectByExample(cardOrderExample);
        Integer result = 0;
        if (null != crdCardOrders && !crdCardOrders.isEmpty()){
            CrdCardOrder cardOrder = crdCardOrders.get(0);
            Date date = new Date();
            cardOrder.setPaymentStatus(PaymentStatusEnum.PAYMENT_OK.getEnumCode());
            cardOrder.setStatus(CardOrderStatusEnum.SETTLE_CARD.getEnumCode());
            cardOrder.setCardStatus(CardStatusEnum.ACTIVATED.getEnumCode());
            cardOrder.setPaymentTime(date);
            cardOrder.setPayedAmount(new BigDecimal(amount).divide(new BigDecimal(100)));
            cardOrder.setUpdateTime(date);
            result += crdCardOrderMapper.updateByPrimaryKeySelective(cardOrder);
            CrdCard card = crdCardMapper.selectByPrimaryKey(cardOrder.getCardId());
            if (null != card){
                card.setStatus(CardStatusEnum.ACTIVATED.getEnumCode());
                result += crdCardMapper.updateByPrimaryKeySelective(card);
            }
        } else{
            throw new MarketingException("源开卡单不存在，调用updateCardPaymentStatus失败");
        }
        if (result != 2){
            throw new MarketingException("更新卡状态失败");
        }
    }

    @Override
    public CardOrderDetailResp queryCardOrder(QueryCardOrderReq req) {
        log.info("卡详情请求参数：{}",JSONObject.toJSON(req));
        CrdCardOrder cardOrder = crdCardOrderMapper.selectByPrimaryKey(req.getCardOrderId());

        CardOrderDetailResp resp = new CardOrderDetailResp();
        BeanUtils.copyProperties(cardOrder,resp);

        CrdCard card = crdCardMapper.selectByPrimaryKey(cardOrder.getCardId());
        resp.setForever(card.getForever() == 1 ? true : false);
        resp.setCardTypeCode(card.getCardTypeCode());
        //如果卡不是永久有效，则判断卡是否过期
        if (!resp.getForever()){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            resp.setExpiryDate(dateFormat.format(card.getExpiryDate()));
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
        List<CrdCardItem> cardItems = crdCardItemMapper.selectByExample(example);
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

        //查询使用记录 -- 调用order
//        resp.setUseRecord(iCardService.consumptionHistory(resp.getCardId()));

        return resp;
    }

    @Override
    public List<CustomerCardOrder> getCustomersForCusGroup( Long storeId, Date beginTime){

        return crdCardOrderMapper.getCustomersForCusGroup(storeId,beginTime);

    }

}
