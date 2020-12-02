package com.tuhu.store.saas.marketing.sys;

import com.tuhu.store.saas.marketing.dataobject.SysReqLog;
import com.tuhu.store.saas.marketing.repository.SysReqLogWriteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author yangshengyong
 * @since 2020-11-18
 */
@Service
@Slf4j
public class SysReqLogService {
    @Autowired
    private SysReqLogWriteRepository sysReqLogWriteRepository;

    public void saveReqLog(SysReqLog log){
        log.setCreateTime(new Date());
        new Thread(() ->  sysReqLogWriteRepository.save(log)).start();
    }
}
