package com.tuhu.store.saas.marketing.service.activity.execute;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.dataobject.MessageTemplateLocal;
import com.tuhu.store.saas.marketing.enums.SMSTypeEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.request.CustomerAndVehicleReq;
import com.tuhu.store.saas.marketing.request.MarketingAddReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityModel;
import com.tuhu.store.saas.marketing.service.IMessageTemplateLocalService;
import com.tuhu.store.saas.marketing.service.activity.MarketingResult;
import com.tuhu.store.saas.marketing.service.activity.handler.AbstractMarketingHandler;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 *  营销秒杀活动
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-10
 */
@Component
@Slf4j
public class SecKillHandler extends AbstractMarketingHandler {
    @Autowired
    private SeckillActivityService seckillActivityService;
    @Autowired
    private IMessageTemplateLocalService messageTemplateLocalService;

    @Override
    public String getMarketingMethod() {
        return "2";
    }

    @Override
    public void execute(MarketingAddReq addReq, List<String> customerIds) {
        MarketingResult result = new MarketingResult();
        SeckillActivityModel secKill = seckillActivityService.getSeckillActivityModelById(addReq.getCouponOrActiveId(), Long.valueOf(addReq.getStoreId()));
        if (null == secKill || !addReq.getStoreId().equals(secKill.getStoreId())) {
            //禁止查询非本门店的营销活动
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "秒杀活动不存在或者不属于本店");
        }
        if (secKill.getStatus() == 9) {
            //活动下架了
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "请将秒杀活动上架");
        }
        if (addReq.getSendTime().after(secKill.getEndTime())) {
            //活动结束了
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "秒杀活动已结束，不能做营销");
        }
        if (addReq.getSendTime().before(DateUtils.now())) {
            //发送时间小于当前时间
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "发送时间小于当前时间");
        }
        if (addReq.getSendTime().before(secKill.getStartTime())) {
            //活动还没有开始
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "发送时间不能小于秒杀活动开始时间");
        }
        List<CustomerAndVehicleReq> customerList = getCustomerAndVehicleReqList(addReq, customerIds);
        result.setCustomerList(customerList);
        result.setSecKill(secKill);
        CustomerMarketing customerMarketing = this.buildCustomerMarketing(addReq, secKill);
        result.setCustomerMarketing(customerMarketing);
        this.handler(addReq, result);
    }

    private CustomerMarketing buildCustomerMarketing(MarketingAddReq addReq, SeckillActivityModel secKill) {
        CustomerMarketing customerMarketing = this.buildCustomerMarketing(addReq);
        //营销活动模板配置 https://www.yuntongxun.com/member/smsCount/getSmsConfigInfo，存入在message_template_local表
        //秒杀活动模板复用报名活动模板
        MessageTemplateLocal messageTemplateLocal = messageTemplateLocalService.getTemplateLocalById(SMSTypeEnum.MARKETING_ACTIVITY.templateCode(), addReq.getStoreId());
        if (messageTemplateLocal == null) {
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "不存在秒杀活动营销短信模板");
        }
        customerMarketing.setMessageTemplate(messageTemplateLocal.getTemplateName());
        //存的是本地的message模板，发送短信时需要单独查询
        customerMarketing.setMessageTemplateId(messageTemplateLocal.getId());
        customerMarketing.setCouponId(secKill.getId());
//            customerMarketing.setCouponCode(activity.getActivityCode());
        customerMarketing.setCouponTitle(secKill.getActivityTitle());
        return customerMarketing;
    }
}
