package com.tuhu.store.saas.marketing.service.customergroup.filter;

import com.google.common.collect.Lists;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.VehicleMaintenanceVo;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.service.customergroup.AbstractFactorFilter;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.marketing.util.SpringApplicationContextUtil;
import com.tuhu.store.saas.order.response.serviceorder.ListCustomerInfoResp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 保养日期离今天XX天--XX天的
 */
@Data
@Slf4j
public class MaintenanceDayFilter extends AbstractFactorFilter {

    private Integer dayStart;

    private Integer dayEnd;

    private Long storeId;

    private Long tenantId;

    @Override
    public boolean isOpen() {
        return storeId!=null && dayStart!=null && dayEnd!=null;
    }

    @Override
    public List<String> filterSelf() {
        CustomerClient customerClient = SpringApplicationContextUtil.getBean(CustomerClient.class);
        VehicleMaintenanceVo vehicleMaintenanceVo = new VehicleMaintenanceVo();
        vehicleMaintenanceVo.setTenantId(tenantId);
        vehicleMaintenanceVo.setStoreId(storeId);
        vehicleMaintenanceVo.setNextMaintenanceDateStart(geStartDateAfterAdd(dayStart));
        vehicleMaintenanceVo.setNextMaintenanceDateEnd(geEndDateAfterAdd(dayEnd));
        List<CustomerDTO> CustomerDTOList = customerClient.getCustomerByVehicleMaintenance(vehicleMaintenanceVo).getData();
        List<String> customerIdList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(CustomerDTOList)){
            for(CustomerDTO customerDto : CustomerDTOList){
                if(StringUtils.isNotBlank(customerDto.getId()) && !customerIdList.contains(customerDto.getId())){
                    customerIdList.add(customerDto.getId());
                }
            }
        }
        return customerIdList;
    }

    private Date geStartDateAfterAdd(Integer dayLength){
        Date dayBegain = DateUtils.getDayBegin();
        return DateUtils.getNextDay(dayBegain,dayLength-1);
    }

    private Date geEndDateAfterAdd(Integer dayLength){
        Date dayEnd = DateUtils.getDayEnd();
        return DateUtils.getNextDay(dayEnd,dayLength-1);
    }
}
