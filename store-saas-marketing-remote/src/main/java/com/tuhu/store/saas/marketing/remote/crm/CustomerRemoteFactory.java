package com.tuhu.store.saas.marketing.remote.crm;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdReqVO;
import com.tuhu.store.saas.crm.vo.BaseIdsReqVO;
import com.tuhu.store.saas.crm.vo.CustomerSearchVO;
import com.tuhu.store.saas.crm.vo.CustomerVO;
import com.tuhu.store.saas.crm.vo.VehicleMaintenanceVo;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

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

            @Override
            public BizBaseResponse<List<CustomerDTO>> getCustomerByQuery(CustomerVO customerVO) {
                log.error("getCustomerByQuery error,request={},error={}", customerVO, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<List<CustomerDTO>> getCustomerByVehicleMaintenance(VehicleMaintenanceVo vehicleMaintenanceVo) {
                log.error("getCustomerByVehicleMaintenance error,request={},error={}", vehicleMaintenanceVo, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<List<CustomerDTO>> getCustomerListByIdList(BaseIdsReqVO baseIdsReqVO) {
                log.error("getCustomerListByIdList error,request={},error={}", baseIdsReqVO, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<List<CustomerDTO>> getCustomerListByPhoneOrName(CustomerSearchVO customerSearchVO) {
                log.error("getCustomerListByPhoneOrName error,request={},error={}", customerSearchVO, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

        };
    }
}
