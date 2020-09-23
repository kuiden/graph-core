package com.tuhu.store.saas.marketing.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CustomerLastPurchaseRequest {
    @NotEmpty(message = "customerId不能为空")
    private List<String> customerIds;
    @NotNull(message = "storeId不能为空")
    @Min(value = 1L)
    private Long storeId;
    @Min(value = 1L)
    private Long tenantId;
}
