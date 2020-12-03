package com.tuhu.store.saas.marketing.service.seckill;

import com.baomidou.mybatisplus.service.IService;
import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivity;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityStatisticsResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRegistrationRecordResp;

import java.util.List;

/**
 * <p>
 * 秒杀活动表 服务类
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
public interface SeckillActivityService extends IService<SeckillActivity> {

    /**
     * 自动更新下架
     * @return
     */
    int autoUpdateOffShelf();

    /**
     * 分页查询活动列表
     * @param req
     * @return
     */
    PageInfo<SeckillActivityResp> pageList(SeckillActivityReq req);

    /**
     * 下架活动
     * @param activityId
     * @return
     */
    boolean offShelf(String activityId);

    /**
     * 活动数据
     * @param activityId
     * @return
     */
    SeckillActivityStatisticsResp dataStatistics(String activityId);

    /**
     * 参与详情
     * @param customersId
     * @return
     */
    List<SeckillRegistrationRecordResp> participateDetail(String customersId);

    /**
     * 活动数据-已购客户、浏览未购买客户分页列表
     * @param req
     * @return
     */
    PageInfo<SeckillRegistrationRecordResp> pageBuyList(SeckillActivityReq req);
}
