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
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityModel;
import com.tuhu.store.saas.marketing.service.IMessageTemplateLocalService;
import com.tuhu.store.saas.marketing.service.IUtilityService;
import com.tuhu.store.saas.marketing.service.activity.MarketingResult;
import com.tuhu.store.saas.marketing.service.activity.handler.AbstractMarketingHandler;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Autowired
    private IUtilityService iUtilityService;

    @Override
    public String getMarketingMethod() {
        return "2";
    }

    @Override
    public void execute(MarketingAddReq addReq, List<String> customerIds) {
        log.info("SecKillHandler{},{}", JSON.toJSONString(addReq), customerIds);
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
//        if (addReq.getSendTime().before(secKill.getStartTime())) {
//            //活动还没有开始
//            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "发送时间不能小于秒杀活动开始时间");
//        }
        List<CustomerAndVehicleReq> customerList = getCustomerAndVehicleReqList(addReq, customerIds);
        result.setCustomerList(customerList);
        result.setSecKill(secKill);
        result.setMessageData(getMessageData(addReq));
        CustomerMarketing customerMarketing = this.buildCustomerMarketing(addReq, secKill);
        log.info("SecKillHandler.customerMarketing{},{}", JSON.toJSONString(customerMarketing));
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

    private String getMessageData(MarketingAddReq addReq){
        List<String> params = new ArrayList<>();
        //秒杀活动营销
        SeckillActivityModel seckillActivityModel = seckillActivityService.getSeckillActivityModelById(addReq.getCouponOrActiveId(),addReq.getStoreId());
        if (null == seckillActivityModel) {
            //禁止查询非本门店的秒杀活动
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED,"活动不存在");
        }
        //查询门店信息
        StoreInfoVO storeInfoVO = new StoreInfoVO();
        storeInfoVO.setStoreId(addReq.getStoreId());
        StoreDTO storeDTO = storeInfoClient.getStoreInfo(storeInfoVO).getData();
        BigDecimal activityPrice = seckillActivityModel.getNewPrice() != null ? seckillActivityModel.getNewPrice():new BigDecimal("0");
        BigDecimal srcPrice = seckillActivityModel.getOriginalPrice() != null ? seckillActivityModel.getOriginalPrice():new BigDecimal("0");
        //短信模板占位符是从{1}开始，所以此处增加一个空串占位{0}
        //【云雀智修】车主您好，{1}，本店{2}邀请您参加{3}活动，点击查看详情：{4},退订回N
        params.add(activityPrice.setScale(2, BigDecimal.ROUND_DOWN).toString()+"抵"+srcPrice.setScale(2, BigDecimal.ROUND_DOWN).toString());
        params.add(storeDTO.getClientAppointPhone());
        params.add(seckillActivityModel.getActivityTitle());
        //生成短连接
        if (StringUtils.isNotBlank(addReq.getOriginUrl())) {
            params.add(setALabel(iUtilityService.getShortUrl(addReq.getOriginUrl())));
        }
        return StringUtils.join(params, ",");
    }
}
