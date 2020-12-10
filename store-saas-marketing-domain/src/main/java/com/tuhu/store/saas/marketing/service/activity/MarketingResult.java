package com.tuhu.store.saas.marketing.service.activity;

import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.request.CustomerAndVehicleReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityModel;
import com.tuhu.store.saas.marketing.response.ActivityResponse;
import com.tuhu.store.saas.marketing.response.CouponResp;
import lombok.Data;

import java.util.List;

/**
 * <p>
 *  营销活动
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-10
 */
@Data
public class MarketingResult {
    private CustomerMarketing customerMarketing;
    private CouponResp coupon;
    private ActivityResponse activity;
    private SeckillActivityModel secKill;
    private List<CustomerAndVehicleReq> customerList;
}
