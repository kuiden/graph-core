package com.tuhu.store.saas.marketing.service;

import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.po.Activity;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.response.*;
//import com.tuhu.saas.crm.po.Activity;
//import com.tuhu.saas.crm.rpc.dto.ActivityCustomerDTO;
//import com.tuhu.saas.crm.rpc.vo.ActivityCustomerRpcVO;
//import com.tuhu.saas.crm.rpc.vo.ServiceOrderActivityUseVO;

import java.util.List;
import java.util.Map;

/**
 * 营销活动相关接口
 */
public interface IActivityService {
    /**
     * 新增营销活动
     *
     * @param addActivityReq
     * @return
     */
    AddActivityReq addNewActivity(AddActivityReq addActivityReq);

    /**
     * 根据营销活动标题获取营销活动
     *
     * @param title
     * @param storeId
     * @return
     */
    List<Activity> getActivityByTitle(String title, Long storeId);

    /**
     * 新增营销活动项目
     *
     * @param addActivityReq
     * @param code
     */
    void addNewActivityItem(AddActivityReq addActivityReq, String code);

    /**
     * 根据营销ID获取营销活动详情
     *
     * @param activityId
     * @return
     */
    ActivityResp getActivityDetailById(Long activityId, Long storeId);

    ActivityResp getActivityDetailById(Long activityId);

    /**
     * 营销活动上下架操作
     *
     * @param activityChangeStatusReq
     * @return
     */
    ActivityChangeStatusReq changeActivityStatus(ActivityChangeStatusReq activityChangeStatusReq);

    /**
     * 营销活动列表查询
     *
     * @param activityListReq
     * @return
     */
    PageInfo<ActivityResp> listActivity(ActivityListReq activityListReq);

    /**
     * 营销活动编辑
     *
     * @param editActivityReq
     * @return
     */
    EditActivityReq editActivity(EditActivityReq editActivityReq);

    /**
     * 维护活动项目
     *
     * @param oldActivity
     * @param editActivityReq
     */
    void editActivityItems(ActivityResp oldActivity, EditActivityReq editActivityReq);

    /**
     * 营销活动报名
     *
     * @param activityApplyReq
     * @return
     */
    CommonResp<String> applyActivity(ActivityApplyReq activityApplyReq);

    /**
     * 生成活动报名信息
     *
     * @param activity
     * @param activityApplyReq
     * @return
     */
//    CommonResp<String> genarateActivityCustomer(Activity activity, ActivityApplyReq activityApplyReq);

    /**
     * 获取客户活动报名详情
     *
     * @param activityCustomerReq
     * @return
     */
    ActivityCustomerResp getActivityCustomerDetail(ActivityCustomerReq activityCustomerReq);


    /**
     * 计算活动的原价格
     * @param activityResp
     * @return
     */
    Boolean getOriginalPriceOfActivity(ActivityResp activityResp);

    /**
     * 根据活动编码获取活动信息
     *
     * @param activityCode
     * @return
     */
    ActivityResp getActivityByActivityCode(String activityCode);

    /**
     * 确认核销或取消订单
     *
     * @param activityCustomerReq
     * @return
     */
    Boolean writeOffOrCancelActivityCustomer(ActivityCustomerReq activityCustomerReq);

    /**
     * 使用或取消使用营销活动
     *
     * @param serviceOrderActivityUseVO
     * @return
     */
//    ActivityCustomerDTO useOrCancelActivityCustomerForOrder(ServiceOrderActivityUseVO serviceOrderActivityUseVO);

    /**
     * 客户活动订单信息查询
     *
     * @param activityCustomerListReq
     * @return
     */
    PageInfo<SimpleActivityCustomerResp> listActivityCustomer(ActivityCustomerListReq activityCustomerListReq);


    /**
     * 营销活动列表查询
     *
     * @param storeId
     * @return
     */
    List<Activity> getActivityListByStoreId(Long storeId);

    /**
     * c端-根据营销ID获取营销活动详情
     *
     * @param activityId
     * @return
     */
    ActivityResp  getActivityDetailForClient(Long activityId, Long storeId, String customerId);

    ActivityCustomerPageResp getMyActivityList(ActivityCustomerListRequest activityCustomerListRequest);

    String getQrCodeForActivity(ActivityQrCodeRequest request);

    /**
     * 获取门店活动数据
     * @param activityId
     * @param storeId
     * @return
     */
    Map<String, Object> getActivityStatistics(Long activityId, Long storeId);

    /**
     * 营销活动报名回写工单id并修改使用状态
     * @param activityCustomerRpcVO
     * @return
     */
//    int updateActivityCustomerForOrder(ActivityCustomerRpcVO activityCustomerRpcVO);

    /**
     * 获取营销活动报名详情
     * @param activityCustomerRpcVO
     * @return
     */
//    ActivityCustomerDTO getActivityCustomer(ActivityCustomerRpcVO activityCustomerRpcVO);

    /**
     * 删除营销活动报名
     * @param activityCustomerRpcVO
     * @return
     */
//    int deleteActivityCustomer(ActivityCustomerRpcVO activityCustomerRpcVO);


    /**
     * 新增营销活动 - 营销版
     */
    AddActivityRequest addActivity(AddActivityRequest addActivityReq);
    //营销活动详情
    ActivityResponse getActivityById(Long activityId, Long storeId);
    //营销活动列表查询
    PageInfo<ActivityResponse> list(ActivityListReq activityListReq);
    //营销活动编辑
//    EditActivityRequest edit(EditActivityRequest editActivityReq);

}
