package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.base.mapper.BaseWriteMapper;
import com.tuhu.store.saas.marketing.dataobject.ClientEventRecordDAO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 门店事件记录表 Mapper 接口
 * </p>
 *
 * @author kudeng
 * @since 2020-08-05
 */
public interface ClientEventRecordMapper extends BaseWriteMapper<ClientEventRecordDAO> {

    List<Map<String, Object>> getClientEventRecordStatisticsByEvent(Map<String, Object> paramMap);

}
