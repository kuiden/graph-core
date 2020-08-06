package com.tuhu.store.saas.marketing.remote.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "添加车辆客户对象")
public class AddVehicleReq implements Serializable {
    private static final long serialVersionUID = 961563047701781769L;
    private VehicleReq vehicleReq;

    @NotNull(message = "customerReq不能为空")
    private CustomerReq customerReq;

    private String userId;

    private Long tenantId;

    private Long storeId;

    private Boolean isSearch = Boolean.FALSE;
}
