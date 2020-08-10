package com.tuhu.store.saas.marketing.service;

import com.tuhu.base.service.IBaseQueryService;
import com.tuhu.base.service.IBaseService;
import com.tuhu.base.service.ICrudService;
import com.tuhu.store.saas.marketing.entity.EndUserVisitedStoreEntity;
import com.tuhu.store.saas.marketing.request.EndUserVistiedStoreRequest;
import com.tuhu.store.saas.marketing.response.EndUserVisitedStoreResp;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 车主端用户访问的门店记录 服务类
 * </p>
 *
 * @author someone
 * @since 2020-08-03
 */
public interface IEndUserVisitedStoreService extends IBaseQueryService<EndUserVisitedStoreEntity>, IBaseService<EndUserVisitedStoreEntity>, ICrudService<EndUserVisitedStoreEntity> {



    /**
     * 根据openId和门店id定位一条访问记录
     *
     * @param openId
     * @param storeId
     * @return
     */
    EndUserVisitedStoreEntity findFirstByOpenIdAndStoreId(String openId, String storeId);

    /**
     * 根据小程序code及门店id，记录用户访问门店的行为
     *
     * @param endUserVistiedStoreRequest
     * @return
     */
    EndUserVisitedStoreEntity recordEndUserVistiedStore(EndUserVistiedStoreRequest endUserVistiedStoreRequest);

    /**
     * 新增浏览的门店记录
     *
     * @param endUserVisitedStoreEntity
     * @return
     */
    Integer addNewEndUserVisitedStore(EndUserVisitedStoreEntity endUserVisitedStoreEntity);

    /**
     * 根据Id更新访问次数和时间
     *
     * @param id
     * @param newDate
     * @return
     */
    Integer updateVisitedTimeById(String id, Date newDate);

    /**
     * 根据小程序openId及门店id，记录用户访问门店的行为
     *
     * @param endUserVisitedStoreEntity
     * @return
     */
    EndUserVisitedStoreEntity recordEndUserVistiedStore(EndUserVisitedStoreEntity endUserVisitedStoreEntity);


    /**
     * 根据openId查询所有浏览记录,根据访问时间倒序
     *
     * @param openId
     * @return
     */
    List<EndUserVisitedStoreEntity> findAllByOpenId(String openId);

    /**
     * 根据小程序code，查询用户所有访问过的门店记录
     *
     * @param endUserVistiedStoreRequest
     * @return
     */
    List<EndUserVisitedStoreResp> findAllVisitedStoresByOpenIdCode(EndUserVistiedStoreRequest endUserVistiedStoreRequest);
}
