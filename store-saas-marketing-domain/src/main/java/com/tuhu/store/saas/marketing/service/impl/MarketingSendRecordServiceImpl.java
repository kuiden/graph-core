package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tuhu.store.saas.marketing.dataobject.MarketingSendRecord;
import com.tuhu.store.saas.marketing.dataobject.MarketingSendRecordExample;
import com.tuhu.store.saas.marketing.dataobject.MessageRemind;
import com.tuhu.store.saas.marketing.dataobject.MessageRemindExample;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.MarketingSendRecordMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.MessageRemindMapper;
import com.tuhu.store.saas.marketing.service.IMarketingSendRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MarketingSendRecordServiceImpl implements IMarketingSendRecordService {
    @Autowired
    private MarketingSendRecordMapper marketingSendRecordMapper;

    @Autowired
    private MessageRemindMapper remindMapper;

    @Override
    public List<MarketingSendRecord> getMarketingSendRecord(String marketingId, String phone, String marketingMethod) {
        List<MarketingSendRecord> result;
        try {
            MarketingSendRecordExample example = new MarketingSendRecordExample();
            MarketingSendRecordExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotEmpty(marketingId)) {
                criteria.andMarketingIdEqualTo(marketingId);
            }
            if (StringUtils.isNotEmpty(phone)) {
                criteria.andPhoneNumberLike("%".concat(phone).concat("%"));
            }
            result = marketingSendRecordMapper.selectByExample(example);

            //根据营销ID和客户ID查询短信是否发送成功
            if(("1").equals(marketingMethod)){
                List<MarketingSendRecord> msrList = new ArrayList();
                for (MarketingSendRecord record : result){
                    MessageRemindExample remindExample = new MessageRemindExample();
                    MessageRemindExample.Criteria remindCriteria = remindExample.createCriteria();
                    remindCriteria.andSourceIdEqualTo(marketingId);
                    remindCriteria.andCustomerIdEqualTo(record.getCustomerId());
                    byte sendType = record.getSendType();
                    List<MessageRemind> remindList = remindMapper.selectByExample(remindExample);
                    if (remindList.size()>0){
                        MessageRemind messageRemind = remindList.get(0);
                        String status = messageRemind.getStatus();
                        if(status.equals("message_success")){
                            sendType = 1;
                        }else if(status.equals("message_wait")&&messageRemind.getTryTime().equals(3)){
                            sendType = 2;
                        }
                        this.updateMarketingSendRecord(record.getCustomerId(),marketingId,sendType+"");
                    }

                    record.setSendType(sendType);
                    msrList.add(record);
                }
                result = msrList;
            }

        } catch (Exception ex) {
            log.error("getMarketingSendRecord=>error=>", ex);
            throw ex;
        }
        return result;
    }

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
