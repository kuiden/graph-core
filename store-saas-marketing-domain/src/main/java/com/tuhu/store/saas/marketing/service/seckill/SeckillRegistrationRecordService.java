package com.tuhu.store.saas.marketing.service.seckill;

import com.baomidou.mybatisplus.service.IService;
import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.dataobject.SeckillRegistrationRecord;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRegistrationRecordResp;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 秒杀报名记录表  服务类
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
public interface SeckillRegistrationRecordService extends IService<SeckillRegistrationRecord> {
    /**
     * 活动id 对应的购买数量
     * @param activityIds
     * @return
     */
    Map<String, Integer> activityIdNumMap(List<String> activityIds);

    /**
     * 活动数据-已购客户、浏览未购买客户分页列表
     * @param req
     * @return
     */
    PageInfo<SeckillRegistrationRecordResp> pageBuyOrBrowseList(SeckillActivityReq req);

    /**
     * 参与记录
     * @param customersId
     * @return
     */
    List<SeckillRegistrationRecordResp> participateDetail(String customersId);

}
