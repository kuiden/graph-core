package com.tuhu.store.saas.marketing.service.activity.execute;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.dataobject.MessageTemplateLocal;
import com.tuhu.store.saas.marketing.enums.SMSTypeEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.request.CustomerAndVehicleReq;
import com.tuhu.store.saas.marketing.request.MarketingAddReq;
import com.tuhu.store.saas.marketing.response.ActivityResponse;
import com.tuhu.store.saas.marketing.service.IActivityService;
import com.tuhu.store.saas.marketing.service.IMessageTemplateLocalService;
import com.tuhu.store.saas.marketing.service.activity.MarketingResult;
import com.tuhu.store.saas.marketing.service.activity.handler.AbstractMarketingHandler;
import com.tuhu.store.saas.marketing.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Override
    public String getMarketingMethod() {
        return "1";
    }

    public void execute(MarketingAddReq addReq, List<String> customerIds) {
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
        result.setCustomerMarketing(customerMarketing);
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
}
