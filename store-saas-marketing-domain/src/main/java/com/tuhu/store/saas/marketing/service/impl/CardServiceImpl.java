package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.springcloud.common.util.RedisUtils;
import com.tuhu.store.saas.crm.dto.StoreInfoRelatedDTO;
import com.tuhu.store.saas.dto.product.QueryGoodsListDTO;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.enums.CardExpiryDateEnum;
import com.tuhu.store.saas.marketing.enums.CardStatusEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.*;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.remote.order.ServiceOrderClient;
import com.tuhu.store.saas.marketing.remote.product.StoreProductClient;
import com.tuhu.store.saas.marketing.remote.reponse.CardUseRecordDTO;
import com.tuhu.store.saas.marketing.remote.wms.StoreWmsClient;
import com.tuhu.store.saas.marketing.request.card.*;
import com.tuhu.store.saas.marketing.request.vo.UpdateCardVo;
import com.tuhu.store.saas.marketing.response.CustomerIdMarketInfo;
import com.tuhu.store.saas.marketing.response.card.CardItemResp;
import com.tuhu.store.saas.marketing.response.card.CardResp;
import com.tuhu.store.saas.marketing.response.card.CardUseRecordResp;
import com.tuhu.store.saas.marketing.response.card.QueryCardItemResp;
import com.tuhu.store.saas.marketing.response.dto.CustomerMarketCountDTO;
import com.tuhu.store.saas.marketing.service.ICardService;
import com.tuhu.store.saas.marketing.service.ICardTemplateItemService;
import com.tuhu.store.saas.marketing.util.DataTimeUtil;
import com.tuhu.store.saas.marketing.util.StoreRedisUtils;
import com.tuhu.store.saas.request.product.GoodsForMarketReq;
import com.tuhu.store.saas.response.product.ServiceGoodsListForMarketResp;
import com.tuhu.store.saas.vo.product.QueryGoodsListVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.scmc.arch.model.facade.rsp.BizRsp;
import org.scmc.store.stk.qty.dto.StkQtyDto;
import org.scmc.store.stk.qty.request.StkQtyRequest;
import org.scmc.wms.stkcenter.enums.DamagedEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Autowired
    private StoreWmsClient storeWmsClient;

    @Autowired
    private StoreProductClient productClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CustomerCouponMapper customerCouponMapper;

    @Autowired
    private ValueCardMapper valueCardMapper;

    @Autowired
    private CrdCardOrderMapper crdCardOrderMapper;

    @Autowired
    private CrdCardItemMapper crdCardItemMapper;

    @Override
    @Transactional
    public Long saveCardTemplate(CardTemplateModel req, String userId) {
        log.info("CardServiceImpl-> addCardTemplate req={}", req);
//        if (cardTemplateMapper.checkCardTemplateName(req.getCardName().trim(), req.getId() == null ? 0 : req.getId(), req.getTenantId(), req.getStoreId()) > 0)
//            throw new StoreSaasMarketingException("卡名称不能重复");
        boolean isUpdate = req.getId() != null && req.getId() > 0 ? true : false;
        //商品、服务越权校验
        List<String> goodIds = req.getCardTemplateItemModelList().stream()
                .map(x->x.getGoodsId()).distinct().collect(Collectors.toList());
        BizBaseResponse<List<String>> productResult = productClient.hasProduct(goodIds, req.getStoreId(), req.getTenantId());
        if (productResult== null || productResult.getCode()!=10000 || productResult.getData() == null || productResult.getData().size() !=goodIds.size()){
            throw  new StoreSaasMarketingException ("服务/商品已禁用");
        }
        CardTemplate cardTemplate = this.convertorToCardTemplate(req);
        if (isUpdate) {
            cardTemplate.setUpdateUser(userId);
            cardTemplate.setUpdateTime(new Date());
        } else {
            cardTemplate.setCreateUser(userId);
            cardTemplate.setCreateTime(new Date());
            cardTemplate.setUpdateTime(new Date());
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
        result = this.convertModel(cardTemplateEntity,Boolean.TRUE);
        return result;
    }

    /**
     * entity转换model
     * @param cardTemplateEntity
     * @param hashItem
     * @return
     */
    private CardTemplateModel convertModel(CardTemplate cardTemplateEntity, boolean hashItem) {
        CardTemplateModel result = new CardTemplateModel();
        if (cardTemplateEntity != null) {
            BeanUtils.copyProperties(cardTemplateEntity, result);
            if (hashItem) {
                result.setCardTemplateItemModelList(itemService.getCardTemplateItemListByCardTemplateId(cardTemplateEntity.getId()));
            }
            if (CardExpiryDateEnum.EXPIRE_MONTH.getCode() == cardTemplateEntity.getExpiryType() && result.getExpiryPeriod() != null) {
                LocalDateTime dateTime = LocalDateTime.now().plusMonths(result.getExpiryPeriod()).withHour(23).withMinute(59).withSecond(59);
                result.setExpiryDate(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
            } else if (CardExpiryDateEnum.EXPIRE_DAY.getCode() == cardTemplateEntity.getExpiryType() && result.getExpiryDay() != null) {
                LocalDateTime dateTime = LocalDateTime.now().plusDays(result.getExpiryDay()).withHour(23).withMinute(59).withSecond(59);
                result.setExpiryDate(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
            }
        }
        return result;
    }


    @Override
    public PageInfo<CardTemplateModel> getCardTemplatePageInfo(CardTemplateReq req) {
        log.info("CardServiceImpl-> addCardTemplate req={}", req);
        PageInfo<CardTemplateModel> result = new PageInfo<>();
        PageHelper.startPage(req.getPageNum() + 1, req.getPageSize());
        List<CardTemplate> cardTemplates = cardTemplateMapper.selectPage(req.getStatus(), req.getQuery(), req.getTenantId(), req.getStoreId(), req.getIsShow(), req.getType());
        PageInfo<CardTemplate> cardTemplatePageInfo = new PageInfo<>(cardTemplates);
        if (CollectionUtils.isNotEmpty(cardTemplates)) {
            List<CardTemplateModel> resultArray = new ArrayList<>();
                for (CardTemplate x : cardTemplates) {
                    //CardTemplateModel model = new CardTemplateModel();
                    CardTemplateModel model =this.convertModel(x,req.getHashItem());
                    BeanUtils.copyProperties(x, model);
                    resultArray.add(model);
                }
            result.setList(resultArray);
        }
        result.setTotal(cardTemplatePageInfo.getTotal());
        return result;
    }

    @Override
    @Transactional
    public Boolean updateCardQuantity(UpdateCardVo updateCardVo) {
        log.info("updateCardQuantity-> req -> {}", updateCardVo);
        CrdCard card = cardMapper.selectByPrimaryKey(updateCardVo.getCardId());
        if (null == card) {
            throw new StoreSaasMarketingException("次卡不存在");
        }
        if (!card.getStatus().equals(CardStatusEnum.ACTIVATED.getEnumCode())) {
            throw new StoreSaasMarketingException("卡未激活");
        }
        //如果次数为正数才判断次卡是否过期
        Map<String, Integer> itemQuantity = updateCardVo.getItemQuantity();
        for (Integer value : itemQuantity.values()) {
            if (value.compareTo(0) > 0 && card.getForever().intValue() == 0 && DataTimeUtil.getDateZeroTime(card.getExpiryDate()).getTime() < System.currentTimeMillis()){
                throw new StoreSaasMarketingException("次卡已过期");
            }
        }

        String key = "updateCardQuantity:" + updateCardVo.getCardId();
        RedisUtils redisUtils = new RedisUtils(redisTemplate, "STORE-SAAS-MARKETING-");
        StoreRedisUtils storeRedisUtils = new StoreRedisUtils(redisUtils, redisTemplate);
        Object value = storeRedisUtils.tryLock(key, 10, 10);
        Boolean ok = true;
        if (null != value) {
            try {
                CrdCardItemExample example = new CrdCardItemExample();
                example.createCriteria().andStoreIdEqualTo(updateCardVo.getStoreId())
                        .andTenantIdEqualTo(updateCardVo.getTenantId())
                        .andCardIdEqualTo(updateCardVo.getCardId());
                List<CrdCardItem> cardItems = cardItemMapper.selectByExample(example);
                Map<String,List<CrdCardItem>> cardItemsMap = cardItems.stream().collect(Collectors.groupingBy(x->x.getGoodsId()));
                Date date = new Date();
                for (String goodsId : cardItemsMap.keySet()){
                    if (itemQuantity.containsKey(goodsId)){
                        //计算次数
                        Integer totalQuantity = itemQuantity.get(goodsId);
                        for (CrdCardItem item : cardItemsMap.get(goodsId)){
                            Integer quantity = totalQuantity + item.getUsedQuantity();
                            Integer usedQuantity = item.getUsedQuantity();
                            if (quantity.compareTo(item.getMeasuredQuantity()) > 0) {
                                //更新
                                item.setUsedQuantity(item.getMeasuredQuantity());
                            } else if (quantity.compareTo(0) < 0) {
                                //更新
                                item.setUsedQuantity(0);
                            } else {
                                item.setUsedQuantity(quantity);
                            }
                            totalQuantity -= (item.getUsedQuantity() - usedQuantity);
                            item.setUpdateTime(date);
                            Integer result = cardItemMapper.updateByPrimaryKeySelective(item);
                            if (result <= 0) {
                                log.error("cardItem update失败");
                                throw new StoreSaasMarketingException("次卡更新异常");
                            }
                        }
                        if (totalQuantity.compareTo(0) > 0){
                            throw new StoreSaasMarketingException("次卡更新失败，更新后次数超过可用数");
                        }
                        if (totalQuantity.compareTo(0) < 0){
                            throw new StoreSaasMarketingException("次卡更新失败，更新后次数小于0");
                        }
                    }
                }
            } finally {
                storeRedisUtils.releaseLock(key, value.toString());
            }
        } else {
            ok = false;
        }
        return ok;
    }

    @Override
    public Boolean hasCardByCustomerId(String id, Long storeId, Long tenantId) {
        log.info("hasCardByCustomerId -> req= {},{},{}", id, storeId, tenantId);
        Boolean result = Boolean.FALSE;
        if (StringUtils.isNotEmpty(id) && storeId != null && tenantId != null) {
            CrdCardExample cardExample = new CrdCardExample();
            cardExample.createCriteria().andCustomerIdEqualTo(id)
                    .andStoreIdEqualTo(storeId).andTenantIdEqualTo(tenantId);
            result = cardMapper.countByExample(cardExample) > 0;
        } else {
            throw new StoreSaasMarketingException("参数验证失败");
        }
        return result;
    }

    @Override
    public List<CardResp> queryCardRespList(MiniQueryCardReq req) {
        log.info("查询客户次卡，请求参数：{}", JSONObject.toJSON(req));
        CrdCardExample cardExample = new CrdCardExample();
        CrdCardExample.Criteria criteria = cardExample.createCriteria();
        criteria.andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId());
        if (null != req.getCustomerId()) {
            criteria.andCustomerIdEqualTo(req.getCustomerId());
        }
        if (null != req.getCustomerPhoneNumber()) {
            criteria.andCustomerPhoneNumberEqualTo(req.getCustomerPhoneNumber());
        }
        criteria.andStatusEqualTo(CardStatusEnum.ACTIVATED.getEnumCode());
        cardExample.setOrderByClause("update_time desc");
        List<CrdCard> cardList = cardMapper.selectByExample(cardExample);

        Map<Long, Long> cardOrderIdMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(cardList)) {
            CrdCardOrderExample crdCardOrderExample = new CrdCardOrderExample();
            crdCardOrderExample.createCriteria().andCardIdIn(cardList.stream().map(x -> x.getId()).collect(Collectors.toList()));
            List<CrdCardOrder> crdCardOrderList = crdCardOrderMapper.selectByExample(crdCardOrderExample);
            if (CollectionUtils.isNotEmpty(crdCardOrderList)) {
                cardOrderIdMap.putAll(crdCardOrderList.stream().collect(Collectors.toMap(k -> k.getCardId(), v -> v.getId(), (i, j) -> i)));
            }
        }
        List<CardResp> cardRespList = new ArrayList<>();
        for (CrdCard card : cardList) {
            CardResp resp = new CardResp();
            BeanUtils.copyProperties(card, resp);
            resp.setCardStatus(CardStatusEnum.valueOf(card.getStatus()).getDescription());
            resp.setCardStatusCode(CardStatusEnum.valueOf(card.getStatus()).getEnumCode());
            resp.setForever(card.getForever() == 1 ? true : false);
            resp.setCardOrderId(cardOrderIdMap.get(card.getId()));
            resp.setCardStatus(CardStatusEnum.ACTIVATED.getDescription());
            resp.setCardStatusCode(CardStatusEnum.ACTIVATED.getEnumCode());
            resp.setSort(CardStatusEnum.ACTIVATED.getSort());

            if (!resp.getForever()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                resp.setExpiryDate(dateFormat.format(card.getExpiryDate()));
                resp.setDate(card.getExpiryDate());
                Date date = new Date();
                Date expiryDate = DataTimeUtil.getDateZeroTime(card.getExpiryDate());
                if (date.compareTo(expiryDate) > 0) {
                    resp.setCardStatus(CardStatusEnum.EXPIRED.getDescription());
                    resp.setCardStatusCode(CardStatusEnum.EXPIRED.getEnumCode());
                    resp.setSort(CardStatusEnum.EXPIRED.getSort());
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
            resp.setCardServiceItem(cardServiceItem);
            resp.setCardGoodsItem(cardGoodsItem);
            if (remainQuantity.compareTo(0L) <= 0) {
                resp.setCardStatus(CardStatusEnum.FINISHED.getDescription());
                resp.setCardStatusCode(CardStatusEnum.FINISHED.getEnumCode());
                resp.setSort(CardStatusEnum.FINISHED.getSort());
            }
            if (null == req.getCardStatus() ||
                    resp.getCardStatusCode().equals(req.getCardStatus())) {
                cardRespList.add(resp);
            }
        }

        Comparator<CardResp> statuCodeASC = Comparator.comparing(CardResp::getSort);
        // update time 降序
        Comparator<CardResp> byUpdateTimeDESC = Comparator.comparing(CardResp::getUpdateTime).reversed();
        Collections.sort(cardRespList, statuCodeASC.thenComparing(byUpdateTimeDESC));
        return cardRespList;
    }

    @Override
    public List<CardUseRecordResp> consumptionHistory(Long id) {
        BizBaseResponse<List<CardUseRecordDTO>> bizBaseResponse = serviceOrderClient.getCardUseRecord(id.toString());
        if (!bizBaseResponse.isSuccess()) {
            throw new StoreSaasMarketingException("获取使用记录失败");
        }
        List<CardUseRecordDTO> recordDTOList = bizBaseResponse.getData();
        List<CardUseRecordResp> respList = new ArrayList<>();
        for (CardUseRecordDTO dto : recordDTOList) {
            CardUseRecordResp resp = new CardUseRecordResp();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            resp.setTime(dateFormat.format(dto.getUseTime()));
            resp.setServiceOrderId(dto.getServiceOrderId());
            resp.setServiceOrderTypeCode(dto.getServiceOrderTypeCode());
            List<CardItemResp> item = new ArrayList<>();
            for (CardUseRecordDTO.ServiceOrderItem serviceOrderItem : dto.getServiceOrderItems()) {
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

    @Override
    public List<QueryCardItemResp> queryCardItem(QueryCardItemReq req) {
        log.info("查询次卡服务项目/商品列表，请求参数：{}", JSONObject.toJSON(req));

        CrdCardItemExample example = new CrdCardItemExample();
        CrdCardItemExample.Criteria criteria = example.createCriteria();
        criteria.andCardIdEqualTo(req.getCardId())
                .andStoreIdEqualTo(req.getStoreId())
                .andTenantIdEqualTo(req.getTenantId())
                .andTypeEqualTo(req.getType().byteValue());
        List<CrdCardItem> cardItems = cardItemMapper.selectByExample(example);
        List<QueryCardItemResp> queryCardItemResps = new ArrayList<>();
        List<String> goodsIdList = new ArrayList<>();
        Map<String,QueryCardItemResp> cardItemRespMap = new HashMap<>();
        for (CrdCardItem item : cardItems) {
            if (cardItemRespMap.containsKey(item.getGoodsId())){
                QueryCardItemResp resp = cardItemRespMap.get(item.getGoodsId());
                resp.setMeasuredQuantity(resp.getMeasuredQuantity()+item.getMeasuredQuantity());
                resp.setUsedQuantity(resp.getUsedQuantity()+item.getUsedQuantity());
                resp.setRemainQuantity(resp.getMeasuredQuantity()-resp.getUsedQuantity());
            } else {
                QueryCardItemResp resp = new QueryCardItemResp();
                BeanUtils.copyProperties(item, resp);
                resp.setRemainQuantity(resp.getMeasuredQuantity() - resp.getUsedQuantity());
                queryCardItemResps.add(resp);
                goodsIdList.add(item.getGoodsId());
                cardItemRespMap.put(item.getGoodsId(),resp);
            }
        }

        //查商品
        if (req.getType() == 2 && !goodsIdList.isEmpty()) {
            List<QueryGoodsListDTO> queryGoodsListDTOS = this.queryGoodsInfoList(goodsIdList,req.getStoreId(),req.getTenantId(),req.getSearch());
            LinkedHashMap<String, BigDecimal> inventoryMap = new LinkedHashMap();
            String warehouseId = null;
            String warehouseName = null;
            // 商品 - 查库存
            if (!goodsIdList.isEmpty()) {
                StoreInfoRelatedDTO storeRelatedResponse = storeInfoClient.getRelatedInfoByStoreId(req.getStoreId()).getData();
                log.info("查询门店仓库信息返回：{}", JSON.toJSONString(storeRelatedResponse));
                if (null == storeRelatedResponse) {
                    throw new StoreSaasMarketingException("获取门店关联的信息异常");
                }
                warehouseId = String.valueOf(storeRelatedResponse.getStoreOutPurchaseWarehouseId());
                warehouseName = storeRelatedResponse.getStoreOutPurchaseWarehouseName();
                log.info("获取门店WMS库存,ids={}", JSONObject.toJSONString(goodsIdList));
                StkQtyRequest request = new StkQtyRequest();
                request.setWarehouseId(warehouseId);
                request.setSkuIdList(goodsIdList);
                request.setDamaged(DamagedEnum.NORMAL.getType());
                try {
                    BizRsp<List<StkQtyDto>> stkQtyDtoListResp = storeWmsClient.listQty(request);
                    log.info("查询门店库存信息返回：{}", JSON.toJSONString(stkQtyDtoListResp));
                    if (null != stkQtyDtoListResp && 1 == stkQtyDtoListResp.getStatus() && stkQtyDtoListResp.getData() != null) {
                        List<StkQtyDto> stkQtyDtoList = stkQtyDtoListResp.getData();
                        for (StkQtyDto dto : stkQtyDtoList) {
                            inventoryMap.put(dto.getSkuId(), dto.getQty());
                        }
                    } else {
                        log.warn("根据门店商品ID和仓库ID未查询到库存信息,goodsIdList={},warehouseId={}",
                                JSONObject.toJSONString(goodsIdList), warehouseId);
                        throw new StoreSaasMarketingException("获取门店关联的信息异常");
                    }
                } catch (Exception e) {
                    log.error("根据门店商品ID和仓库ID查询库存信息异常", e);
                    throw new StoreSaasMarketingException("根据门店商品ID和仓库ID查询库存信息异常");
                }
            }
            Map<String,QueryGoodsListDTO> goodsListDTOMap = queryGoodsListDTOS.stream().collect(Collectors.toMap(x->x.getGoodsId(),v->v));
            for (QueryCardItemResp queryCardItemResp : queryCardItemResps){
                if (goodsListDTOMap.containsKey(queryCardItemResp.getGoodsId())){
                    QueryGoodsListDTO dto = goodsListDTOMap.get(queryCardItemResp.getGoodsId());
                    QueryCardItemResp.CardGoods resp = new QueryCardItemResp.CardGoods();
                    BeanUtils.copyProperties(dto, resp);
                    resp.setBusinessCategory(dto.getBusinessCategoryCode());
                    if (null != dto.getProductId()) {
                        resp.setProductId(String.valueOf(dto.getProductId()));
                    }
                    resp.setUsedNum(inventoryMap.getOrDefault(resp.getGoodsId(), BigDecimal.ZERO));
                    resp.setWarehouseId(warehouseId);
                    resp.setWarehouseName(warehouseName);
                    queryCardItemResp.setGoods(resp);
                }
            }
        }
        //查服务
        if (req.getType() == 1 && !goodsIdList.isEmpty()) {
            List<ServiceGoodsListForMarketResp> serviceGoodsList = this.queryServiceInfoList(goodsIdList,req.getStoreId(),req.getTenantId(),req.getSearch());
            Map<String,ServiceGoodsListForMarketResp> serviceListMap = serviceGoodsList.stream().collect(Collectors.toMap(x->x.getId(),v->v));
            for (QueryCardItemResp queryCardItemResp : queryCardItemResps){
                if (serviceListMap.containsKey(queryCardItemResp.getGoodsId())){
                    QueryCardItemResp.CardService resp = new QueryCardItemResp.CardService();
                    BeanUtils.copyProperties(serviceListMap.get(queryCardItemResp.getGoodsId()), resp);
                    queryCardItemResp.setService(resp);
                }
            }
        }
        return queryCardItemResps;
    }

    @Override
    public CardResp clientQueryCardItem(QueryCardItemReq req) {
        CardResp cardResp = new CardResp();
        CrdCardItemExample example = new CrdCardItemExample();
        CrdCardItemExample.Criteria criteria = example.createCriteria();
        criteria.andCardIdEqualTo(req.getCardId())
                .andStoreIdEqualTo(req.getStoreId())
                .andTenantIdEqualTo(req.getTenantId());
        List<CrdCardItem> cardItems = cardItemMapper.selectByExample(example);
        List<CardItemResp> cardServiceItem = new ArrayList<>();
        List<CardItemResp> cardGoodsItem = new ArrayList<>();
        for (CrdCardItem item : cardItems) {
            CardItemResp itemResp = new CardItemResp();
            BeanUtils.copyProperties(item, itemResp);
            itemResp.setRemainQuantity(itemResp.getMeasuredQuantity() - itemResp.getUsedQuantity());
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
            List<QueryGoodsListDTO> queryGoodsListDTOList = this.queryGoodsInfoList(goodsIdList,req.getStoreId(),req.getTenantId(),req.getSearch());
            Map<String,QueryGoodsListDTO> goodsListDTOMap = queryGoodsListDTOList.stream().collect(Collectors.toMap(x->x.getGoodsId(),v -> v));
            for (CardItemResp item : cardGoodsItem) {
                if (goodsListDTOMap.containsKey(item.getGoodsId())){
                    item.setServiceItemName(goodsListDTOMap.get(item.getGoodsId()).getGoodsName());
                }
            }
        }
        //查询最新服务信息
        List<String> serviceIdList = new ArrayList<>();
        for (CardItemResp item : cardServiceItem) {
            serviceIdList.add(item.getGoodsId());
        }
        if (!serviceIdList.isEmpty()) {
            List<ServiceGoodsListForMarketResp> serviceGoodsList = this.queryServiceInfoList(serviceIdList,req.getStoreId(),req.getTenantId(),req.getSearch());
            Map<String,ServiceGoodsListForMarketResp> serviceGoodsListForMarketRespMap = serviceGoodsList.stream().collect(Collectors.toMap(x->x.getId(),v -> v));
            for (CardItemResp item : cardServiceItem) {
                if (serviceGoodsListForMarketRespMap.containsKey(item.getGoodsId())){
                    item.setServiceItemName(serviceGoodsListForMarketRespMap.get(item.getGoodsId()).getServiceName());
                }
            }
        }
        cardResp.setCardGoodsItem(cardGoodsItem);
        cardResp.setCardServiceItem(cardServiceItem);
        return cardResp;
    }

    @Override
    public CustomerMarketCountDTO queryCustomerMarketInfo(String customerId) {
        CustomerMarketCountDTO customerMarketCountDTO = new CustomerMarketCountDTO();
        //统计客户次卡总数只统计已付款结清的次卡总数
        int onceCardCount = cardMapper.countByCustomerId(customerId);
        customerMarketCountDTO.setOnceCardCount(onceCardCount);
        Integer couponCount = customerCouponMapper.countCustomerCoupon(customerId);
        customerMarketCountDTO.setCouponCount(couponCount);
        return customerMarketCountDTO;
    }

    @Override
    public List<QueryCardItemResp> queryCardItemByCustomer(QueryByCustomerIdReq req) {
        log.info("查询客户可用次卡项目/商品，请求参数：{}", JSONObject.toJSON(req));
        if (null == req.getStoreId() || null == req.getTenantId() || (null == req.getCustomerId() && null == req.getCustomerPhoneNumber())){
            log.error("参数校验失败，req={}",req);
            throw new StoreSaasMarketingException("参数校验失败");
        }
        List<QueryCardItemResp> resultList = new ArrayList<>();
        //查客户可用次卡项目
        QueryAvailableItemsReq queryAvailableItemsReq = new QueryAvailableItemsReq();
        if (null != req.getCustomerId()){
            queryAvailableItemsReq.setCustomerId(req.getCustomerId());
        } else if (null != req.getCustomerPhoneNumber()) {
            queryAvailableItemsReq.setCustomerPhoneNumber(req.getCustomerPhoneNumber());
        }
        queryAvailableItemsReq.setType(req.getType());
        queryAvailableItemsReq.setStoreId(req.getStoreId());
        queryAvailableItemsReq.setTenantId(req.getTenantId());
        queryAvailableItemsReq.setDate(DataTimeUtil.getDateStartTime(new Date()));
        //未过期 且 可用次数大于0 按 临近有效期排序
        List<CrdCardItem> cardItems = cardItemMapper.selectAvailableItems(queryAvailableItemsReq);
        if (CollectionUtils.isNotEmpty(cardItems)){
            List<String> goodsIdList = cardItems.stream().map(x->x.getGoodsId()).distinct().collect(Collectors.toList());
            Map<String,List<CrdCardItem>> cardItemsMap = cardItems.stream().collect(Collectors.groupingBy(x->x.getGoodsId()));
            //查商品
            if (req.getType() == 2 && !goodsIdList.isEmpty()) {
                this.getCardGoodsList(req,goodsIdList,cardItemsMap,resultList);
            }
            //查服务
            if (req.getType() == 1 && !goodsIdList.isEmpty()) {
                this.getCardServiceList(req,goodsIdList,cardItemsMap,resultList);
            }
        }
        return resultList;
    }

    //获取服务信息
    private List<ServiceGoodsListForMarketResp> queryServiceInfoList(List<String> goodsIdList,Long storeId,Long tenantId,String search){
        GoodsForMarketReq goodsForMarketReq = new GoodsForMarketReq();
        goodsForMarketReq.setGoodsName("");
        if (null != search) {
            goodsForMarketReq.setGoodsName(search);
        }
        goodsForMarketReq.setStoreId(storeId);
        goodsForMarketReq.setTenantId(tenantId);
        goodsForMarketReq.setServiceIdList(goodsIdList);
        goodsForMarketReq.setPageSize(500);
        BizBaseResponse<PageInfo<ServiceGoodsListForMarketResp>> serviceGoodsPage = productClient.serviceGoodsForFeign(goodsForMarketReq);
        List<ServiceGoodsListForMarketResp> serviceGoodsList = new ArrayList<>();
        if (null != serviceGoodsPage.getData() && null != serviceGoodsPage.getData().getList()) {
            serviceGoodsList = serviceGoodsPage.getData().getList();
        }
        return serviceGoodsList;
    }

    //通过code获取服务信息
    private List<ServiceGoodsListForMarketResp> getServiceByCode(List<String> codeList,Long storeId,Long tenantId,String vehicleType){
        GoodsForMarketReq goodsForMarketReq = new GoodsForMarketReq();
        goodsForMarketReq.setStoreId(storeId);
        goodsForMarketReq.setTenantId(tenantId);
        goodsForMarketReq.setVehicleType(vehicleType);
        BizBaseResponse<List<ServiceGoodsListForMarketResp>> serviceGoodsPage = productClient.getServiceGoodsByCode(goodsForMarketReq,codeList);
        List<ServiceGoodsListForMarketResp> serviceGoodsList = new ArrayList<>();
        if (null != serviceGoodsPage.getData()) {
            serviceGoodsList = serviceGoodsPage.getData();
        }
        return serviceGoodsList;
    }

    //获取次卡服务列表
    private void getCardServiceList(QueryByCustomerIdReq req, List<String> goodsIdList, Map<String,List<CrdCardItem>> cardItemsMap, List<QueryCardItemResp> resultList){
        List<ServiceGoodsListForMarketResp> serviceGoodsList = this.queryServiceInfoList(goodsIdList,req.getStoreId(),req.getTenantId(),req.getSearch());
        Map<String,ServiceGoodsListForMarketResp> serviceListMap = serviceGoodsList.stream().collect(Collectors.toMap(x->x.getId(),v->v));
        for (String goodsId : goodsIdList){
            if (serviceListMap.containsKey(goodsId)){
                //组装服务信息
                QueryCardItemResp.CardService resp = new QueryCardItemResp.CardService();
                BeanUtils.copyProperties(serviceListMap.get(goodsId), resp);
                QueryCardItemResp queryCardItemResp = new QueryCardItemResp();
                queryCardItemResp.setGoodsId(goodsId);
                queryCardItemResp.setService(resp);
                //组装服务的 次卡信息
                Integer remainQuantity = 0; //可用总次数
                List<QueryCardItemResp.Cards> cardsList = new ArrayList<>();
                if (cardItemsMap.containsKey(goodsId)){
                    List<CrdCardItem> cardItemList = cardItemsMap.get(goodsId);
                    for (CrdCardItem cardItem : cardItemList){
                        QueryCardItemResp.Cards cards = new QueryCardItemResp.Cards();
                        cards.setCardId(cardItem.getCardId());
                        cards.setCardName(cardItem.getCardName());
                        cards.setRemainQuantity(cardItem.getMeasuredQuantity() - cardItem.getUsedQuantity());
                        cardsList.add(cards);
                        remainQuantity += cards.getRemainQuantity();
                    }
                }
                queryCardItemResp.setCards(cardsList);
                queryCardItemResp.setRemainQuantity(remainQuantity);
                resultList.add(queryCardItemResp);
            }
        }
    }

    //获取商品信息
    private List<QueryGoodsListDTO> queryGoodsInfoList(List<String> goodsIdList,Long storeId,Long tenantId,String search){
        QueryGoodsListVO queryGoodsListVO = new QueryGoodsListVO();
        queryGoodsListVO.setStoreId(storeId);
        queryGoodsListVO.setTenantId(tenantId);
        queryGoodsListVO.setGoodsIdList(goodsIdList);
        queryGoodsListVO.setGoodsSource("");
        BizBaseResponse<List<QueryGoodsListDTO>> productResult = productClient.queryGoodsListV2(queryGoodsListVO);
        List<QueryGoodsListDTO> queryGoodsListDTOS = new ArrayList<>();
        if (productResult != null && CollectionUtils.isNotEmpty(productResult.getData())) {
            queryGoodsListDTOS = productResult.getData();
            if (null != search) {
                queryGoodsListDTOS = queryGoodsListDTOS.stream()
                        .filter(x -> x.getBrandName().indexOf(search) > -1 || x.getGoodsName().indexOf(search) > -1
                                || x.getGoodsCode().indexOf(search) > -1).collect(Collectors.toList());
                goodsIdList.clear();
                queryGoodsListDTOS.stream().forEach(x -> goodsIdList.add(x.getGoodsId()));
            }
        }
        return queryGoodsListDTOS;
    }

    //获取次卡商品列表
    private void getCardGoodsList(QueryByCustomerIdReq req, List<String> goodsIdList, Map<String,List<CrdCardItem>> cardItemsMap, List<QueryCardItemResp> resultList){
        List<QueryGoodsListDTO> queryGoodsListDTOS = this.queryGoodsInfoList(goodsIdList,req.getStoreId(),req.getTenantId(),req.getSearch());
        LinkedHashMap<String, BigDecimal> inventoryMap = new LinkedHashMap();
        String warehouseId = null;
        String warehouseName = null;
        // 商品 - 查库存
        if (!goodsIdList.isEmpty()) {
            StoreInfoRelatedDTO storeRelatedResponse = storeInfoClient.getRelatedInfoByStoreId(req.getStoreId()).getData();
            log.info("查询门店仓库信息返回：{}", JSON.toJSONString(storeRelatedResponse));
            if (null == storeRelatedResponse) {
                throw new StoreSaasMarketingException("获取门店关联的信息异常");
            }
            warehouseId = String.valueOf(storeRelatedResponse.getStoreOutPurchaseWarehouseId());
            warehouseName = storeRelatedResponse.getStoreOutPurchaseWarehouseName();
            log.info("获取门店WMS库存,ids={}", JSONObject.toJSONString(goodsIdList));
            StkQtyRequest request = new StkQtyRequest();
            request.setWarehouseId(warehouseId);
            request.setSkuIdList(goodsIdList);
            request.setDamaged(DamagedEnum.NORMAL.getType());
            try {
                BizRsp<List<StkQtyDto>> stkQtyDtoListResp = storeWmsClient.listQty(request);
                log.info("查询门店库存信息返回：{}", JSON.toJSONString(stkQtyDtoListResp));
                if (null != stkQtyDtoListResp && 1 == stkQtyDtoListResp.getStatus() && stkQtyDtoListResp.getData() != null) {
                    List<StkQtyDto> stkQtyDtoList = stkQtyDtoListResp.getData();
                    for (StkQtyDto dto : stkQtyDtoList) {
                        inventoryMap.put(dto.getSkuId(), dto.getQty());
                    }
                } else {
                    log.warn("根据门店商品ID和仓库ID未查询到库存信息,goodsIdList={},warehouseId={}",
                            JSONObject.toJSONString(goodsIdList), warehouseId);
                    throw new StoreSaasMarketingException("获取门店关联的信息异常");
                }
            } catch (Exception e) {
                log.error("根据门店商品ID和仓库ID查询库存信息异常", e);
                throw new StoreSaasMarketingException("根据门店商品ID和仓库ID查询库存信息异常");
            }
        }
        Map<String,QueryGoodsListDTO> goodsListDTOMap = queryGoodsListDTOS.stream().collect(Collectors.toMap(x->x.getGoodsId(),v->v));

        for (String goodsId : goodsIdList){
            if (goodsListDTOMap.containsKey(goodsId)){
                //组装商品信息
                QueryGoodsListDTO dto = goodsListDTOMap.get(goodsId);
                QueryCardItemResp.CardGoods resp = new QueryCardItemResp.CardGoods();
                BeanUtils.copyProperties(dto, resp);
                resp.setBusinessCategory(dto.getBusinessCategoryCode());
                if (null != dto.getProductId()) {
                    resp.setProductId(String.valueOf(dto.getProductId()));
                }
                resp.setUsedNum(inventoryMap.getOrDefault(resp.getGoodsId(), BigDecimal.ZERO));
                resp.setWarehouseId(warehouseId);
                resp.setWarehouseName(warehouseName);
                QueryCardItemResp queryCardItemResp = new QueryCardItemResp();
                queryCardItemResp.setGoodsId(goodsId);
                queryCardItemResp.setGoods(resp);

                //组装商品 次卡信息
                Integer remainQuantity = 0; //可用总次数
                List<QueryCardItemResp.Cards> cardsList = new ArrayList<>();
                if (cardItemsMap.containsKey(goodsId)){
                    List<CrdCardItem> cardItemList = cardItemsMap.get(goodsId);
                    for (CrdCardItem cardItem : cardItemList){
                        QueryCardItemResp.Cards cards = new QueryCardItemResp.Cards();
                        cards.setCardId(cardItem.getCardId());
                        cards.setCardName(cardItem.getCardName());
                        cards.setRemainQuantity(cardItem.getMeasuredQuantity() - cardItem.getUsedQuantity());
                        cardsList.add(cards);
                        remainQuantity += cards.getRemainQuantity();
                    }
                }
                queryCardItemResp.setCards(cardsList);
                queryCardItemResp.setRemainQuantity(remainQuantity);
                resultList.add(queryCardItemResp);
            }
        }
    }

    @Override
    public List<QueryCardItemResp> allotCardItem(AllotCardItemReq req) {
        log.info("按有效期分配次卡项目，请求参数：{}", JSONObject.toJSON(req));
        if (null == req.getStoreId() || null == req.getTenantId() || (null == req.getCustomerId() && null == req.getCustomerPhoneNumber())){
            log.error("参数校验失败，req=",req);
            throw new StoreSaasMarketingException("参数校验失败");
        }
        List<QueryCardItemResp> resultList = new ArrayList<>();
        //查客户可用次卡项目
        QueryAvailableItemsReq queryAvailableItemsReq = new QueryAvailableItemsReq();
        if (null != req.getCustomerId()){
            queryAvailableItemsReq.setCustomerId(req.getCustomerId());
        } else if (null != req.getCustomerPhoneNumber()) {
            queryAvailableItemsReq.setCustomerPhoneNumber(req.getCustomerPhoneNumber());
        }
        queryAvailableItemsReq.setType(null);
        queryAvailableItemsReq.setStoreId(req.getStoreId());
        queryAvailableItemsReq.setTenantId(req.getTenantId());
        queryAvailableItemsReq.setDate(DataTimeUtil.getDateStartTime(new Date()));
        //未过期 且 可用次数大于0 按 临近有效期排序
        List<CrdCardItem> cardItems = cardItemMapper.selectAvailableItems(queryAvailableItemsReq);

        if (CollectionUtils.isNotEmpty(cardItems)){
            //goodsId - 次数
            Map<String,Integer> goodsNumMap = req.getGoodsNumMap();
            //过滤出要分配的项目
            cardItems = cardItems.stream().filter(x -> goodsNumMap.containsKey(x.getGoodsId())).collect(Collectors.toList());
            //goodsId - 次卡项目
            Map<String,List<CrdCardItem>> cardItemsMap = cardItems.stream().collect(Collectors.groupingBy(x->x.getGoodsId()));
            List<String> goodsIdList = cardItems.stream().map(x->x.getGoodsId()).distinct().collect(Collectors.toList());
            List<String> goodsIds = cardItems.stream().filter(x->x.getType()==2).map(x->x.getGoodsId()).distinct().collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(goodsIdList)) {
                StoreInfoRelatedDTO storeRelatedResponse = storeInfoClient.getRelatedInfoByStoreId(req.getStoreId()).getData();
                log.info("查询门店仓库信息返回：{}", JSON.toJSONString(storeRelatedResponse));
                if (null == storeRelatedResponse) {
                    throw new StoreSaasMarketingException("获取门店关联的信息异常");
                }
                String warehouseId = String.valueOf(storeRelatedResponse.getStoreOutPurchaseWarehouseId());
                String warehouseName = storeRelatedResponse.getStoreOutPurchaseWarehouseName();
                Map<String,QueryGoodsListDTO> goodsListDTOMap = new HashMap<>();
                LinkedHashMap<String, BigDecimal> inventoryMap = new LinkedHashMap();
                if (CollectionUtils.isNotEmpty(goodsIds)){
                    //查商品信息
                    List<QueryGoodsListDTO> queryGoodsListDTOS = this.queryGoodsInfoList(goodsIdList,req.getStoreId(),req.getTenantId(),null);
                    goodsListDTOMap = queryGoodsListDTOS.stream().collect(Collectors.toMap(x->x.getGoodsId(),v->v));
                    //查商品可用库存
                    if (CollectionUtils.isNotEmpty(queryGoodsListDTOS)){
                        goodsIds = queryGoodsListDTOS.stream().map(x->x.getGoodsId()).distinct().collect(Collectors.toList());
                        log.info("获取门店WMS库存,ids={}", JSONObject.toJSONString(goodsIds));
                        StkQtyRequest request = new StkQtyRequest();
                        request.setWarehouseId(warehouseId);
                        request.setSkuIdList(goodsIds);
                        request.setDamaged(DamagedEnum.NORMAL.getType());
                        try {
                            BizRsp<List<StkQtyDto>> stkQtyDtoListResp = storeWmsClient.listQty(request);
                            log.info("查询门店库存信息返回：{}", JSON.toJSONString(stkQtyDtoListResp));
                            if (null != stkQtyDtoListResp && 1 == stkQtyDtoListResp.getStatus() && stkQtyDtoListResp.getData() != null) {
                                List<StkQtyDto> stkQtyDtoList = stkQtyDtoListResp.getData();
                                for (StkQtyDto dto : stkQtyDtoList) {
                                    inventoryMap.put(dto.getSkuId(), dto.getQty());
                                }
                            } else {
                                log.warn("根据门店商品ID和仓库ID未查询到库存信息,goodsIdList={},warehouseId={}",
                                        JSONObject.toJSONString(goodsIdList), warehouseId);
                                throw new StoreSaasMarketingException("获取门店关联的信息异常");
                            }
                        } catch (Exception e) {
                            log.error("根据门店商品ID和仓库ID查询库存信息异常", e);
                            throw new StoreSaasMarketingException("根据门店商品ID和仓库ID查询库存信息异常");
                        }
                    }
                }

                //查服务信息,跟据车型适配工时
                List<String> serviceCodeList = cardItems.stream().filter(x->(x.getType()==1)).map(x->x.getServiceItemCode()).distinct().collect(Collectors.toList());
                List<ServiceGoodsListForMarketResp> serviceGoodsList = this.getServiceByCode(serviceCodeList,req.getStoreId(),req.getTenantId(),req.getVehicleType());
                Map<String,ServiceGoodsListForMarketResp> serviceListMap = serviceGoodsList.stream().collect(Collectors.toMap(x->x.getServiceCode(),v->v));

                //生成返回结果
                for (String goodsId : goodsIdList){
                    if (goodsNumMap.containsKey(goodsId) && cardItemsMap.containsKey(goodsId)){
                        List<CrdCardItem> cardItemList = cardItemsMap.get(goodsId);
                        Integer num = goodsNumMap.get(goodsId);
                        //服务信息
                        QueryCardItemResp.CardService cardService = new QueryCardItemResp.CardService();
                        //商品信息
                        QueryCardItemResp.CardGoods cardGoods = new QueryCardItemResp.CardGoods();
                        if (CollectionUtils.isNotEmpty(cardItemList) && serviceListMap.containsKey(cardItemList.get(0).getServiceItemCode())){
                            ServiceGoodsListForMarketResp serviceGoods = serviceListMap.get(cardItemList.get(0).getServiceItemCode());
                            serviceGoods.setId(goodsId);
                            //copy服务信息
                            BeanUtils.copyProperties(serviceGoods, cardService);
                        }
                        if (goodsListDTOMap.containsKey(goodsId)){
                            //copy商品信息
                            QueryGoodsListDTO dto = goodsListDTOMap.get(goodsId);
                            BeanUtils.copyProperties(dto, cardGoods);
                            cardGoods.setBusinessCategory(dto.getBusinessCategoryCode());
                            if (null != dto.getProductId()) {
                                cardGoods.setProductId(String.valueOf(dto.getProductId()));
                            }
                            cardGoods.setWarehouseId(warehouseId);
                            cardGoods.setWarehouseName(warehouseName);
                            cardGoods.setUsedNum(inventoryMap.containsKey(goodsId)?inventoryMap.get(goodsId):BigDecimal.ZERO);
                        }
                        //按次卡 在前面的优先分配
                        int index = 0;
                        while (num > 0 && index < cardItemList.size()){
                            CrdCardItem item = cardItemList.get(index);
                            Integer remainQuantity = item.getMeasuredQuantity() - item.getUsedQuantity();
                            //拆成一条一条显示- -
                            while (num > 0 && remainQuantity > 0){
                                QueryCardItemResp queryCardItemResp = new QueryCardItemResp();
                                BeanUtils.copyProperties(item, queryCardItemResp);
                                queryCardItemResp.setRemainQuantity(1);
                                //服务
                                if (item.getType() == 1) {
                                    queryCardItemResp.setService(cardService);
                                } else { //商品
                                    queryCardItemResp.setGoods(cardGoods);
                                }
                                resultList.add(queryCardItemResp);
                                remainQuantity--;
                                num--;
                            }
                            index++;
                        }
                    }
                }
            }
        }
        return resultList;
    }


    private CardTemplate convertorToCardTemplate(CardTemplateModel cardTemplateModelReq) {
        CardTemplate cardTemplate = new CardTemplate();
        BeanUtils.copyProperties(cardTemplateModelReq, cardTemplate);
        cardTemplate.setUpdateUser(cardTemplateModelReq.getCreateUser());
        cardTemplate.setIsDelete(false);
        cardTemplateModelReq.setDiscountAmount(cardTemplateModelReq.getActualAmount().subtract(cardTemplateModelReq.getFaceAmount()));
        List<CardTemplateItem> cardTemplateList = new ArrayList<>();
        cardTemplate.setDiscountAmount(BigDecimal.ZERO);
//        cardTemplate.setActualAmount(BigDecimal.ZERO);
        cardTemplate.setActualAmount(cardTemplateModelReq.getActualAmount());
        cardTemplate.setFaceAmount(BigDecimal.ZERO);
        if (CollectionUtils.isNotEmpty(cardTemplateModelReq.getCardTemplateItemModelList())) {
            Map<String, List<CardTemplateItemModel>> collect = cardTemplateModelReq.getCardTemplateItemModelList().stream()
                    .collect(Collectors.groupingBy(k -> k.getGoodsId()));
            for (Map.Entry<String, List<CardTemplateItemModel>> entry : collect.entrySet()) {
                CardTemplateItemModel cardTemplateItemModel = entry.getValue().get(0);
                if (entry.getValue().size() > 1) {
                    Integer sum = Integer.valueOf(0);
                    //有多条时遍历数量  合并当前重复商品/ 服务 只追加数量。其他的以第一条为准
                    for (CardTemplateItemModel templateItemModel : entry.getValue()) {
                        sum += templateItemModel.getMeasuredQuantity();
                    }
                    cardTemplateItemModel.setMeasuredQuantity(sum);
                }
                CardTemplateItem cardTemplateItem = convertorToCardTemplateItem(cardTemplateModelReq.getCreateUser(), cardTemplateModelReq.getStoreId(), cardTemplateModelReq.getTenantId(), cardTemplateItemModel);
                BigDecimal quantity = new BigDecimal(cardTemplateItem.getMeasuredQuantity() == null ? 0 : cardTemplateItem.getMeasuredQuantity());
                //计算单次项目总优惠
                cardTemplateItem.setDiscountAmount(cardTemplateItem.getPrice().subtract(cardTemplateItem.getFaceAmount()).multiply(quantity));
                //计算单次项目 总单价
                cardTemplateItem.setActualAmount(cardTemplateItem.getFaceAmount().multiply(quantity));
                //计算总项实额
//                cardTemplate.setActualAmount(cardTemplate.getActualAmount().add(cardTemplateItem.getActualAmount()));
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

    /**
     * 获取客户 存值，优惠券,次卡
     *
     * @param customerIds
     * @return
     */
    public Map<String, CustomerMarketCountDTO> getCustomerIdMarketInfoMap(List<String> customerIds) {
        Map<String, CustomerMarketCountDTO> customerIdMarketInfoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(customerIds)) {
            //次卡支付总数
            List<CustomerIdMarketInfo> customerIdMarketInfos = cardMapper.countByCustomerIds(customerIds);
            Map<String, CustomerIdMarketInfo> customerIdCardMap = getCustomerIdMap(customerIdMarketInfos);
            //次卡有效总数
            Map<String, Integer> customerIdUseOnceCardCountMap = getCustomerIdUseOnceCardCountMap(customerIds);
            //优惠券
            List<CustomerIdMarketInfo> customerIdMarketInfos2 = customerCouponMapper.countByCustomerIds(customerIds);
            Map<String, CustomerIdMarketInfo> customerIdCouponMap = getCustomerIdMap(customerIdMarketInfos2);
            //储值卡
            List<CustomerIdMarketInfo> customerIdMarketInfos3 = valueCardMapper.countByCustomerIds(customerIds);
            Map<String, CustomerIdMarketInfo> customerIdValueCardMap = getCustomerIdMap(customerIdMarketInfos3);
            CustomerMarketCountDTO customerMarketCountDTO = null;
            for (String customerId : customerIds) {
                customerMarketCountDTO = new CustomerMarketCountDTO();
                CustomerIdMarketInfo coupon = customerIdCouponMap.get(customerId);
                CustomerIdMarketInfo valueCard = customerIdValueCardMap.get(customerId);
                CustomerIdMarketInfo card = customerIdCardMap.get(customerId);
                Integer useCount = customerIdUseOnceCardCountMap.get(customerId);
                customerMarketCountDTO.setCouponCount(null != coupon ? coupon.getCount() : 0);
                customerMarketCountDTO.setValueCardAmount(null != valueCard ? valueCard.getAmount() : BigDecimal.ZERO);
                customerMarketCountDTO.setUseOnceCardCount(null != useCount ? useCount : 0);
                customerMarketCountDTO.setOnceCardCount(null != card ? card.getCount() : 0);
                customerIdMarketInfoMap.put(customerId, customerMarketCountDTO);
            }
        }
        return customerIdMarketInfoMap;
    }

    private Map<String, CustomerIdMarketInfo> getCustomerIdMap(List<CustomerIdMarketInfo> customerIdMarketInfos) {
        if (CollectionUtils.isNotEmpty(customerIdMarketInfos)) {
            Map<String, CustomerIdMarketInfo> customerIdMarketInfoMap = customerIdMarketInfos.stream().collect(Collectors.toMap(x -> x.getCustomerId(), v -> v));
            return customerIdMarketInfoMap;
        }
        return Collections.emptyMap();
    }

    private Map<String, List<CrdCard>> getCustomerIdCardsMap(List<CrdCard> crdCards) {
        if (CollectionUtils.isNotEmpty(crdCards)) {
            Map<String, List<CrdCard>> customerIdCardMap = crdCards.stream().collect(Collectors.groupingBy(x -> (x.getCustomerId())));
            return customerIdCardMap;
        }
        return Collections.emptyMap();
    }

    /**
     * 客户对应的有效次卡数量
     * @param customerIds
     * @return
     */
    private Map<String, Integer> getCustomerIdUseOnceCardCountMap(List<String> customerIds) {
        if (CollectionUtils.isNotEmpty(customerIds)) {
            Map<String, Integer> customerIdUseOnceCardCount = new HashMap<>();
            List<CrdCard> crdCards = cardMapper.cardsByCustomerIds(customerIds);
            //开卡单 每个客户对应有效头的卡id集合
            Map<String, List<CrdCard>> customerIdCardsMap = getCustomerIdCardsMap(crdCards);
            for (Map.Entry<String, List<CrdCard>> entry : customerIdCardsMap.entrySet()) {
                //初始化，默认都是有效的
                customerIdUseOnceCardCount.put(entry.getKey(), CollectionUtils.isEmpty(entry.getValue()) ? 0 : entry.getValue().size());
            }
            if (CollectionUtils.isNotEmpty(crdCards)) {
                List<Long> cardIds = crdCards.stream().map(CrdCard::getId).collect(Collectors.toList());
                List<CrdCardItem> crdCardItems = crdCardItemMapper.crdCardItemsByCardIds(cardIds);
                if (CollectionUtils.isEmpty(crdCardItems)) {
                    return Collections.emptyMap();
                }
                //每个卡id对应的明细已使用次数小于等于0使用完
                Map<Long, CrdCardItem> cardIdCardItemMap = crdCardItems.stream().collect(Collectors.toMap(x -> x.getCardId(), v -> v));
                for (Map.Entry<String, List<CrdCard>> entry : customerIdCardsMap.entrySet()) {
                    if (CollectionUtils.isEmpty(entry.getValue())) {
                        continue;
                    }
                    Integer count = customerIdUseOnceCardCount.get(entry.getKey());
                    for (CrdCard crdCard : entry.getValue()) {
                        CrdCardItem crdCardItem = cardIdCardItemMap.get(crdCard.getId());
                        if (null == crdCardItem) {
                            continue;
                        }
                        if (null != crdCardItem.getMeasuredQuantity() && crdCardItem.getMeasuredQuantity() <= 0) { //过滤已使用完的次卡
                            count--;
                        }
                    }
                    customerIdUseOnceCardCount.put(entry.getKey(), count < 0 ? 0 : count);
                }
            }
            return customerIdUseOnceCardCount;
        }
        return Collections.emptyMap();
    }
}
