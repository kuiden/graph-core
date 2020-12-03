package com.tuhu.store.saas.marketing.repository;

import com.tuhu.base.repository.BaseRepository;
import com.tuhu.store.saas.marketing.dataobject.SysReqLog;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SysReqLogWriteMapper;
import org.springframework.stereotype.Repository;

/**
 * @author yangshengyong
 * @since 2020-11-18
 */
@Repository
public class SysReqLogWriteRepository extends BaseRepository<SysReqLogWriteMapper, SysReqLog> {

}
