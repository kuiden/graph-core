package com.tuhu.store.saas.marketing.service;

import com.tuhu.base.service.IBaseQueryService;
import com.tuhu.base.service.IBaseService;
import com.tuhu.base.service.ICrudService;
import com.tuhu.store.saas.marketing.dataobject.OauthClientDetailsDAO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author someone
 * @since 2020-08-03
 */
public interface IOauthClientDetailsService extends IBaseQueryService<OauthClientDetailsDAO>, IBaseService<OauthClientDetailsDAO>, ICrudService<OauthClientDetailsDAO> {

    /**
     * 查找客户端
     *
     * @param clientId
     * @return
     */
    OauthClientDetailsDAO getClientDetailByClientId(String clientId);

}
