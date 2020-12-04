package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.base.mapper.BaseWriteMapper;
import com.tuhu.store.saas.marketing.dataobject.SeckillRegistrationRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 秒杀报名记录表  Mapper 接口
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
public interface SeckillRegistrationRecordMapper extends BaseWriteMapper<SeckillRegistrationRecord> {
    List<SeckillRegistrationRecord> pageBuyOrBrowseList(@Param("tenantId") Long tenantId, @Param("storeId") Long storeId, @Param("seckillActivityId") String seckillActivityId, @Param("phone") String phone);

    SeckillRegistrationRecord dataStatistics(@Param("tenantId") Long tenantId, @Param("storeId") Long storeId, @Param("seckillActivityId") String seckillActivityId, @Param("type") Integer type);
}
