package com.tuhu.store.saas.marketing.service.impl;

import com.tuhu.store.saas.marketing.dataobject.EndUser;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.EndUserWriteMapper;
import com.tuhu.store.saas.marketing.service.IEndUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EndUserServiceImpl implements IEndUserService {

    @Autowired
    private EndUserWriteMapper endUserWriteMapper;

    @Override
    public List<EndUser> findByCustomerId(String customerId) {
        return endUserWriteMapper.findByCustomerId(customerId);
    }

}
