package com.tuhu.store.saas.marketing.service.seckill;

import com.baomidou.mybatisplus.service.IService;
import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.dataobject.SeckillRegistrationRecord;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillRecordAddReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityStatisticsResp;
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
     *
     * @param activityIds
     * @return
     */
    Map<String, Integer> activityIdNumMap(List<String> activityIds);

    /**
     * 活动数据
     *
     * @param activityId
     * @return
     */
    SeckillActivityStatisticsResp dataStatistics(String activityId);

    /**
     * 参与详情
     *
     * @param customersId
     * @return
     */
    List<SeckillRegistrationRecordResp> participateDetail(String customersId);

    /**
     * 活动数据-已购客户、浏览未购买客户分页列表
     *
     * @param req
     * @return
     */
    PageInfo<SeckillRegistrationRecordResp> pageBuyList(SeckillActivityReq req);


    /**
     * 活动数据-已购客户、浏览未购买客户分页列表
     *
     * @param req
     * @return
     */
    PageInfo<SeckillRegistrationRecordResp> pageNoBuyBrowseList(SeckillActivityReq req);

    /**
     * 秒杀活动下单24小时未支付自动取消相关订单
     * @return
     */
    void seckillActivity24AutoCancel();


    /**
     * 活动抢购
     *
     * @param req
     * @return
     */
    void customerActivityOrderAdd(SeckillRecordAddReq req);


}
