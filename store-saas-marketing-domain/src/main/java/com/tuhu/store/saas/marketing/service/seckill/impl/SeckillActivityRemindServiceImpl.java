package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivityRemind;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillActivityRemindMapper;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.seckill.SeckillRemindAddReq;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityRemindService;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 秒杀活动开抢提醒 服务实现类
 * </p>
 *
 * @author wangyuqing
 * @since 2020-12-08
 */
@Service
public class SeckillActivityRemindServiceImpl extends ServiceImpl<SeckillActivityRemindMapper, SeckillActivityRemind> implements SeckillActivityRemindService {

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Override
    public void customerActivityRemindAdd(SeckillRemindAddReq req) {
        //查询是否已添加过
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

}
