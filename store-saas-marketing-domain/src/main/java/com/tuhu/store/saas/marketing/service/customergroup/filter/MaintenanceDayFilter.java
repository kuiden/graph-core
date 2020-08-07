package com.tuhu.store.saas.marketing.service.customergroup.filter;

import com.tuhu.store.saas.marketing.service.customergroup.AbstractFactorFilter;
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

    @Override
    public boolean isOpen() {
        return storeId!=null && dayStart!=null && dayEnd!=null;
    }

    @Override
    public List<String> filterSelf() {


        return null;
    }
}
