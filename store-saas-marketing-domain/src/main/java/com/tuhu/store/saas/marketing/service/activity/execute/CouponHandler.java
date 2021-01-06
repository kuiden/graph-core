package com.tuhu.store.saas.marketing.service.activity.execute;

import com.alibaba.fastjson.JSON;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.store.saas.marketing.dataobject.Coupon;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.dataobject.MessageTemplateLocal;
import com.tuhu.store.saas.marketing.enums.SMSTypeEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.CustomerAndVehicleReq;
import com.tuhu.store.saas.marketing.request.MarketingAddReq;
import com.tuhu.store.saas.marketing.response.CouponResp;
import com.tuhu.store.saas.marketing.service.ICouponService;
import com.tuhu.store.saas.marketing.service.IMessageTemplateLocalService;
import com.tuhu.store.saas.marketing.service.IUtilityService;
import com.tuhu.store.saas.marketing.service.activity.MarketingResult;
import com.tuhu.store.saas.marketing.service.activity.handler.AbstractMarketingHandler;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
/**
 * <p>
 *  营销优惠券活动
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-10
 */
@Component
@Slf4j
public class CouponHandler extends AbstractMarketingHandler {

    @Autowired
    private ICouponService couponService;

    @Autowired
    private IMessageTemplateLocalService messageTemplateLocalService;

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Autowired
    private IUtilityService iUtilityService;

    @Override
    public String getMarketingMethod() {
        return "0";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(MarketingAddReq addReq, List<String> customerIds) {
        log.info("CouponHandler{},{}", JSON.toJSONString(addReq), customerIds);
        MarketingResult result = new MarketingResult();
        Long couponId = Long.valueOf(addReq.getCouponOrActiveId());
        CouponResp coupon = couponService.getCouponDetailById(couponId);
        if (null == coupon || !addReq.getStoreId().equals(coupon.getStoreId())) {
            //禁止查询非本门店的优惠券
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "优惠券不存在或者不属于本店");
        }
        if (addReq.getSendTime().before(DateUtils.now())) {
            //发送时间小于当前时间
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "发送时间小于当前时间");
        }
        Long availableAccount = couponService.getCouponAvailableAccount(coupon.getId(), addReq.getStoreId());
        //根据任务中记录的发送对象信息查询出客户列表
        List<CustomerAndVehicleReq> customerList = getCustomerAndVehicleReqList(addReq, customerIds);
        //如果是限量优惠券，需要判断剩余额度
        if (availableAccount >= 0 && availableAccount < customerList.size()) {
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "优惠券数量不足");
        }
        result.setCoupon(coupon);
        result.setCustomerList(customerList);
        CustomerMarketing customerMarketing = this.buildCustomerMarketing(addReq, coupon);
        log.info("CouponHandler.customerMarketing{},{}", JSON.toJSONString(customerMarketing));
        result.setCustomerMarketing(customerMarketing);
        result.setMessageData(getMessageData(addReq));
        this.handler(addReq, result);
        //如果是优惠券定向营销，需要占用优惠券额度
        Coupon couponEntity = new Coupon();
        BeanUtils.copyProperties(coupon, couponEntity);
        couponService.setOccupyNum(couponEntity, customerList.size());
    }

    private CustomerMarketing buildCustomerMarketing(MarketingAddReq addReq, CouponResp coupon) {
        CustomerMarketing customerMarketing = this.buildCustomerMarketing(addReq);
        //营销活动模板配置 https://www.yuntongxun.com/member/smsCount/getSmsConfigInfo，存入在message_template_local表
        MessageTemplateLocal messageTemplateLocal = messageTemplateLocalService.getTemplateLocalById(SMSTypeEnum.MARKETING_COUPON.templateCode(), addReq.getStoreId());
        if (messageTemplateLocal == null) {
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "不存在优惠券营销短信模板");
        }
        customerMarketing.setMessageTemplate(messageTemplateLocal.getTemplateName());
        //存的是本地的message模板，发送短信时需要单独查询
        customerMarketing.setMessageTemplateId(messageTemplateLocal.getId());
        customerMarketing.setCouponId(coupon.getId().toString());
        customerMarketing.setCouponCode(coupon.getCode());
        customerMarketing.setCouponTitle(coupon.getTitle());
        return customerMarketing;
    }

    private String getMessageData(MarketingAddReq addReq){
        List<String> params = new ArrayList<>();
        //优惠券营销
        CouponResp coupon = couponService.getCouponDetailById(Long.valueOf(addReq.getCouponOrActiveId()));
        if (null == coupon || coupon.getId() == null) {
            //禁止查询非本门店的优惠券
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "优惠券不存在");
        }
        //查询门店信息
        StoreInfoVO storeInfoVO = new StoreInfoVO();
        storeInfoVO.setStoreId(addReq.getStoreId());
        StoreDTO storeDTO = storeInfoClient.getStoreInfo(storeInfoVO).getData();
        //短信模板占位符是从{1}开始，所以此处增加一个空串占位{0}
        //【云雀智修】车主您好,{1}优惠券,本店{2}已送到您的手机号,点击查看详情{3},退订回N
        params.add("价值" + coupon.getContentValue().intValue() + "元" + coupon.getTitle());
        params.add(storeDTO.getClientAppointPhone());
        //生成短连接
        if (StringUtils.isNotBlank(addReq.getOriginUrl())) {
            params.add(setALabel(iUtilityService.getShortUrl(addReq.getOriginUrl())));
        }
        return StringUtils.join(params, ",");
    }
}
