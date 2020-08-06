package com.tuhu.store.saas.marketing.remote.crm;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdReqVO;
import com.tuhu.store.saas.crm.vo.BaseIdsReqVO;
import com.tuhu.store.saas.crm.vo.CustomerVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CustomerRemoteFactory implements FallbackFactory<CustomerClient> {
    @Override
    public CustomerClient create(Throwable throwable) {
        return new CustomerClient() {

            @Override
            public BizBaseResponse<List<CustomerDTO>> getCustomer(CustomerVO customerVO) {
                log.error("getCustomer error,request={},error={}", customerVO, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<CustomerDTO> getCustomerById(BaseIdReqVO baseIdReqVO) {
                log.error("getCustomerById error,request={},error={}", baseIdReqVO, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<List<CustomerDTO>> getCustomerByIds(BaseIdsReqVO baseIdsReqVO) {
                log.error("getCustomerByIds error,request={},error={}", baseIdsReqVO, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<List<CustomerDTO>> listCustomer(CustomerVO customerVO) {
                log.error("listCustomer error,request={},error={}", customerVO, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

        };
    }
}
