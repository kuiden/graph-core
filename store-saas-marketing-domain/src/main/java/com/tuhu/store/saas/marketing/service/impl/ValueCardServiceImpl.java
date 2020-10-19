package com.tuhu.store.saas.marketing.service.impl;

import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.dataobject.ValueCardRule;
import com.tuhu.store.saas.marketing.dataobject.ValueCardRuleExample;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ValueCardChangeMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ValueCardMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ValueCardRuleMapper;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Override
    public Boolean customerRechargeOrRefund(ValueCardRechargeOrRefundReq req) {
        return null;
    }

    @Override
    public Boolean customerConsumption(ValueCardConsumptionReq req) {
        return null;
    }
}
