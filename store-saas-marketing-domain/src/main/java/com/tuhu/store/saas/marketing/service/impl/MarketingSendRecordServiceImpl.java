package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tuhu.store.saas.marketing.dataobject.MarketingSendRecord;
import com.tuhu.store.saas.marketing.dataobject.MarketingSendRecordExample;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.MarketingSendRecordMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.MessageRemindMapper;
import com.tuhu.store.saas.marketing.service.IMarketingSendRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MarketingSendRecordServiceImpl implements IMarketingSendRecordService {
    @Autowired
    private MarketingSendRecordMapper marketingSendRecordMapper;

    @Autowired
    private MessageRemindMapper remindMapper;

    @Override
    public List<Integer> batchInsertMarketingSendRecord(List<MarketingSendRecord> records) {
        List<Integer> result = new ArrayList<>();
        try {
            log.info("BatchInsertMarketingSendRecord=>param=>".concat(JSON.toJSONString(records)));
            for (MarketingSendRecord record : records) {
                if (record != null) {
                    result.add(marketingSendRecordMapper.insert(record));
                }
            }
        } catch (Exception ex) {
            log.error("BatchInsertMarketingSendRecord=>error=>", ex);
            throw ex;
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMarketingSendRecord(String customerId,String marketingId,String sendType) {
        String funName = "更新定向营销发送记录状态";
        log.info("{} -> 请求参数: {}", funName, JSONObject.toJSONString(customerId+","+marketingId));
        MarketingSendRecord marketingSendRecord = this.queryMarketingSendRecord(customerId,marketingId);
        if (marketingSendRecord!=null){
            marketingSendRecord.setSendType(Byte.valueOf(sendType));
            marketingSendRecordMapper.updateByPrimaryKeySelective(marketingSendRecord);
        }

        log.info("更新定向营销发送记录状态完成");
    }

    @Override
    public List<MarketingSendRecord> listMarketingSendRecord(String marketingId, List<Byte> sendTypes) {
        log.info("根据营销id{}和发送状态{}查询发送记录",marketingId,sendTypes);
        MarketingSendRecordExample example = new MarketingSendRecordExample();
        MarketingSendRecordExample.Criteria criteria = example.createCriteria();
        criteria.andMarketingIdEqualTo(marketingId)
                .andSendTypeIn(sendTypes);
        return marketingSendRecordMapper.selectByExample(example);
    }

    @Override
    public Map<String, Long> getMarketingSendRecordCount(List<String> marketingIds) {

        log.info("根据营销ids{}查询发送记录", JSON.toJSONString(marketingIds));
        MarketingSendRecordExample example = new MarketingSendRecordExample();
        MarketingSendRecordExample.Criteria criteria = example.createCriteria();
        criteria.andMarketingIdIn(marketingIds);
        List<MarketingSendRecord> list = marketingSendRecordMapper.selectByExample(example);

        Map<String, Long> map = new HashMap<>();

        if(CollectionUtils.isEmpty(list)) {
            return map;
        }

        for(MarketingSendRecord marketingSendRecord : list) {
            if(map.get(marketingSendRecord.getMarketingId()) == null) {
                map.put(marketingSendRecord.getMarketingId() , 0L);
            }
            Long num = map.get(marketingSendRecord.getMarketingId());
            map.put(marketingSendRecord.getMarketingId(), num + 1);
        }

        return map;
    }

    /**
     * 根据客户ID和营销任务ID查询对应的发送记录信息
     *
     * @param customerId,marketingId
     * @return
     */
    private MarketingSendRecord queryMarketingSendRecord(String customerId,String marketingId) {
        MarketingSendRecordExample example = new MarketingSendRecordExample();
        MarketingSendRecordExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(customerId);
        criteria.andMarketingIdEqualTo(marketingId);
        List<MarketingSendRecord> list = marketingSendRecordMapper.selectByExample(example);
        if (list.size() >0) {
            return list.get(0);
        } else {
            return null;
        }
    }




}
