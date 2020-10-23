package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.springcloud.common.util.RedisUtils;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdReqVO;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.CustomerVO;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ValueCardChangeMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ValueCardMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ValueCardRuleMapper;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.remote.order.StoreReceivingClient;
import com.tuhu.store.saas.marketing.request.valueCard.*;
import com.tuhu.store.saas.marketing.response.valueCard.CustomerValueCardDetailResp;
import com.tuhu.store.saas.marketing.response.valueCard.QueryValueCardListResp;
import com.tuhu.store.saas.marketing.response.valueCard.QueryValueCardRuleResp;
import com.tuhu.store.saas.marketing.response.valueCard.ValueCardChangeResp;
import com.tuhu.store.saas.marketing.service.IValueCardService;
import com.tuhu.store.saas.marketing.util.CodeFactory;
import com.tuhu.store.saas.marketing.util.StoreRedisUtils;
import com.tuhu.store.saas.order.vo.finance.nonpayment.AddNonpaymentVO;
import com.tuhu.store.saas.order.vo.finance.receiving.AddReceivingVO;
import com.tuhu.store.saas.request.purchase.AddPurchaseReturnReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.Unsafe;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wangyuqing
 * @since 2020/10/19 13:42
 */
@Service
@Slf4j
public class ValueCardServiceImpl implements IValueCardService {

    @Autowired
    private ValueCardRuleMapper valueCardRuleMapper;

    @Autowired
    private ValueCardMapper valueCardMapper;

    @Autowired
    private ValueCardChangeMapper valueCardChangeMapper;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CodeFactory codeFactory;

    @Override
    @Transactional
    public AddValueCardRuleReq addValueCardRule(AddValueCardRuleReq req) {
        log.info("新增储值规则请求参数：{}",req);
        if (null == req.getStoreId() || null == req.getTenantId()){
            throw new StoreSaasMarketingException("参数校验失败");
        }
        //删除原规则
        valueCardRuleMapper.deleteByStoreIdAndTenantId(req.getStoreId(),req.getTenantId());
        Date date = new Date();
        ValueCardRule valueCardRule = new ValueCardRule();
        BeanUtils.copyProperties(req,valueCardRule);
        valueCardRule.setCreateTime(date);
        valueCardRule.setUpdateTime(date);
        if (CollectionUtils.isNotEmpty(req.getRuleList())){
            //排序
            req.getRuleList().sort(new Comparator<ValueCardRuleReq>() {
                @Override
                public int compare(ValueCardRuleReq o1, ValueCardRuleReq o2) {
                    return o1.getAmount().compareTo(o2.getAmount());
                }
            });
            for (ValueCardRuleReq ruleReq : req.getRuleList()){
                if(ruleReq.getPresentAmount().compareTo(BigDecimal.ZERO) <= 0){
                    throw new StoreSaasMarketingException("赠送金设置需大于0");
                }
                valueCardRule.setId(null);
                valueCardRule.setAmount(ruleReq.getAmount());
                valueCardRule.setPresentAmount(ruleReq.getPresentAmount());
                valueCardRuleMapper.insertSelective(valueCardRule);
            }
        } else {
            valueCardRuleMapper.insertSelective(valueCardRule);
        }
        return req;
    }

    @Override
    public QueryValueCardRuleResp queryValueCardRule(Long storeId, Long tenantId) {
        log.info("查询储值规则请求参数：storeId = {}, tenantId = {}",storeId,tenantId);
        if (null == storeId || null == tenantId){
            throw new StoreSaasMarketingException("参数校验失败");
        }
        ValueCardRuleExample example = new ValueCardRuleExample();
        example.createCriteria().andStoreIdEqualTo(storeId)
                .andTenantIdEqualTo(tenantId);
        List<ValueCardRule> ruleList = valueCardRuleMapper.selectByExample(example);
        QueryValueCardRuleResp resp = new QueryValueCardRuleResp();
        resp.setConditionLimit(new BigDecimal(-1));
        if (CollectionUtils.isNotEmpty(ruleList)){
            resp.setConditionLimit(ruleList.get(0).getConditionLimit());
            List<ValueCardRuleReq> ruleRespList = new ArrayList<>();
            for (ValueCardRule valueCardRule : ruleList){
                if (!(valueCardRule.getAmount() == null && valueCardRule.getPresentAmount() == null)){
                    ValueCardRuleReq ruleResp = new ValueCardRuleReq();
                    ruleResp.setAmount(valueCardRule.getAmount());
                    ruleResp.setPresentAmount(valueCardRule.getPresentAmount());
                    ruleRespList.add(ruleResp);
                }
            }
            resp.setRuleList(ruleRespList);
        }
        return resp;
    }

