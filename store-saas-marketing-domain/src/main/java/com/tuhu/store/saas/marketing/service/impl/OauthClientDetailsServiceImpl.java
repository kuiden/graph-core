package com.tuhu.store.saas.marketing.service.impl;

import com.tuhu.base.service.impl.BaseServiceImpl;
import com.tuhu.store.saas.marketing.dataobject.OauthClientDetailsDAO;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.OauthClientDetailsWriteMapper;
import com.tuhu.store.saas.marketing.service.IOauthClientDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class OauthClientDetailsServiceImpl extends BaseServiceImpl<OauthClientDetailsWriteMapper, OauthClientDetailsDAO> implements IOauthClientDetailsService {

//    @Autowired
//    private OauthClientDetailsWriteMapper oauthClientDetailsWriteMapper;

    @Override
    public OauthClientDetailsDAO getClientDetailByClientId(String clientId) {
        return baseMapper.selectById(clientId);
    }
}
