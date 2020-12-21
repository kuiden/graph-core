package com.tuhu.store.saas.marketing.service.seckill;

import com.baomidou.mybatisplus.service.IService;
import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivity;
import com.tuhu.store.saas.marketing.request.seckill.*;
import com.tuhu.store.saas.marketing.response.seckill.*;
import com.tuhu.store.saas.user.dto.StoreDTO;
import org.springframework.transaction.annotation.Transactional;

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
     * 保存秒杀活动
     * @param model
     * @return
     */
    @Transactional
    String saveSeckillActivity(SeckillActivityModel model);

    /**
     * 获取活动详情by ID
     * @param id
     * @param storeId
     * @return
     */
    SeckillActivityModel getSeckillActivityModelById(String id, Long storeId);

    /**
     * 自动更新下架
     *
     * @return
     */
    int autoUpdateOffShelf();

    /**
     * 分页查询活动列表
     *
     * @param req
     * @return
     */
    PageInfo<SeckillActivityResp> pageList(SeckillActivityReq req);

    /*
     * 查询活动列表 - C端
     */
    List<SeckillActivityListResp> clientActivityList(Long storeId, Long tenantId);

    /*
     * 查询活动详情 - C端
     */
    SeckillActivityDetailResp clientActivityDetail(SeckillActivityDetailReq req);

    //活动预览
    SeckillActivityDetailResp activityDetailPreview(String id);

    /*
     * 查询秒杀活动参与记录（分页） - C端
     */
    PageInfo<SeckillRecordListResp> clientActivityRecordList(SeckillActivityDetailReq req);

    /*
     * 查询客户活动订单列表 - C端
     */
    List<CustomerActivityOrderListResp> customerActivityOrderList(String customerId, Long storeId, Long tenantId);

    /*
     * 查询秒杀订单详情 - C端
     */
    CustomerActivityOrderDetailResp customerActivityOrderDetail(SeckillActivityDetailReq req);

    /**
     * 下架活动
     *
     * @param seckillActivityId
     * @return
     */
    boolean offShelf(String seckillActivityId);


    /**
     * 上架活动
     *
     * @param seckillActivityId
     * @return
     */
    boolean onShelf(String seckillActivityId);

    /**
     * 活动海报
     *
     * @param request
     * @return
     */
    SeckillActivityResp poster(SeckillActivityQrCodeReq request);


    /**
     * 活动二位码url
     *
     * @param request
     * @return
     */
    String qrCodeUrl(SeckillActivityQrCodeReq request);


    /**
     * 活动二位码url
     *
     * @param request
     * @return
     */
    String qrCodeUrlMin(SeckillActivityQrCodeReq request);


    /**
     * 活动二位码url
     *
     * @param request
     * @return
     */
    byte[]  qrCodeImage(SeckillActivityQrCodeReq request);

    /**
     * 活动数据-已购客户、浏览未购买客户分页列表
     *
     * @param req
     * @return
     */
    PageInfo<SeckillRegistrationRecordResp> pageBuyOrBrowseList(SeckillActivityReq req);


    /**
     * 校验活动id
     *
     * @param seckillActivityId
     * @param flag              ture:B端   false:C端
     * @return
     */
    SeckillActivity check(String seckillActivityId, Boolean flag);

    /**
     * 获取门店信息
     *
     * @param flag true C端 false B端
     * @return
     */
    StoreDTO getStoreInfo(boolean flag);

    /**
     * 获取活动预售数量
     * @param seckillActivityBuy
     * @return
     */
    Integer getPreNum(SeckillActivityBuy seckillActivityBuy);
}
