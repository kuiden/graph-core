package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.springcloud.common.util.RedisUtils;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdReqVO;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;

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
        valueCardRule.setAmount(BigDecimal.ZERO);
        valueCardRule.setPresentAmount(BigDecimal.ZERO);
        if (CollectionUtils.isNotEmpty(req.getRuleList())){
            //排序
            req.getRuleList().sort(new Comparator<ValueCardRuleReq>() {
                @Override
                public int compare(ValueCardRuleReq o1, ValueCardRuleReq o2) {
                    return o2.getAmount().compareTo(o1.getAmount());
                }
            });
            for (ValueCardRuleReq ruleReq : req.getRuleList()){
                valueCardRule.setId(null);
                valueCardRule.setAmount(ruleReq.getAmount());
                valueCardRule.setPresentAmount(ruleReq.getAmount());
                valueCardRuleMapper.insertSelective(valueCardRule);
            }
        } else {
            valueCardRuleMapper.insertSelective(valueCardRule);
        }
        return req;
    }

    @Override
    public QueryValueCardRuleResp queryValueCardRule(Long storeId, Long tenantId) {
        return null;
    }

    @Override
    public Map<String, BigDecimal> queryTotalValue(Long storeId, Long tenantId) {
        return null;
    }

    @Override
    public PageInfo<QueryValueCardListResp> queryDetailList(QueryValueCardListReq req) {
        return null;
    }

    @Override
    public CustomerValueCardDetailResp customerValueCardDetail(CustomerValueCardDetailReq req) {
        return null;
    }

    @Override
    public PageInfo<ValueCardChangeResp> customerValueCardChangeList(CustomerValueCardDetailReq req) {
        return null;
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CodeFactory codeFactory;

    @Autowired
    private CustomerClient customerClient ;

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
        if (model.getAmount().add(req.getChangePresent()).compareTo(BigDecimal.ZERO) == Integer.valueOf(-1)) {
            throw new StoreSaasMarketingException("当前本金不足,无法退款");
        }
        //客户退款钱不够的情况 当赠送金-当前要扣除的赠送金 <0的话 证明不够扣的
        if (model.getPresentAmount().add(req.getChangePrincipal()).compareTo(BigDecimal.ZERO) == Integer.valueOf(-1)) {
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
        Object value = storeRedisUtils.tryLock(settlementCacheKey.concat(req.getCustomerId()), 1000, 1000);
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
                    if (hasPayProcess) {
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
                                result = addResultObject.getData();
                            }
                        } else if (valueCardChange.getChangeType() == Integer.valueOf(0)) {
                            //生成一条待付单

                            //通知finance添加待付
                            AddNonpaymentVO addNonpaymentVO = createAddNonpaymentVO(valueCardChange, valueCard.getCustomerId(), customerDTO.getName(), customerDTO.getPhoneNumber());
                            result = storeReceivingClient.addNonpaymentForValueCard(addNonpaymentVO).getData();
                        }
                        if (StringUtils.isBlank(result)) {
                            throw new StoreSaasMarketingException("创建变更单失败");
                        }
                        //添加一个变更单号
                        valueCardChange.setFinNo(result);
                        valueCardMapper.updateByPrimaryKey(valueCard);
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

    @Override
    public Boolean customerConsumption(ValueCardConsumptionReq req) {
        return null;
    }
}
