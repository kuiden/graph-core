package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.dataobject.MarketingSendRecord;

import java.util.List;
import java.util.Map;

public interface IMarketingSendRecordService {
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

    /**
     * 根据营销id和发送状态列表查询营销发送记录
     * @param marketingId
     * @return
     */
    public List<MarketingSendRecord> listMarketingSendRecord(String marketingId,List<Byte> sendTypes);

    /**
     * 根据营销id和发送状态列表查询营销发送记录
     * @return
     */
    public Map<String,Long> getMarketingSendRecordCount(List<String> marketingIds);
}
