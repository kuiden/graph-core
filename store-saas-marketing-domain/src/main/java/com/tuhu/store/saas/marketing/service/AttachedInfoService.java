package com.tuhu.store.saas.marketing.service;

import com.baomidou.mybatisplus.service.IService;
import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.dataobject.AttachedInfo;
import com.tuhu.store.saas.marketing.request.AttachedInfoAddReq;
import com.tuhu.store.saas.marketing.request.AttachedInfoPageReq;
import com.tuhu.store.saas.marketing.response.AttachedInfoResp;

/**
 * <p>
 * 附属信息表 服务类
 * </p>
 *
 * @author zhaijingtao
 * @since 2020-12-04
 */
public interface AttachedInfoService extends IService<AttachedInfo> {

    String add(AttachedInfoAddReq req, Long storeId, Long tenantId, String userId);

    PageInfo<AttachedInfoResp> getListByQuery(AttachedInfoPageReq req);

    AttachedInfoResp getAttachedInfoById(String id, Long storeId);

    Boolean del(String id, Long storeId);
}
