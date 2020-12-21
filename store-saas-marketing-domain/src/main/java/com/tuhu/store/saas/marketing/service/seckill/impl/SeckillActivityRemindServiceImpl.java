package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.dataobject.OauthClientDetailsDAO;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivity;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivityRemind;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillActivityRemindMapper;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.seckill.SeckillRemindAddReq;
import com.tuhu.store.saas.marketing.service.IOauthClientDetailsService;
import com.tuhu.store.saas.marketing.service.IWechatService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityRemindService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * <p>
 * 秒杀活动开抢提醒 服务实现类
 * </p>
 *
 * @author wangyuqing
 * @since 2020-12-08
 */
@Service
@Slf4j
public class SeckillActivityRemindServiceImpl extends ServiceImpl<SeckillActivityRemindMapper, SeckillActivityRemind> implements SeckillActivityRemindService {

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Autowired
    private SeckillActivityService seckillActivityService;

    @Autowired
    private Executor taskExecutor;

    @Autowired
    private IWechatService iWechatService;

    @Autowired
    private IOauthClientDetailsService iOauthClientDetailsService;

    @Value("${wechat.seckill.miniprogram.message.client.type}")
    private String seckillClientType;

    @Value("${wechat.seckill.miniprogram.message.template.id}")
    private String seckillTemplateId;

    @Override
    public void customerActivityRemindAdd(SeckillRemindAddReq req) {
        log.info("customerActivityRemindAdd -> req:{}",req);
        if (null == req.getTemplateId()){
            req.setTemplateId(this.seckillTemplateId);
        }
        if (StringUtils.isBlank(req.getOpenId())){
            //获取openId
            String clientType = req.getClientType() == null ? this.seckillClientType : req.getClientType();
            OauthClientDetailsDAO oauthClientDetails = iOauthClientDetailsService.getClientDetailByClientId(clientType);
            String openId = iWechatService.getOpenId(oauthClientDetails.getWxAppid(),
                    oauthClientDetails.getWxSecret(), req.getOpenIdCode(), oauthClientDetails.getWxOpenidUrl());
            req.setOpenId(openId);
        }
        //查询是否已添加过提醒
        List<SeckillActivityRemind> remindList = this.baseMapper.selectList(new EntityWrapper<SeckillActivityRemind>()
                .eq("open_id", req.getOpenId())
                .eq("seckill_activity_id", req.getSeckillActivityId())
                .eq("is_delete", 0));
        if (CollectionUtils.isEmpty(remindList)) {
            //添加
            SeckillActivityRemind seckillActivityRemind = new SeckillActivityRemind();
            BeanUtils.copyProperties(req, seckillActivityRemind);
            seckillActivityRemind.setCreateTime(new Date());
            //查询门店信息
            StoreInfoVO storeInfoVO = new StoreInfoVO();
            storeInfoVO.setStoreId(req.getStoreId());
            storeInfoVO.setTanentId(req.getTenantId());
            BizBaseResponse<StoreDTO> resultData = storeInfoClient.getStoreInfo(storeInfoVO);
            if (null != resultData && null != resultData.getData()) {
                StoreDTO storeDTO = resultData.getData();
                seckillActivityRemind.setStoreAddress(storeDTO.getAddress());
                seckillActivityRemind.setStoreName(storeDTO.getStoreName());
                seckillActivityRemind.setStorePhone(storeDTO.getClientAppointPhone());
            }
            this.baseMapper.insert(seckillActivityRemind);
        }
    }

    @Override
    public void autoSendRemind() {
        log.info("autoSendRemind -> start");
        List<SeckillActivityRemind> remindList = this.baseMapper.selectList(new EntityWrapper<SeckillActivityRemind>().eq("is_delete", 0));
        List<String> activityIds = remindList.stream().map(x->x.getSeckillActivityId()).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(activityIds)){
            //查进行中的活动
            Date now = new Date();
            List<SeckillActivity> seckillActivityList = seckillActivityService.selectList(new EntityWrapper<SeckillActivity>()
                    .in("id",activityIds).eq("is_delete", 0).le("start_time", now)
                    .ge("end_time", now).ne("status", 9));
            Map<String,List<SeckillActivityRemind>> remindMap = remindList.stream().collect(Collectors.groupingBy(x->x.getSeckillActivityId()));
            for (SeckillActivity seckillActivity : seckillActivityList){
                if (remindMap.containsKey(seckillActivity.getId())){
                    for (SeckillActivityRemind seckillActivityRemind : remindMap.get(seckillActivity.getId())){
                        //发送服务通知，更新
                        taskExecutor.execute(() -> {
                            String result = iWechatService.miniSeckillProgramNotify(seckillActivityRemind,seckillActivity);
                            if (result != null){
                                JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
                                //发送成功
                                if (jsonObject.getInteger("errcode").equals(0)){
                                    seckillActivityRemind.setStatus(1);  //成功
                                } else {
                                    seckillActivityRemind.setStatus(2);  //失败
                                }
                                seckillActivityRemind.setIsDelete(1); //已提醒 删除
                            }
                            seckillActivityRemind.setReturnMessage(result);
                            seckillActivityRemind.setUpdateTime(new Date());
                            this.baseMapper.updateById(seckillActivityRemind);
                        });
                    }
                }
            }
        }
    }


}
