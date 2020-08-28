package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.base.mapper.BaseWriteMapper;
import com.tuhu.store.saas.marketing.entity.EndUserVisitedCouponEntity;
import org.springframework.data.repository.query.Param;

/**
 * <p>
 * 车主端用户访问的优惠券记录 Mapper 接口
 * </p>
 *
 * @author someone
 * @since 2020-08-03
 */
public interface EndUserVisitedCouponWriteMapper extends BaseWriteMapper<EndUserVisitedCouponEntity> {

    EndUserVisitedCouponEntity findFirstByOpenIdAndCouponCode(@Param("openId") String openId, @Param("couponCode") String couponCode);

}
