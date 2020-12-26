package com.tuhu.store.saas.marketing.service;


import com.tuhu.store.saas.marketing.dataobject.ClientEventRecordDAO;
import com.tuhu.store.saas.marketing.request.ClientEventRecordRequest;
import com.tuhu.store.saas.user.dto.ClientEventRecordDTO;
import com.tuhu.store.saas.user.vo.ClientEventRecordVO;

import java.util.Date;
import java.util.Map;

/**
 * C端用户事件记录Service
 */
public interface IClientEventRecordService {

    /**
     * 新增用户事件记录
     *
     * @param clientEventRecordEntity
     * @return
     */
    Integer addNewClientEventRecord(ClientEventRecordDAO clientEventRecordEntity);

    /**
     * 记录C端用户事件
     *
     * @param clientEventRecordRequest
     */
    void recordEndUserEvent(ClientEventRecordRequest clientEventRecordRequest);

    /**
     * 更新事件计数
     *
     * @param id
     * @param date
     */
    void updateClientEventRecordCountById(String id);

    ClientEventRecordDAO getEventRecordByParams(String eventType, String contentType, String contentValue, String openId, Integer sourceType, String customerId, String phoneNumber);

    Map<String, ClientEventRecordDTO> getClientEventRecordStatisticsByEvent(ClientEventRecordVO clientEventRecordVO);
}
