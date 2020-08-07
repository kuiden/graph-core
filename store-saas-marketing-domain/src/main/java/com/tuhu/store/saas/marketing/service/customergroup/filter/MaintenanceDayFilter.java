package com.tuhu.store.saas.marketing.service.customergroup.filter;

import com.tuhu.store.saas.crm.vo.VehicleMaintenanceVo;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.service.customergroup.AbstractFactorFilter;
import com.tuhu.store.saas.marketing.util.SpringApplicationContextUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
        customerClient.getCustomerByVehicleMaintenance(vehicleMaintenanceVo);
        return null;
    }
}