    @Override
    public Map<String, BigDecimal> queryTotalValue(Long storeId, Long tenantId) {
        log.info("查询门店会员储值总额请求参数：storeId = {}, tenantId = {}",storeId,tenantId);
        if (null == storeId || null == tenantId){
            throw new StoreSaasMarketingException("参数校验失败");
        }
        ValueCardExample example = new ValueCardExample();
        example.createCriteria().andStoreIdEqualTo(storeId)
                .andTenantIdEqualTo(tenantId).andIsDeleteEqualTo(false);
        List<ValueCard> valueCardList = valueCardMapper.selectByExample(example);
        BigDecimal principal = BigDecimal.ZERO;
        BigDecimal present = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(valueCardList)){
            for (ValueCard valueCard : valueCardList){
                principal = principal.add(valueCard.getAmount());
                present = present.add(valueCard.getPresentAmount());
            }
        }
        Map<String,BigDecimal> map = new HashMap<>();
        map.put("amount",principal.add(present));
        map.put("principal",principal);
        map.put("present",present);
        return map;
    }

    @Override
    public PageInfo<QueryValueCardListResp> queryDetailList(QueryValueCardListReq req) {
        log.info("查询储值明细列表请求参数：{}",req);
        if (null == req.getStoreId() || null == req.getTenantId()){
            throw new StoreSaasMarketingException("参数校验失败");
        }
        PageInfo<QueryValueCardListResp> respPageInfo = new PageInfo<>();
        //查询客户列表
        CustomerVO vo = new CustomerVO();
        vo.setStoreId(req.getStoreId());
        vo.setTenantId(req.getTenantId());
        vo.setQuery(req.getSearch());
        BizBaseResponse<List<CustomerDTO>> crmResult = customerClient.getCustomerByQuery(vo);
        if (null != crmResult && CollectionUtils.isNotEmpty(crmResult.getData())){
            List<String> customerIdList = crmResult.getData().stream().map(x->x.getId()).collect(Collectors.toList());
            //查询客户储值卡
            PageHelper.startPage(req.getPageNum(),req.getPageSize());
            ValueCardExample example = new ValueCardExample();
            example.createCriteria().andCustomerIdIn(customerIdList).andIsDeleteEqualTo(false);
            //排序方式
            if (req.getSortType().equals(0)){
                example.setOrderByClause("update_time desc");
            } else if (req.getSortType().equals(1)){
                example.setOrderByClause("amount+present_amount desc");
            } else if (req.getSortType().equals(2)){
                example.setOrderByClause("amount+present_amount asc");
            }
            List<ValueCard> valueCardList = valueCardMapper.selectByExample(example);
            PageInfo<ValueCard> pageInfo = new PageInfo<>(valueCardList);
            List<QueryValueCardListResp> queryValueCardListResps = new ArrayList<>();
            //补充客户信息
            Map<String,CustomerDTO> customerDTOMap = crmResult.getData().stream().collect(Collectors.toMap(x->x.getId(),v->v));
            for (ValueCard valueCard : pageInfo.getList()){
                QueryValueCardListResp resp = new QueryValueCardListResp();
                resp.setAmount(valueCard.getAmount().add(valueCard.getPresentAmount()));
                resp.setCardId(valueCard.getId());
                resp.setCustomerId(valueCard.getCustomerId());
                if (customerDTOMap.containsKey(resp.getCustomerId())){
                    CustomerDTO dto = customerDTOMap.get(resp.getCustomerId());
                    resp.setCustomerName(dto.getName());
                    resp.setCustomerPhone(dto.getPhoneNumber());
                }
                queryValueCardListResps.add(resp);
            }
            respPageInfo.setList(queryValueCardListResps);
            respPageInfo.setTotal(pageInfo.getTotal());
        }
        return respPageInfo;
    }

    @Override
    public CustomerValueCardDetailResp customerValueCardDetail(CustomerValueCardDetailReq req) {
        log.info("查询客户储值详情请求参数：{}",req);
        if (null == req.getStoreId() || null == req.getTenantId()){
            throw new StoreSaasMarketingException("参数校验失败");
        }
        ValueCard valueCard = null;
        if (null != req.getCardId()){
            valueCard = valueCardMapper.selectByPrimaryKey(req.getCardId());
        } else {
            ValueCardExample example = new ValueCardExample();
            example.createCriteria().andCustomerIdEqualTo(req.getCustomerId())
                    .andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId())
                    .andIsDeleteEqualTo(false);
            List<ValueCard> valueCardList = valueCardMapper.selectByExample(example);
            if (CollectionUtils.isNotEmpty(valueCardList)){
                valueCard = valueCardList.get(0);
            }
        }
        if (null == valueCard){
            throw new StoreSaasMarketingException("客户未开通储值卡");
        }
        CustomerValueCardDetailResp resp = new CustomerValueCardDetailResp();
        BeanUtils.copyProperties(valueCard,resp);
        resp.setCardId(valueCard.getId());
        resp.setAmount(valueCard.getAmount().add(valueCard.getPresentAmount()));
        resp.setPrincipalAmount(valueCard.getAmount());

        BaseIdReqVO baseIdReqVO = new BaseIdReqVO();
        baseIdReqVO.setId(valueCard.getCustomerId());
        baseIdReqVO.setStoreId(valueCard.getStoreId());
        baseIdReqVO.setTenantId(valueCard.getTenantId());
        BizBaseResponse<CustomerDTO> customerDTOBizBaseResponse = customerClient.getCustomerById(baseIdReqVO);
        CustomerDTO customerDTO = customerDTOBizBaseResponse.getData() != null ?
                customerDTOBizBaseResponse.getData() : new CustomerDTO();
        resp.setCustomerName(customerDTO.getName());
        resp.setCustomerPhone(customerDTO.getPhoneNumber());

        //查询储值变更
        ValueCardChangeExample cardChangeExample = new ValueCardChangeExample();
        cardChangeExample.createCriteria().andCardIdEqualTo(valueCard.getId())
                .andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId())
                .andIsDeleteEqualTo(false);
        List<ValueCardChange> cardChanges = valueCardChangeMapper.selectByExample(cardChangeExample);
        BigDecimal rechargeAmount = BigDecimal.ZERO;
        Integer rechargeCount = 0;
        BigDecimal consumptionAmount = BigDecimal.ZERO;
        Integer consumptionCount = 0;
        if (CollectionUtils.isNotEmpty(cardChanges)){
            for (ValueCardChange cardChange : cardChanges){
                if (cardChange.getChangeType().equals(1)){ //消费
                    consumptionAmount = consumptionAmount.add(cardChange.getChangePrincipal()).add(cardChange.getChangePresent());
                    consumptionCount++;
                } else if (cardChange.getChangeType().equals(2)){ //充值
                    rechargeAmount = rechargeAmount.add(cardChange.getChangePrincipal()).add(cardChange.getChangePresent());
                    rechargeCount++;
                }
            }
        }
        resp.setRechargeAmount(rechargeAmount);
        resp.setRechargeCount(rechargeCount);
        resp.setConsumptionAmount(consumptionAmount);
        resp.setConsumptionCount(consumptionCount);
        return resp;
    }

    @Override
    public PageInfo<ValueCardChangeResp> customerValueCardChangeList(CustomerValueCardDetailReq req) {
        log.info("查询客户储值变更明细详情请求参数：{}",req);
        if (null == req.getStoreId() || null == req.getTenantId()){
            throw new StoreSaasMarketingException("参数校验失败");
        }
        //如果没拿到卡id 使用客户id查询
        if (null == req.getCardId()){
            ValueCardExample example = new ValueCardExample();
            example.createCriteria().andCustomerIdEqualTo(req.getCustomerId())
                    .andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId())
                    .andIsDeleteEqualTo(false);
            List<ValueCard> valueCardList = valueCardMapper.selectByExample(example);
            if (CollectionUtils.isNotEmpty(valueCardList)){
                req.setCardId(valueCardList.get(0).getId());
            } else{
                throw new StoreSaasMarketingException("客户未开通储值卡");
            }
        }
        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        ValueCardChangeExample cardChangeExample = new ValueCardChangeExample();
        cardChangeExample.createCriteria().andCardIdEqualTo(req.getCardId())
                .andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId())
                .andStatusEqualTo(true).andIsDeleteEqualTo(false);
        cardChangeExample.setOrderByClause("update_time desc");
        List<ValueCardChange> cardChanges = valueCardChangeMapper.selectByExample(cardChangeExample);
        PageInfo<ValueCardChangeResp> respPageInfo = new PageInfo<>();
        if (CollectionUtils.isNotEmpty(cardChanges)){
            PageInfo<ValueCardChange> pageInfo = new PageInfo<>(cardChanges);
            List<ValueCardChangeResp> respList = new ArrayList<>();
            for (ValueCardChange cardChange : pageInfo.getList()){
                ValueCardChangeResp resp = new ValueCardChangeResp();
                BeanUtils.copyProperties(cardChange,resp);
                respList.add(resp);
            }
            respPageInfo.setList(respList);
            respPageInfo.setTotal(pageInfo.getTotal());
        }
        return respPageInfo;
    }

    @Override
    public Map<String, BigDecimal> customerValueCardAmount(CustomerValueCardDetailReq req) {
        log.info("查询客户储值卡余额请求参数：{}",req);
        if (null == req.getStoreId() || null == req.getTenantId()){
            throw new StoreSaasMarketingException("参数校验失败");
        }
        Map<String, BigDecimal> map = new HashMap<>();
        BigDecimal principal = BigDecimal.ZERO;
        BigDecimal present = BigDecimal.ZERO;
        ValueCard valueCard = null;
        if (null != req.getCardId()){
            valueCard = valueCardMapper.selectByPrimaryKey(req.getCardId());
        } else {
            ValueCardExample example = new ValueCardExample();
            example.createCriteria().andCustomerIdEqualTo(req.getCustomerId())
                    .andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId())
                    .andIsDeleteEqualTo(false);
            List<ValueCard> valueCardList = valueCardMapper.selectByExample(example);
            if (CollectionUtils.isNotEmpty(valueCardList)){
                valueCard = valueCardList.get(0);
            }
        }
        if (null != valueCard){
            principal = valueCard.getAmount();
            present = valueCard.getPresentAmount();
        }
        map.put("amount",principal.add(present));
        map.put("principal",principal);
        map.put("present",present);
        return map;
    }
    @Autowired
    private StoreReceivingClient storeReceivingClient;
    private  final  static  String settlementCacheKey = "ValueCardServiceImpl:settlement:customerIdKey:";

    /**
     * 退款校验
     */
    BiFunction<ValueCardRechargeOrRefundReq, ValueCard, Boolean> func = (req, model) -> {
        log.info("进入退款数据校验检查->  req :{}  model -> {} ", req, model);
        if (req.getChangePresent().compareTo(BigDecimal.ZERO) == Integer.valueOf(1)) {
            //退款金额为正数
            throw  new StoreSaasMarketingException("退款本金为正正数");
        }
        if (req.getChangePrincipal().compareTo(BigDecimal.ZERO) == Integer.valueOf(1)){
            throw  new StoreSaasMarketingException("退款赠金为正正数");
        }
        // 客户没有开过卡而且要退款的情况
        if (model == null) {
            throw new StoreSaasMarketingException("查询不到储值卡信息,无法退款");
        }
        //客户退款钱不够的情况 当本金-当前要扣除的本金 <0的话 证明不够扣的
        if (model.getAmount().add(req.getChangePrincipal()).compareTo(BigDecimal.ZERO) == Integer.valueOf(-1)) {
            throw new StoreSaasMarketingException("当前本金不足,无法退款");
        }
        //客户退款钱不够的情况 当赠送金-当前要扣除的赠送金 <0的话 证明不够扣的
        if (model.getPresentAmount().add(req.getChangePresent()).compareTo(BigDecimal.ZERO) == Integer.valueOf(-1)) {
            throw new StoreSaasMarketingException("当前本金不足,无法退款");
        }
        return Boolean.TRUE;
    };
    /**
     * 收款校验
     */
    Function<ValueCardRechargeOrRefundReq, Boolean> func1 = (req) -> {
        log.info("进入退款数据校验检查->  req :{}  model ->  ", req);
        if (req.getChangePresent().compareTo(BigDecimal.ZERO) == Integer.valueOf(-1)) {
            //退款金额为正数
            throw  new StoreSaasMarketingException("充值本金为负数");
        }
        if (req.getChangePrincipal().compareTo(BigDecimal.ZERO) == Integer.valueOf(-1)){
            throw  new StoreSaasMarketingException("充值赠金为负数");
        }
        return Boolean.TRUE;
    };


    @Override
    @Transactional
    public String settlement(ValueCardRechargeOrRefundReq req) {
        log.info("ValueCardServiceImpl->settlement-> req->{}", req);
        String result = null;
        if (redisTemplate.hasKey(settlementCacheKey.concat(req.getCustomerId()))) {
            throw new StoreSaasMarketingException("当前客户已经有该单据正在进行结算请稍后再试");
        }
        RedisUtils redisUtils = new RedisUtils(redisTemplate, "valueCardSettlement");
        StoreRedisUtils storeRedisUtils = new StoreRedisUtils(redisUtils, redisTemplate);
        //如果要去除分布式锁 请考虑并发环境
        Object value = storeRedisUtils.tryLock(settlementCacheKey.concat(req.getCustomerId()), 10, 10);
        if (value != null) {
            try {
                //查看当前用户余额记录
                ValueCardExample valueCardExample = new ValueCardExample();
                valueCardExample.createCriteria().andCustomerIdEqualTo(req.getCustomerId())
                        .andStoreIdEqualTo(req.getStoreId())
                        .andTenantIdEqualTo(req.getTenantId())
                        .andIsDeleteEqualTo(Boolean.FALSE);
                List<ValueCard> valueCards = valueCardMapper.selectByExample(valueCardExample);
                ValueCard valueCard = CollectionUtils.isNotEmpty(valueCards) ? valueCards.get(0) : null;
                if ((req.getType().equals(0) && func.apply(req, valueCard)) || (req.getType().equals(2) && func1.apply(req))) {
                    // 开始构建数据
                    // 包含支付流程标识  本金变动为0 赠金变动不为0  = true 包含 = flase 不包含
                    boolean hasPayProcess = !(req.getChangePrincipal().compareTo(BigDecimal.ZERO) == 0
                            && req.getChangePresent().compareTo(BigDecimal.ZERO) != 0);
                    //如果是新增 则初始化一条客户数据
                    valueCard = valueCard == null ? new ValueCard(req) : valueCard;
                    if (valueCard.getId() == null) {
                        // 如果不包含支付流程 则直接赋值金额
                        if (!hasPayProcess) {
                            valueCard.setAmount(req.getChangePrincipal());
                            valueCard.setPresentAmount(req.getChangePresent());
                        }
                        //insert
                        log.info("新增总表开始 valueCard->{}", valueCard);
                        if (valueCardMapper.insert(valueCard) <= 0) {
                            throw new StoreSaasMarketingException("新增客户卡信息失败");
                        }
                    } else if (valueCard.getId() != null && !hasPayProcess) {
                        //因为是分布式锁的环境 所以就不把计算金额的步骤写到SQL 里面去了

                        valueCard.setAmount(valueCard.getAmount().add(req.getChangePrincipal()));
                        valueCard.setPresentAmount(valueCard.getPresentAmount().add(req.getChangePresent()));
                        valueCard.setUpdateTime(new Date(System.currentTimeMillis()));
                        log.info("修改总表开始 valueCard->{}", valueCard);
                        //update 修改总余额
                        if (valueCardMapper.updateByPrimaryKey(valueCard) <= 0) {
                            throw new StoreSaasMarketingException("修改客户储值信息失败");
                        }
                    }
                    String codeNumber = codeFactory.getCodeNumber("CZBG", req.getStoreId());
                    codeNumber = "CZBG"+valueCard.getStoreId()+codeNumber;
                    //添加一条流水
                    ValueCardChange valueCardChange = new ValueCardChange(valueCard, req);
                    valueCardChange.setChangeNo(codeNumber);
                    //如果当前记录 本金为0  赠金不为0  则直接生效 不用走支付流程
                    if (!hasPayProcess) {
                        valueCardChange.setStatus(Boolean.TRUE);
                    }
                    //新增一条记录
                    if (valueCardChangeMapper.insert(valueCardChange) <= 0) {
                        throw new StoreSaasMarketingException("添加余额变更流水失败");
                    }
                    result = valueCardChange.getId().toString();
                    if (hasPayProcess) {
                        String  finNo = null;
                        BaseIdReqVO baseIdReqVO = new BaseIdReqVO();
                        baseIdReqVO.setId(valueCard.getCustomerId());
                        baseIdReqVO.setStoreId(valueCard.getStoreId());
                        baseIdReqVO.setTenantId(valueCard.getTenantId());
                        BizBaseResponse<CustomerDTO> customerDTOBizBaseResponse = customerClient.getCustomerById(baseIdReqVO);
                        CustomerDTO customerDTO = customerDTOBizBaseResponse.getData() != null ?
                                customerDTOBizBaseResponse.getData() : new CustomerDTO();
                        if (valueCardChange.getChangeType() == Integer.valueOf(2)) {
                            //生成一条预收款单
                            AddReceivingVO addReceivingVO = createAddReceingVo(valueCardChange, valueCard.getCustomerId(), customerDTO.getName(), customerDTO.getPhoneNumber());
                            log.info("储值待收参数：{}", JSONObject.toJSONString(addReceivingVO));
                            BizBaseResponse<String> addResultObject = storeReceivingClient.addReceivingForValueCard(addReceivingVO);
                            log.info("创建待收列表返回->{}", addResultObject);
                            if (null != addResultObject && addResultObject.isSuccess() && null != addResultObject.getData()) {
                                if (addResultObject.getData().equals(Boolean.FALSE)) {
                                    throw new StoreSaasMarketingException("创建退货单失败");
                                }
                                finNo = addResultObject.getData();
                            }
                        } else if (valueCardChange.getChangeType() == Integer.valueOf(0)) {
                            //生成一条待付单

                            //通知finance添加待付
                            AddNonpaymentVO addNonpaymentVO = createAddNonpaymentVO(valueCardChange, valueCard.getCustomerId(), customerDTO.getName(), customerDTO.getPhoneNumber());
                            finNo = storeReceivingClient.addNonpaymentForValueCard(addNonpaymentVO).getData();
                        }
                        if (StringUtils.isBlank(finNo)) {
                            throw new StoreSaasMarketingException("创建变更单失败");
                        }
                        //添加一个变更单号
                        valueCardChange.setFinNo(finNo);
                        valueCardChangeMapper.updateByPrimaryKey(valueCardChange);
                    }


                } else {
                    throw new StoreSaasMarketingException("数据校验失败");
                }


            } finally {
                storeRedisUtils.releaseLock(settlementCacheKey.concat(req.getCustomerId()), value.toString());
            }
        }

        return result;
    }

    private  AddNonpaymentVO createAddNonpaymentVO(ValueCardChange cardChange,String payerId,String payerName,String phone){
        log.info("预退款单初始化开始");
        AddNonpaymentVO addNonpaymentVO = new AddNonpaymentVO();
        addNonpaymentVO.setOrderId(cardChange.getId().toString());
        addNonpaymentVO.setOrderNo(cardChange.getChangeNo());
        addNonpaymentVO.setOrderDate(cardChange.getCreateTime());
        addNonpaymentVO.setBusinessCategoryCode("VALUE_CARD_CHANGE");
        addNonpaymentVO.setSellerId(payerId);
        addNonpaymentVO.setSellerName(payerName);
        // 2019年3月12日 产品要求改成采购人
        addNonpaymentVO.setSettlementOfficerId(cardChange.getCreateUserId());
        addNonpaymentVO.setSettlementOfficerName(cardChange.getCreateUserName());
        addNonpaymentVO.setCashierId(payerId);
        addNonpaymentVO.setCashierName(payerName);
        addNonpaymentVO.setCashierPhoneNumber(phone);
        //因为存的是负数所以这变需要变成正数
        long amount = Math.abs(( (cardChange.getChangePrincipal())
                .multiply(new BigDecimal(100))).longValue());
        addNonpaymentVO.setAmount(amount);
        addNonpaymentVO.setActualAmount(amount);
        addNonpaymentVO.setStatus("INIT");
        addNonpaymentVO.setStoreId(cardChange.getStoreId());
        addNonpaymentVO.setTenantId(cardChange.getTenantId());
        addNonpaymentVO.setCreateUser("系统生成");
        addNonpaymentVO.setType(new Byte("0"));
        return  addNonpaymentVO;
    }

    private AddReceivingVO createAddReceingVo(ValueCardChange cardChange,String payerId,String payerName,String phone) {
        log.info("预收款单初始化开始");
        AddReceivingVO receivingVO = new AddReceivingVO();
        Date now = new Date();
        receivingVO.setOrderId(cardChange.getId().toString());
        receivingVO.setOrderNo(cardChange.getChangeNo());
        receivingVO.setOrderDate(cardChange.getCreateTime());
        receivingVO.setBusinessCategoryCode("VALUE_CARD_CHANGE");
        receivingVO.setBusinessCategoryName("储值变更单");
        receivingVO.setPayerId(payerId);
        receivingVO.setPayerName(payerName);
        receivingVO.setPayerPhoneNumber(phone);
        receivingVO.setAmount((cardChange.getChangePrincipal().multiply(new BigDecimal(100))).longValue());
        receivingVO.setDiscountAmount(0L);
        receivingVO.setActualAmount(receivingVO.getAmount());
        receivingVO.setPayedAmount(0L);
        receivingVO.setStatus("INIT");
        receivingVO.setPaymentStatus("UNRECEIVABLE");
        receivingVO.setDescription("");
        receivingVO.setCreateTime(now);
        receivingVO.setCreateUser(cardChange.getCreateUserId());
        receivingVO.setStoreId(cardChange.getStoreId());
        receivingVO.setTenantId(cardChange.getTenantId());
        receivingVO.setStoreNo(UserContextHolder.getUser().getStoreNo());
        return receivingVO;
    }

    /*
     * 核销节点：结算页点击“选择收款方式”或“确认收款”后即核销储值余额
     * 核销规则：优先扣减本金，本金扣为0后，才开始扣减赠送金
     */
    @Override
    @Transactional
    public Boolean customerConsumption(ValueCardConsumptionReq req) {
        log.info("储值卡核销请求参数：{}",req);
        if (null == req.getStoreId() || null == req.getTenantId() || null == req.getCustomerId()){
            throw new StoreSaasMarketingException("参数校验失败");
        }
        String key = "valueCardConsumption:" + req.getCustomerId();
        RedisUtils redisUtils = new RedisUtils(redisTemplate, "STORE-SAAS-MARKETING-");
        StoreRedisUtils storeRedisUtils = new StoreRedisUtils(redisUtils, redisTemplate);
        Object value = storeRedisUtils.tryLock(key, 10, 10);
        Boolean result = true;
        if (null != value) {
            try {
                if (req.getAmount().compareTo(BigDecimal.ZERO) > 0){
                    ValueCard valueCard = null;
                    if (null != req.getCardId()){
                        valueCard = valueCardMapper.selectByPrimaryKey(req.getCardId());
                    } else {
                        ValueCardExample example = new ValueCardExample();
                        example.createCriteria().andCustomerIdEqualTo(req.getCustomerId())
                                .andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId())
                                .andIsDeleteEqualTo(false);
                        List<ValueCard> valueCardList = valueCardMapper.selectByExample(example);
                        if (CollectionUtils.isNotEmpty(valueCardList)){
                            valueCard = valueCardList.get(0);
                        }
                    }
                    if (null == valueCard){
                        throw new StoreSaasMarketingException("客户未开通储值卡");
                    }
                    //分配本金和赠送金扣减金额
                    BigDecimal principal = valueCard.getAmount();
                    BigDecimal changePrincipal = BigDecimal.ZERO;
                    BigDecimal present = valueCard.getPresentAmount();
                    BigDecimal changePresent = BigDecimal.ZERO;
                    if (req.getAmount().compareTo(principal) <= 0) {
                        changePrincipal = changePrincipal.subtract(req.getAmount());
                        principal = principal.subtract(req.getAmount());
                    } else{
                        changePresent = principal.subtract(req.getAmount());
                        present = present.add(changePresent);
                        changePrincipal = changePrincipal.subtract(principal);
                        principal = BigDecimal.ZERO;
                    }
                    if (present.compareTo(BigDecimal.ZERO) < 0){
                        throw new StoreSaasMarketingException("客户储值余额不足");
                    }
                    Date date = new Date();
                    //生成变更记录
                    ValueCardChange cardChange = new ValueCardChange();
                    cardChange.setCardId(valueCard.getId());
                    cardChange.setStoreId(valueCard.getStoreId());
                    cardChange.setTenantId(valueCard.getTenantId());
                    String codeNumber = "CZBG" + req.getStoreId() + codeFactory.getCodeNumberv2("CZBG", req.getStoreId());
                    cardChange.setChangeNo(codeNumber);
                    cardChange.setOrderId(req.getOrderId());
                    cardChange.setOrderNo(req.getOrderNo());
                    cardChange.setFinNo(req.getFinNo());
                    cardChange.setChangePrincipal(changePrincipal);
                    cardChange.setChangePresent(changePresent);
                    cardChange.setAmount(valueCard.getAmount().add(valueCard.getPresentAmount()).subtract(req.getAmount()));
                    cardChange.setChangeType(1);
                    cardChange.setStatus(true);
                    cardChange.setCreateTime(date);
                    cardChange.setUpdateTime(date);
                    result = valueCardChangeMapper.insertSelective(cardChange) > 0;
                    //更新账户余额
                    valueCard.setAmount(principal);
                    valueCard.setPresentAmount(present);
                    valueCard.setUpdateTime(date);
                    result = result && valueCardMapper.updateByPrimaryKeySelective(valueCard) > 0;
                }
            } finally {
                storeRedisUtils.releaseLock(key, value.toString());
            }
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public PageInfo<ValueCardChangeResp> rechargeRecord(ValueCardChangeRecordReq req) {
        log.info("查询客户充值or消费记录请求参数：{}",req);
        if (null == req.getStoreId() || null == req.getTenantId()){
            throw new StoreSaasMarketingException("参数校验失败");
        }
        PageInfo<ValueCardChangeResp> respPageInfo = new PageInfo<>();
        ValueCardExample example = new ValueCardExample();
        example.createCriteria().andCustomerIdEqualTo(req.getCustomerId())
                .andStoreIdEqualTo(req.getStoreId()).andTenantIdEqualTo(req.getTenantId())
                .andIsDeleteEqualTo(false);
        List<ValueCard> valueCardList = valueCardMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(valueCardList)){
            Long cardId = valueCardList.get(0).getId();
            PageHelper.startPage(req.getPageNum(),req.getPageSize());
            ValueCardChangeExample cardChangeExample = new ValueCardChangeExample();
            ValueCardChangeExample.Criteria criteria = cardChangeExample.createCriteria();
            criteria.andCardIdEqualTo(cardId).andStoreIdEqualTo(req.getStoreId())
                    .andTenantIdEqualTo(req.getTenantId()).andIsDeleteEqualTo(false);
            if (req.getType().equals(0)){ //充值/退款
                criteria.andChangeTypeIn(Arrays.asList(0,2));
            } else if (req.getType().equals(1)){    //消费
                criteria.andChangeTypeEqualTo(1);
            }
            cardChangeExample.setOrderByClause("update_time desc");
            List<ValueCardChange> cardChanges = valueCardChangeMapper.selectByExample(cardChangeExample);
            if (CollectionUtils.isNotEmpty(cardChanges)){
                PageInfo<ValueCardChange> pageInfo = new PageInfo<>(cardChanges);
                List<ValueCardChangeResp> respList = new ArrayList<>();
                for (ValueCardChange cardChange : pageInfo.getList()){
                    ValueCardChangeResp resp = new ValueCardChangeResp();
                    BeanUtils.copyProperties(cardChange,resp);
                    respList.add(resp);
                }
                respPageInfo.setList(respList);
                respPageInfo.setTotal(pageInfo.getTotal());
            }
        }
        return respPageInfo;
    }

    private  final  static  String confirmReceiptCacheKey = "ValueCardServiceImpl:confirmReceipt:finNoKey:";

    @Override
    @Transactional
    public Boolean confirmReceipt(ConfirmReceiptReq req) {
        log.info("储值变更单号确认收款 req-> {}", req);
        Boolean result = false;
        RedisUtils redisUtils = new RedisUtils(redisTemplate, "STORE-SAAS-MARKETING-");
        StoreRedisUtils storeRedisUtils = new StoreRedisUtils(redisUtils, redisTemplate);
        String key = confirmReceiptCacheKey.concat(req.getId().toString());
        Object value = storeRedisUtils.tryLock(key, 10, 10);
        if (null != value) {
            try {
                ValueCardChange valueCardChange = valueCardChangeMapper.selectByPrimaryKey(req.getId());
                if (valueCardChange == null) {
                    throw new StoreSaasMarketingException("查询不到变更单号");
                }
                if (!valueCardChange.getStoreId().equals(req.getStoreId()) || !valueCardChange.getTenantId().equals(req.getTenantId())) {
                    throw new StoreSaasMarketingException("数据存在越权");
                }
                long oilVaule = (valueCardChange.getChangePrincipal().multiply(new BigDecimal(100))).longValue();
                if (oilVaule != req.getAmount()) {
                    throw new StoreSaasMarketingException("收费金额匹配错误");
                }
                if (valueCardChange.getStatus()) {
                    throw new StoreSaasMarketingException("该流水已经生效");
                }
                ValueCard valueCard = valueCardMapper.selectByPrimaryKey(valueCardChange.getCardId());
                if (valueCard == null) {
                    throw new StoreSaasMarketingException("没有查询到客户储值卡总信息");
                }

                //退款 则判断客户的储值本金和赠金变更后是否会小于0
                if (valueCardChange.getChangeType().equals(0)
                        && (valueCardChange.getChangePrincipal().compareTo(valueCard.getAmount()) > 0
                        || valueCardChange.getChangePresent().compareTo(valueCard.getPresentAmount()) > 0)) {
                    throw new StoreSaasMarketingException("退款失败");
                }

                // 开始

                //变更客户卡的钱
                valueCard.setAmount(valueCardChange.getChangePrincipal().add(valueCard.getAmount()));
                valueCard.setPresentAmount(valueCardChange.getChangePresent().add(valueCard.getPresentAmount()));
                valueCard.setUpdateTime(new Date(System.currentTimeMillis()));
                //变更流水状态
                valueCardChange.setStatus(Boolean.TRUE);
                valueCardChange.setAmount(valueCard.getPresentAmount().add(valueCard.getAmount()));
                valueCardChange.setUpdateTime(new Date(System.currentTimeMillis()));
                if (valueCardMapper.updateByPrimaryKey(valueCard) <= 0 || valueCardChangeMapper.updateByPrimaryKey(valueCardChange) <= 0) {
                    throw new StoreSaasMarketingException("变更单确认收款失败");
                }
                result = Boolean.TRUE;
            } finally {
                storeRedisUtils.releaseLock(key, value.toString());
            }
        }else
        {
            throw new StoreSaasMarketingException("该单号正在进行确认收款");
        }
        return result;
    }
}
