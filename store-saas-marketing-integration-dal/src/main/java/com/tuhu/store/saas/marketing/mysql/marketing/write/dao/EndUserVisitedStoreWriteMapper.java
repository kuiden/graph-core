package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.base.mapper.BaseWriteMapper;
import com.tuhu.store.saas.marketing.entity.EndUserVisitedStoreEntity;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 车主端用户访问的门店记录 Mapper 接口
 * </p>
 *
 * @author someone
 * @since 2020-08-03
 */
public interface EndUserVisitedStoreWriteMapper extends BaseWriteMapper<EndUserVisitedStoreEntity> {

    EndUserVisitedStoreEntity findFirstByOpenIdAndStoreId(@Param("openId") String openId, @Param("storeId") String storeId);

    List<EndUserVisitedStoreEntity> findAllByOpenId(@Param("openId") String openId);

    Integer updateVisitedTimeById(@Param("id") String id, @Param("newDate") Date newDate);

}
