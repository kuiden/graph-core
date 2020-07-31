package com.tuhu.store.saas.marketing.response.dto;


import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * 优惠券排序比较器
 * <br>优先按照优惠力度排序，优惠大的在前，然后按照优惠券使用结束时间排序，越快过期的越靠前
 */
public class CustomerCouponDTOComparator implements Comparator<CustomerCouponDTO> {
    @Override
    public int compare(CustomerCouponDTO o1, CustomerCouponDTO o2) {
        BigDecimal discountAmountOne = getCouponDiscountAmount(o1);
        BigDecimal discountAmountTwo = getCouponDiscountAmount(o2);
        int discountAmountCompareResult = discountAmountTwo.compareTo(discountAmountOne);
        if (discountAmountCompareResult == 0) {
            return o1.getUseEndTime().compareTo(o2.getUseEndTime());
        } else {
            return discountAmountCompareResult;
        }
    }

    /**
     * 获取优惠券的优惠金额,代金券为优惠金额，折扣券为折扣金额
     *
     * @param customerCouponDTO
     * @return
     */
    private BigDecimal getCouponDiscountAmount(CustomerCouponDTO customerCouponDTO) {
        CouponDTO couponDTO = customerCouponDTO.getCoupon();
        Integer type = couponDTO.getType();
        //代金券
        if (Integer.valueOf(0).equals(type)) {
            return null == customerCouponDTO.getCouponDiscountAmount() ? BigDecimal.ZERO : customerCouponDTO.getCouponDiscountAmount();
        } else {
            //计算服务项目或商品的总金额
            BigDecimal amount = BigDecimal.ZERO;
            List<ServiceOrderItemDTO> itemDTOS = customerCouponDTO.getItems();
            if (CollectionUtils.isEmpty(itemDTOS)) {
                //折扣券没有工单item时，优惠金额为0
                return BigDecimal.ZERO;
            } else {
                for (ServiceOrderItemDTO itemDTO : itemDTOS) {
                    amount.add(new BigDecimal(itemDTO.getAmount()));
                }
                //折扣券
                BigDecimal discountValue = couponDTO.getDiscountValue();
                discountValue = BigDecimal.ONE.subtract(discountValue.divide(new BigDecimal(100)));
                //门槛
                BigDecimal conditionLimit = couponDTO.getConditionLimit();
                //未到达使用门槛，不可用,优惠按照门槛计算
                if (conditionLimit.compareTo(amount) > 0) {
                    amount = conditionLimit;
                }
                return amount.multiply(discountValue);
            }
        }
    }
}
