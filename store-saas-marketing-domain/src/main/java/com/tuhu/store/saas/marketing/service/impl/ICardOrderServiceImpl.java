package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.boot.common.utils.StringUtils;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdReqVO;
import com.tuhu.store.saas.dto.product.QueryGoodsListDTO;
import com.tuhu.store.saas.marketing.bo.SMSResult;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.enums.*;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.*;
import com.tuhu.store.saas.marketing.parameter.SMSParameter;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.remote.order.StoreReceivingClient;
import com.tuhu.store.saas.marketing.remote.product.StoreProductClient;
import com.tuhu.store.saas.marketing.request.CustomerLastPurchaseDTO;
import com.tuhu.store.saas.marketing.request.CustomerLastPurchaseRequest;
import com.tuhu.store.saas.marketing.request.QueryCardToCommissionReq;
import com.tuhu.store.saas.marketing.request.card.AddCardOrderReq;
import com.tuhu.store.saas.marketing.request.card.ListCardOrderReq;
import com.tuhu.store.saas.marketing.request.card.QueryCardOrderReq;
import com.tuhu.store.saas.marketing.response.ComputeMarktingCustomerForReportResp;
import com.tuhu.store.saas.marketing.response.card.CardItemResp;
import com.tuhu.store.saas.marketing.response.card.CardOrderDetailResp;
import com.tuhu.store.saas.marketing.response.card.CardOrderResp;
import com.tuhu.store.saas.marketing.response.dto.CrdCardOrderExtendDTO;
import com.tuhu.store.saas.marketing.service.ICardOrderService;
import com.tuhu.store.saas.marketing.service.IMessageTemplateLocalService;
import com.tuhu.store.saas.marketing.service.ISMSService;
import com.tuhu.store.saas.marketing.util.CardOrderRedisCache;
import com.tuhu.store.saas.marketing.util.DataTimeUtil;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.marketing.util.PhoneUtil;
import com.tuhu.store.saas.order.vo.finance.receiving.AddReceivingVO;
import com.tuhu.store.saas.request.product.GoodsForMarketReq;
import com.tuhu.store.saas.response.product.ServiceGoodsListForMarketResp;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import com.tuhu.store.saas.vo.product.QueryGoodsListVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private StoreProductClient productClient;

    @Autowired
    private CardOrderRedisCache cardOrderRedisCache;

    @Autowired
    private IMessageTemplateLocalService iMessageTemplateLocalService;
    @Autowired
    private StoreInfoClient storeInfoClient;
    @Autowired
    private ISMSService ismsService;

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
        if (null == customerDTO) {
            throw new StoreSaasMarketingException("未获取到客户信息");
        }
        req.setCustomerName(customerDTO.getName());
        req.setCustomerPhoneNumber(customerDTO.getPhoneNumber());
        if (req.getExpiryDate() != null) {
            req.setExpiryDate(DateUtils.getDateEndTime(req.getExpiryDate()));
        }
        //新增次卡
        CrdCard crdCard = new CrdCard();
        BeanUtils.copyProperties(req, crdCard);
        Long templateId = req.getCardTemplateId();
        CardTemplate cardTemplate = cardTemplateMapper.getCardTemplateById(templateId, req.getTenantId(), req.getStoreId());
        if (null == cardTemplate) {
            throw new StoreSaasMarketingException("无此卡模板数据");
        }
        if ("DISABLE".equals(cardTemplate.getStatus())) {
            throw new StoreSaasMarketingException("卡模板已停用");
        }
        if (null != customerDTO.getGender()) {
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
        for (CardTemplateItem item : cardTemplateItems) {
            CrdCardItem crdCardItem = new CrdCardItem();
            BeanUtils.copyProperties(item, crdCardItem);
            crdCardItem.setAmount(item.getFaceAmount());
            crdCardItem.setCardId(crdCard.getId());
            crdCardItem.setCardName(crdCard.getCardName());
            crdCardItem.setId(null);
            crdCardItemMapper.insertSelective(crdCardItem);
        }

        //新增开卡单
        CrdCardOrder crdCardOrder = new CrdCardOrder();
        BeanUtils.copyProperties(req, crdCardOrder);
        crdCardOrder.setCardId(crdCard.getId());
        crdCardOrder.setCardName(cardTemplate.getCardName());
        crdCardOrder.setAmount(cardTemplate.getFaceAmount());
        crdCardOrder.setActualAmount(cardTemplate.getActualAmount());
        crdCardOrder.setDiscountAmount(cardTemplate.getDiscountAmount());
        crdCardOrder.setStatus(CardOrderStatusEnum.OPENED_CARD.getEnumCode());
        crdCardOrder.setPaymentStatus(PaymentStatusEnum.PAYMENT_NOT.getEnumCode());
        crdCardOrder.setCardStatus(CardStatusEnum.INACTIVATED.getEnumCode());
        //生成开卡单号
        String code = cardOrderRedisCache.getCode(cardOrderRedisPrefix, req.getStoreId());
        if (null == req.getStoreNo()) {
            req.setStoreNo(req.getStoreId().toString());
        }
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
        if (null == result || !result) {
            throw new StoreSaasMarketingException("创建待收记录失败");
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
        List<CrdCardOrder> cardOrderList = new ArrayList<>();
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        if (org.apache.commons.lang3.StringUtils.equals(req.getPaymentStatus(), "ALL")) {
            cardOrderList = crdCardOrderMapper.selectCrdCardOrderByCustomerPhoneNumber(req.getStoreId(), req.getTenantId(), req.getCondition());
        } else {
            CrdCardOrderExample cardOrderExample = new CrdCardOrderExample();
            CrdCardOrderExample.Criteria nameCriteria = cardOrderExample.createCriteria();
            CrdCardOrderExample.Criteria phoneCriteria = cardOrderExample.createCriteria();
            //根据客户id查询
            if (null != req.getCustomerId()) {
                nameCriteria.andCustomerIdEqualTo(req.getCustomerId());
                phoneCriteria.andCustomerIdEqualTo(req.getCustomerId());
            }
            //客户姓名、手机号模糊查询
            if (null != req.getCondition()) {
                nameCriteria.andCustomerNameLike("%" + req.getCondition() + "%");
                phoneCriteria.andCustomerPhoneNumberLike("%" + req.getCondition() + "%");
            }
            //支付状态 未支付、已结清
            if (null != req.getPaymentStatus()) {
                nameCriteria.andPaymentStatusEqualTo(req.getPaymentStatus());
                phoneCriteria.andPaymentStatusEqualTo(req.getPaymentStatus());
            }
            nameCriteria.andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId());
            phoneCriteria.andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId());
            cardOrderExample.or(phoneCriteria);
            cardOrderExample.setOrderByClause("update_time desc");
            cardOrderList = crdCardOrderMapper.selectByExample(cardOrderExample);
        }
        PageInfo<CrdCardOrder> cardOrderPageInfo = new PageInfo<>(cardOrderList);

        List<CardOrderResp> cardOrderRespList = new ArrayList<>();

        for (CrdCardOrder item : cardOrderList) {
            CardOrderResp cardOrderResp = new CardOrderResp();
            BeanUtils.copyProperties(item, cardOrderResp);
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
            if (!cardOrderResp.getForever()) {
                cardOrderResp.setExpiryDate(crdCard.getExpiryDate());
                Date date = new Date();
                Date expiryDate = DataTimeUtil.getDateZeroTime(crdCard.getExpiryDate());
                if (date.compareTo(expiryDate) > 0) {
                    cardOrderResp.setCardStatus(CardStatusEnum.EXPIRED.getDescription());
                    cardOrderResp.setCardStatusCode(CardStatusEnum.EXPIRED.getEnumCode());
                }
            }
            //计算剩余次数
            Long remainQuantity = 0L;
            for (CrdCardItem cardItem : crdCardItems) {
                remainQuantity += (cardItem.getMeasuredQuantity() - cardItem.getUsedQuantity());
            }
            if (remainQuantity.compareTo(0L) <= 0) {
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

    private void sendCardPaySms(Long storeId, String phone, String cardName) {
        log.info("sendCardPaySms,storeId:{},phone:{},cardName:{}", storeId, phone, cardName);
        try {
            if (PhoneUtil.isPhoneLegal(phone)) {
                String smsTemplateId = iMessageTemplateLocalService.getSMSTemplateIdByCodeAndStoreId(SMSTypeEnum.SAAS_CARD_PAY.templateCode(), null);
                if (StringUtils.isNotBlank(smsTemplateId)) {
                    List<String> list = new ArrayList<>();
                    StoreInfoVO storeInfoVO = new StoreInfoVO();
                    storeInfoVO.setStoreId(storeId);
                    StoreDTO storeDTO = storeInfoClient.getStoreInfo(storeInfoVO).getData();
                    if (storeDTO != null && StringUtils.isNotBlank(storeDTO.getStoreName())) {
                        list.add(storeDTO.getStoreName());
                        list.add(cardName);
                        SMSParameter smsParameter = new SMSParameter();
                        smsParameter.setPhone(phone);
                        smsParameter.setTemplateId(smsTemplateId);
                        smsParameter.setDatas(list);
                        SMSResult smsResult = ismsService.sendCommonSms(smsParameter);
                        if (!smsResult.isSendResult()) {
                            log.error("sendCardPaySms fail,message:{}", smsResult.getStatusMsg());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("sendCardPaySms fail", e);
        }
    }

    @Override
    @Transactional
    public void updateCardPaymentStatus(String orderNo, Long storeId, Long tenantId, Long amount) {
        log.info("开卡单号：{}, storeId：{}, tenantId：{}, 金额：{}", orderNo, storeId, tenantId, amount);

        CrdCardOrderExample cardOrderExample = new CrdCardOrderExample();
        cardOrderExample.createCriteria().andOrderNoEqualTo(orderNo)
                .andStoreIdEqualTo(storeId).andTenantIdEqualTo(tenantId);
        List<CrdCardOrder> crdCardOrders = crdCardOrderMapper.selectByExample(cardOrderExample);
        Integer result = 0;
        if (null != crdCardOrders && !crdCardOrders.isEmpty()) {
            CrdCardOrder cardOrder = crdCardOrders.get(0);
            CrdCard card = crdCardMapper.selectByPrimaryKey(cardOrder.getCardId());
            if (null == card) {
                throw new StoreSaasMarketingException("获取不到卡信息，调用updateCardPaymentStatus失败");
            }
            card.setStatus(CardStatusEnum.ACTIVATED.getEnumCode());
            result += crdCardMapper.updateByPrimaryKeySelective(card);
            Date date = new Date();
            cardOrder.setPaymentStatus(PaymentStatusEnum.PAYMENT_OK.getEnumCode());
            cardOrder.setStatus(CardOrderStatusEnum.SETTLE_CARD.getEnumCode());
            cardOrder.setCardStatus(CardStatusEnum.ACTIVATED.getEnumCode());
            cardOrder.setPaymentTime(date);
            cardOrder.setPayedAmount(new BigDecimal(amount).divide(new BigDecimal(100)));
            cardOrder.setUpdateTime(date);
            result += crdCardOrderMapper.updateByPrimaryKeySelective(cardOrder);
            //次卡开卡并支付发送短信
            sendCardPaySms(storeId, cardOrder.getCustomerPhoneNumber(), card.getCardName());
        } else {
            throw new StoreSaasMarketingException("源开卡单不存在，调用updateCardPaymentStatus失败");
        }
        if (result != 2) {
            throw new StoreSaasMarketingException("更新卡状态失败");
        }
    }

    @Override
    public CardOrderDetailResp queryCardOrder(QueryCardOrderReq req) {
        log.info("卡详情请求参数：{}", JSONObject.toJSON(req));
        CrdCardOrder cardOrder = crdCardOrderMapper.selectByPrimaryKey(req.getCardOrderId());

        CardOrderDetailResp resp = new CardOrderDetailResp();
        BeanUtils.copyProperties(cardOrder, resp);

        CrdCard card = crdCardMapper.selectByPrimaryKey(cardOrder.getCardId());
        resp.setForever(card.getForever() == 1 ? true : false);
        resp.setCardTypeCode(card.getCardTypeCode());
        resp.setCardStatusCode(CardStatusEnum.valueOf(card.getStatus()).getEnumCode());
        resp.setCardStatus(CardStatusEnum.valueOf(card.getStatus()).getDescription());
        //如果卡不是永久有效，则判断卡是否过期
        if (!resp.getForever()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            resp.setExpiryDate(dateFormat.format(card.getExpiryDate()));
            Date date = new Date();
            Date expiryDate = DataTimeUtil.getDateZeroTime(card.getExpiryDate());
            if (date.compareTo(expiryDate) > 0) {
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
        for (CrdCardItem item : cardItems) {
            CardItemResp itemResp = new CardItemResp();
            BeanUtils.copyProperties(item, itemResp);
            itemResp.setRemainQuantity(itemResp.getMeasuredQuantity() - itemResp.getUsedQuantity());
            remainQuantity += itemResp.getRemainQuantity();
            if (item.getType().intValue() == 1) {
                cardServiceItem.add(itemResp);
            } else {
                cardGoodsItem.add(itemResp);
            }
        }

        //查询最新商品信息
        List<String> goodsIdList = new ArrayList<>();
        for (CardItemResp item : cardGoodsItem) {
            goodsIdList.add(item.getGoodsId());
        }
        if (!goodsIdList.isEmpty()) {
            QueryGoodsListVO queryGoodsListVO = new QueryGoodsListVO();
            queryGoodsListVO.setStoreId(req.getStoreId());
            queryGoodsListVO.setTenantId(req.getTenantId());
            queryGoodsListVO.setGoodsIdList(goodsIdList);
            queryGoodsListVO.setGoodsSource("");
            BizBaseResponse<List<QueryGoodsListDTO>> productResult = productClient.queryGoodsListV2(queryGoodsListVO);
            if (productResult != null && CollectionUtils.isNotEmpty(productResult.getData())) {
                Map<String, QueryGoodsListDTO> goodsListDTOMap = productResult.getData().stream().collect(Collectors.toMap(x -> x.getGoodsId(), v -> v));
                for (CardItemResp item : cardGoodsItem) {
                    if (goodsListDTOMap.containsKey(item.getGoodsId())) {
                        item.setServiceItemName(goodsListDTOMap.get(item.getGoodsId()).getGoodsName());
                    }
                }
            }
        }
        //查询最新服务信息
        List<String> serviceIdList = new ArrayList<>();
        for (CardItemResp item : cardServiceItem) {
            serviceIdList.add(item.getGoodsId());
        }
        if (!serviceIdList.isEmpty()) {
            GoodsForMarketReq goodsForMarketReq = new GoodsForMarketReq();
            goodsForMarketReq.setGoodsName("");
            goodsForMarketReq.setStoreId(req.getStoreId());
            goodsForMarketReq.setTenantId(req.getTenantId());
            goodsForMarketReq.setServiceIdList(serviceIdList);
            goodsForMarketReq.setPageSize(500);
            BizBaseResponse<PageInfo<ServiceGoodsListForMarketResp>> serviceGoodsPage = productClient.serviceGoodsForFeign(goodsForMarketReq);
            if (null != serviceGoodsPage.getData() && null != serviceGoodsPage.getData().getList()) {
                List<ServiceGoodsListForMarketResp> serviceGoodsList = serviceGoodsPage.getData().getList();
                Map<String, ServiceGoodsListForMarketResp> serviceGoodsListForMarketRespMap = serviceGoodsList.stream().collect(Collectors.toMap(x -> x.getId(), v -> v));
                for (CardItemResp item : cardServiceItem) {
                    if (serviceGoodsListForMarketRespMap.containsKey(item.getGoodsId())) {
                        item.setServiceItemName(serviceGoodsListForMarketRespMap.get(item.getGoodsId()).getServiceName());
                    }
                }
            }
        }
        resp.setCardServiceItem(cardServiceItem);
        resp.setCardGoodsItem(cardGoodsItem);
        if (remainQuantity.compareTo(0L) <= 0) {
            resp.setCardStatus(CardStatusEnum.FINISHED.getDescription());
            resp.setCardStatusCode(CardStatusEnum.FINISHED.getEnumCode());
        }
        return resp;
    }

    @Override
    public List<CustomerCardOrder> getCustomersForCusGroup(Long storeId, Date beginTime) {

        return crdCardOrderMapper.getCustomersForCusGroup(storeId, beginTime);

    }

    @Autowired
    private CustomerCouponMapper customerCouponMapper;

    @Override
    public Map<String, List<ComputeMarktingCustomerForReportResp>> ComputeMarktingCustomerForReport(Long storeId, Long tenantId) {
        log.info("ComputeMarktingCustomerForReport -> req -> {}", storeId, tenantId);
        Map<String, List<ComputeMarktingCustomerForReportResp>> result = new HashMap<>();
        List<ComputeMarktingCustomerForReportResp> coupons = customerCouponMapper.ComputeMarktingCustomerForReportByCoupon(storeId, tenantId);
        if (!CollectionUtils.isNotEmpty(coupons)) {
            coupons = new ArrayList<>();
        }
        List<ComputeMarktingCustomerForReportResp> activities = customerCouponMapper.ComputeMarktingCustomerForReportByActivity(storeId, tenantId);
        if (!CollectionUtils.isNotEmpty(activities)) {
            activities = new ArrayList<>();
        }
        List<ComputeMarktingCustomerForReportResp> cards = customerCouponMapper.ComputeMarktingCustomerForReportByCard(storeId, tenantId);
        if (!CollectionUtils.isNotEmpty(cards)) {
            cards = new ArrayList<>();
        }
        result.put("customerCoupon", coupons);
        result.put("activityCustomer", activities);
        result.put("crdCard", cards);
        return result;
    }

    @Override
    public Map<String, Date> customerLastPurchaseTime(CustomerLastPurchaseRequest request) {
        Map<String, Date> hashMap = new HashMap<>();
        List<String> customerIds = request.getCustomerIds();
        Long storeId = request.getStoreId();
        Long tenantId = request.getTenantId();
        List<CustomerLastPurchaseDTO> list = crdCardOrderMapper.queryCustomerLastPurchaseTime(tenantId, storeId, customerIds);
        Map<String, List<CustomerLastPurchaseDTO>> map = list.stream().collect(Collectors.groupingBy(CustomerLastPurchaseDTO::getCustomerId));
        for (String customerId : customerIds) {
            List<CustomerLastPurchaseDTO> purchaseDTOS = map.get(customerId);
            if (CollectionUtils.isNotEmpty(purchaseDTOS)) {
                Date purchaseTime = purchaseDTOS.get(0).getPurchaseTime();
                hashMap.put(customerId, purchaseTime);
            } else {
                hashMap.put(customerId, null);
            }
        }
        return hashMap;
    }

    @Override
    public List<CrdCardOrderExtendDTO> queryCardToCommission(QueryCardToCommissionReq request) {
        List<CrdCardOrderExtendDTO> crdCardOrderExtendDTOList = Lists.newArrayList();

        List<CrdCardOrder> crdCardOrderList = Lists.newArrayList();
        CrdCardOrderExample cardOrderExample = new CrdCardOrderExample();
        CrdCardOrderExample.Criteria criteria = cardOrderExample.createCriteria();
        criteria.andIsDeleteEqualTo(Byte.valueOf("0"));
        //开卡单状态  已结算
        criteria.andStatusEqualTo(CardOrderStatusEnum.SETTLE_CARD.getEnumCode());
        //收款状态  已结清
        criteria.andPaymentStatusEqualTo(PaymentStatusEnum.PAYMENT_OK.getEnumCode());

        if (Objects.nonNull(request.getStartTime())) {
            criteria.andCreateTimeGreaterThanOrEqualTo(request.getStartTime());
        }
        if (Objects.nonNull(request.getEndTime())) {
            criteria.andCreateTimeLessThanOrEqualTo(request.getEndTime());
        }
        cardOrderExample.setOrderByClause("update_time desc");
        crdCardOrderList = crdCardOrderMapper.selectByExample(cardOrderExample);
        if (CollectionUtils.isEmpty(crdCardOrderList)) {
            return crdCardOrderExtendDTOList;
        }

        List<Long> crdCardIds = crdCardOrderList.stream().map(crdCardOrder -> crdCardOrder.getCardId()).collect(Collectors.toList());
        CrdCardExample cardExample = new CrdCardExample();
        CrdCardExample.Criteria cardExampleCriteria = cardExample.createCriteria();
        cardExampleCriteria.andIdIn(crdCardIds);
        cardExample.setOrderByClause("update_time desc");
        List<CrdCard> crdCardList = crdCardMapper.selectByExample(cardExample);
        if (CollectionUtils.isEmpty(crdCardList)) {
            return crdCardOrderExtendDTOList;
        }
        List<Long> cardTemplateIds = crdCardList.stream().map(crdCard -> crdCard.getCardTemplateId()).collect(Collectors.toList());
        List<CardTemplate> cardTemplateList = cardTemplateMapper.selectCardTemplateByIds(cardTemplateIds);
        if (CollectionUtils.isEmpty(cardTemplateList)) {
            return crdCardOrderExtendDTOList;
        }
        Map<Long, CrdCard> crdCardMap = crdCardList.stream().collect(Collectors.toMap(CrdCard::getId, Function.identity()));
        Map<Long, CardTemplate> cardTemplateMap = cardTemplateList.stream().collect(Collectors.toMap(CardTemplate::getId, Function.identity()));

        crdCardOrderList.forEach(crdCardOrder -> {
            CrdCardOrderExtendDTO crdCardOrderExtendDTO = new CrdCardOrderExtendDTO();
            BeanUtils.copyProperties(crdCardOrder, crdCardOrderExtendDTO);
            if (Objects.nonNull(crdCardMap.get(crdCardOrder.getCardId()))) {
                CrdCard crdCardTemp = crdCardMap.get(crdCardOrder.getCardId());
                if (Objects.nonNull(cardTemplateMap.get(crdCardTemp.getCardTemplateId()))) {
                    crdCardOrderExtendDTO.setCardTemplateId(crdCardTemp.getCardTemplateId());
                }
            }
            crdCardOrderExtendDTOList.add(crdCardOrderExtendDTO);
        });
        return crdCardOrderExtendDTOList;
    }

    @Override
    @Transactional
    public void addCardOrderBySeckillActivity(AddCardOrderReq req) {
        log.info("addCardOrderBySeckillActivity,request：{}", JSONObject.toJSON(req));

        //新增次卡
        CrdCard crdCard = new CrdCard();
        BeanUtils.copyProperties(req, crdCard);
        Long templateId = req.getCardTemplateId();
        CardTemplate cardTemplate = cardTemplateMapper.getCardTemplateById(templateId, req.getTenantId(), req.getStoreId());
        if (Objects.isNull(cardTemplate)) {
            throw new StoreSaasMarketingException("无此卡模板数据");
        }
        if (CardTemplateStatusEnum.DISABLE.name().equals(cardTemplate.getStatus())) {
            throw new StoreSaasMarketingException("卡模板已停用");
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
        for (CardTemplateItem item : cardTemplateItems) {
            CrdCardItem crdCardItem = new CrdCardItem();
            BeanUtils.copyProperties(item, crdCardItem);
            crdCardItem.setAmount(item.getFaceAmount());
            crdCardItem.setCardId(crdCard.getId());
            crdCardItem.setCardName(crdCard.getCardName());
            crdCardItem.setId(null);
            crdCardItemMapper.insertSelective(crdCardItem);
        }

        //新增开卡单
        CrdCardOrder crdCardOrder = new CrdCardOrder();
        BeanUtils.copyProperties(req, crdCardOrder);
        crdCardOrder.setCardId(crdCard.getId());
        crdCardOrder.setCardName(cardTemplate.getCardName());
        crdCardOrder.setAmount(cardTemplate.getFaceAmount());
        crdCardOrder.setActualAmount(cardTemplate.getActualAmount());
        crdCardOrder.setDiscountAmount(cardTemplate.getDiscountAmount());
        crdCardOrder.setStatus(CardOrderStatusEnum.OPENED_CARD.getEnumCode());
        crdCardOrder.setPaymentStatus(PaymentStatusEnum.PAYMENT_NOT.getEnumCode());
        crdCardOrder.setCardStatus(CardStatusEnum.INACTIVATED.getEnumCode());
        //生成开卡单号
        String code = cardOrderRedisCache.getCode(cardOrderRedisPrefix, req.getStoreId());
        if (null == req.getStoreNo()) {
            req.setStoreNo(req.getStoreId().toString());
        }
        crdCardOrder.setOrderNo(getCardOrderNumber(code, req.getStoreNo()));
        crdCardOrderMapper.insertSelective(crdCardOrder);
    }
}
