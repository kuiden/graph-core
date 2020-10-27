package com.tuhu.store.saas.marketing.request.valueCard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wangyuqing
 * @since 2020/10/19 10:25
 */
@Data
public class QueryValueCardListReq {

    @ApiModelProperty("排序方式 0最后操作 1从高到低 2从低到高")
    private Integer sortType;

    @ApiModelProperty("搜索条件：客户姓名或手机号")
    private String search;

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("页量")
    private Integer pageSize;

    private Long storeId;

    private Long tenantId;

}
