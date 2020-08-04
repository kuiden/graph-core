package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.dataobject.MarketingSendRecord;

import java.util.List;

public interface IMarketingSendRecordService {
    /**
     * 根据营销ID 获取营销发送记录
     * @param marketingId
     * @return
     */
    public List<MarketingSendRecord> getMarketingSendRecord(String marketingId, String phone, String marketingMethod);

    /**
     * 批量插入营销发送记录
     * @param records
     * @return
     */
    List<Integer> batchInsertMarketingSendRecord(List<MarketingSendRecord> records);
    /**
     * 更新营销发送记录状态
     * @param customerId
     * @param marketingId
     * @param sendType
     */
    public void updateMarketingSendRecord(String customerId, String marketingId, String sendType);
}
