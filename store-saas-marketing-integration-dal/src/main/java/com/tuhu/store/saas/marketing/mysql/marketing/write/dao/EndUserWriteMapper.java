package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.base.mapper.BaseWriteMapper;
import com.tuhu.store.saas.marketing.dataobject.EndUser;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <p>
 * 车主信息 Mapper 接口
 * </p>
 *
 * @author someone
 * @since 2020-08-03
 */
public interface EndUserWriteMapper extends BaseWriteMapper<EndUser> {

    List<EndUser> findByCustomerId(@Param("customerId") String customerId);

}
