package com.tuhu.store.saas.marketing.service.activity.execute;

import com.alibaba.fastjson.JSON;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.dataobject.MessageTemplateLocal;
import com.tuhu.store.saas.marketing.enums.SMSTypeEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.CustomerAndVehicleReq;
import com.tuhu.store.saas.marketing.request.MarketingAddReq;
import com.tuhu.store.saas.marketing.response.ActivityItemResp;
import com.tuhu.store.saas.marketing.response.ActivityResp;
import com.tuhu.store.saas.marketing.response.ActivityResponse;
import com.tuhu.store.saas.marketing.service.IActivityService;
import com.tuhu.store.saas.marketing.service.IMessageTemplateLocalService;
import com.tuhu.store.saas.marketing.service.IUtilityService;
import com.tuhu.store.saas.marketing.service.activity.MarketingResult;
import com.tuhu.store.saas.marketing.service.activity.handler.AbstractMarketingHandler;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 *  营销活动
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-10
 */
@Component
@Slf4j
public class ActivityHandler extends AbstractMarketingHandler {

    @Autowired
    private IActivityService activityService;

    @Autowired
    private IMessageTemplateLocalService messageTemplateLocalService;

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Autowired
    private IUtilityService iUtilityService;

    @Override
    public String getMarketingMethod() {
        return "1";
    }

    public void execute(MarketingAddReq addReq, List<String> customerIds) {
        log.info("ActivityHandler{},{}", JSON.toJSONString(addReq), customerIds);
        MarketingResult result = new MarketingResult();
        Long activityId = Long.valueOf(addReq.getCouponOrActiveId());
        ActivityResponse activity = activityService.getActivityById(activityId, Long.valueOf(addReq.getStoreId()));
        if (null == activity || !addReq.getStoreId().equals(activity.getStoreId())) {
            //禁止查询非本门店的营销活动
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "活动不存在或者不属于本店");
        }
        if (!activity.getStatus()) {
            //活动下架了
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "请将活动上架");
        }
        if (addReq.getSendTime().after(activity.getEndTime())) {
            //活动结束了
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "活动已结束，不能做营销");
        }
        if (addReq.getSendTime().before(DateUtils.now())) {
            //发送时间小于当前时间
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "发送时间小于当前时间");
        }
        if (addReq.getSendTime().before(activity.getStartTime())) {
            //活动还没有开始
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "发送时间不能小于活动开始时间");
        }
        List<CustomerAndVehicleReq> customerList = getCustomerAndVehicleReqList(addReq, customerIds);
        result.setActivity(activity);
        result.setCustomerList(customerList);
        CustomerMarketing customerMarketing = this.buildCustomerMarketing(addReq, activity);
        log.info("ActivityHandler.customerMarketing{},{}", JSON.toJSONString(customerMarketing));
        result.setCustomerMarketing(customerMarketing);
        result.setMessageData(getMessageData(addReq));
        this.handler(addReq, result);
    }

    private CustomerMarketing buildCustomerMarketing(MarketingAddReq addReq, ActivityResponse activity) {
        CustomerMarketing customerMarketing = this.buildCustomerMarketing(addReq);
        //营销活动模板配置 https://www.yuntongxun.com/member/smsCount/getSmsConfigInfo，存入在message_template_local表
        MessageTemplateLocal messageTemplateLocal = messageTemplateLocalService.getTemplateLocalById(SMSTypeEnum.MARKETING_ACTIVITY.templateCode(), addReq.getStoreId());
        if (messageTemplateLocal == null) {
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "不存在活动营销短信模板");
        }
        customerMarketing.setMessageTemplate(messageTemplateLocal.getTemplateName());
        //存的是本地的message模板，发送短信时需要单独查询
        customerMarketing.setMessageTemplateId(messageTemplateLocal.getId());
        customerMarketing.setCouponId(activity.getId().toString());
        customerMarketing.setCouponCode(activity.getActivityCode());
        customerMarketing.setCouponTitle(activity.getActivityTitle());
        return customerMarketing;
    }

    private String getMessageData(MarketingAddReq addReq){
        List<String> params = new ArrayList<>();
        //活动营销
        ActivityResp activityResp = activityService.getActivityDetailById(Long.valueOf(addReq.getCouponOrActiveId()), addReq.getStoreId());
        if (null == activityResp) {
            //禁止查询非本门店的营销活动
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "活动不存在");
        }
        //算出活动价和原价
        BigDecimal activityPrice = activityResp.getActivityPrice().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal srcPrice = new BigDecimal(0);
        List<ActivityItemResp> activityItemResps = activityResp.getItems();
        for (ActivityItemResp activityItemResp : activityItemResps) {
            BigDecimal itemSiglePrice = BigDecimal.valueOf(activityItemResp.getOriginalPrice()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal exeTime = BigDecimal.valueOf(activityItemResp.getItemQuantity());
            srcPrice = srcPrice.add(itemSiglePrice.multiply(exeTime));
        }
        //查询门店信息
        StoreInfoVO storeInfoVO = new StoreInfoVO();
        storeInfoVO.setStoreId(addReq.getStoreId());
        StoreDTO storeDTO = storeInfoClient.getStoreInfo(storeInfoVO).getData();
        //短信模板占位符是从{1}开始，所以此处增加一个空串占位{0}
        params.add(activityPrice.toString() + "抵" + srcPrice.toString());
        params.add(storeDTO.getClientAppointPhone());
        params.add(activityResp.getActivityTitle());
        //生成短连接
        if (StringUtils.isNotBlank(addReq.getOriginUrl())) {
            params.add(setALabel(iUtilityService.getShortUrl(addReq.getOriginUrl())));
        }
        return StringUtils.join(params, ",");
    }
}
