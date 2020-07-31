package com.tuhu.store.saas.marketing.remote;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class BaseUser {
    private Long accountId;
    private String password;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("登录账号")
    private String account;

    @ApiModelProperty("组织id,途虎运营默认为0")
    private Long orgId;

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("企业id")
    private Long tenantId;

    @ApiModelProperty("企业名称")
    private String tenantName;

    @ApiModelProperty("用户名称")
    private String username;

    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;

}
