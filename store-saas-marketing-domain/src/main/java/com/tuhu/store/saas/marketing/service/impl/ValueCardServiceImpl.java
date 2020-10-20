package com.tuhu.store.saas.marketing.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.CustomerVO;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ValueCardChangeMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ValueCardMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ValueCardRuleMapper;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.request.valueCard.*;
import com.tuhu.store.saas.marketing.response.valueCard.CustomerValueCardDetailResp;
import com.tuhu.store.saas.marketing.response.valueCard.QueryValueCardListResp;
import com.tuhu.store.saas.marketing.response.valueCard.QueryValueCardRuleResp;
import com.tuhu.store.saas.marketing.response.valueCard.ValueCardChangeResp;
import com.tuhu.store.saas.marketing.service.IValueCardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
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
        QueryValueCardRuleResp resp = null;
        if (CollectionUtils.isNotEmpty(ruleList)){
            resp = new QueryValueCardRuleResp();
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

    @Override
    public Boolean customerRechargeOrRefund(ValueCardRechargeOrRefundReq req) {
        return null;
    }

    @Override
    public Boolean customerConsumption(ValueCardConsumptionReq req) {
        return null;
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
}
