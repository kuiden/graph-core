package com.tuhu.store.saas.marketing.enums;

/**
 * <p>
 * 秒杀活动状态 0:未上架 1:上架 9:下架
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-03
 */
public enum SeckillActivityStatusEnum {
    WSJ(0, "未上架","未开始","未开始"),
    SJ(1, "上架","进行中","抢购中"),
    XJ(9, "下架","已结束","已结束");

    private Integer status;

    private String desc;

    private String statusName;

    private String clientStatusName;

    SeckillActivityStatusEnum(int status, String desc,String statusName,String clientStatusName) {
        this.status = status;
        this.desc = desc;
        this.statusName = statusName;
        this.clientStatusName = clientStatusName;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getDesc() {
        return desc;
    }

    public String getClientStatusName() {
        return clientStatusName;
    }
}
