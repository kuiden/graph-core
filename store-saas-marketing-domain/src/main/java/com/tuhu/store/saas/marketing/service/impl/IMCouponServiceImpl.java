package com.tuhu.store.saas.marketing.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mengfan.common.util.GatewayClient;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdReqVO;
import com.tuhu.store.saas.crm.vo.CustomerVO;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.*;
import com.tuhu.store.saas.marketing.po.ActivityCustomer;
import com.tuhu.store.saas.marketing.po.ActivityCustomerExample;
import com.tuhu.store.saas.marketing.po.CouponPO;
import com.tuhu.store.saas.marketing.po.CustomerCouponPO;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.response.*;
import com.tuhu.store.saas.marketing.service.IClientEventRecordService;
import com.tuhu.store.saas.marketing.service.ICouponService;
import com.tuhu.store.saas.marketing.service.IMCouponService;
import com.tuhu.store.saas.marketing.service.MiniAppService;
import com.tuhu.store.saas.marketing.util.GsonTool;
import com.tuhu.store.saas.marketing.util.QrCode;
import com.tuhu.store.saas.user.dto.ClientEventRecordDTO;
import com.tuhu.store.saas.user.dto.ClientStoreDTO;
import com.tuhu.store.saas.user.dto.UserDTO;
import com.tuhu.store.saas.user.vo.ClientEventRecordVO;
import com.tuhu.store.saas.user.vo.ClientStoreVO;
import com.tuhu.store.saas.user.vo.EventTypeEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/14
 * Time: 9:51
 * Description:
 */
@Service
@Slf4j
public class IMCouponServiceImpl implements IMCouponService {

    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private CustomerCouponMapper customerCouponMapper;
    @Autowired
    private OrderCouponMapper orderCouponMapper;
    @Autowired
    private CouponScopeCategoryMapper couponScopeCategoryMapper;

    @Autowired
    private ActivityCustomerMapper activityCustomerMapper;
    //@Autowired
    //private IBussinessCategoryService bussinessCategoryService;
    //@Autowired
    //private IStoreInfoRpcService storeInfoRpcService;
    //@Autowired
    //private IUserRpcService iUserRpcService;

    private final String PIC_URL_DOMAIN = "http://img3.tuhu.org";

    @Autowired
    private GatewayClient getwayClient;

    @Value("${tuhu.img.upload}")
    private String uploadImgUrl;

    private String tokenUrl = "https://api.yunquecloud.com/auth/wechat/accessToken";

    @Autowired
    private ICouponService iCouponService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 优惠券个人领取限制缓存key
     */
    private static final String personalCouponGetNumberPrefix = "COUPON:PERSONAL:";
    /**
     * open接口防刷接口
     */
    private static final String openCouponPrefix = "openCouponPrefix";
    @Autowired
    private EndUserVisitedCouponWriteMapper endUserVisitedCouponWriteMapper;

    @Autowired
    private MiniAppService miniAppService;

    private static final String BUSSINESS_CATEGORY_DTOS_PREFIX = "bussiness_categories_dtos_";

    @Autowired
    private IClientEventRecordService iClientEventRecordService;

    @Override
    public String getQrCodeForCoupon(QrCodeRequest req) {

        /* 1、调微信api,根据当前storeId生成二维码图片buffer,base64编码
         */

         /*
          2、上传图片到图片服务器，
        */
        String qrUrl = miniAppService.getQrCodeUrl(req.getScene(), req.getPath(), req.getWidth());
        /*
          3、保存url到coupon表
        */
        saveQrUrlToDatabase(req.getCouponId(), qrUrl);

        return qrUrl;
    }

    /**
     * 抵用券基本信息：名称，金额满，优惠金额，限定商品/类目
     * 1、总数、剩余数，发放数，使用数
     *
     * @param req
     * @return
     */
    @Override
    public Map getOveralEffect(CouponRequest req) {
        Map resultMap = Maps.newHashMap();
        Coupon couponInfo = couponMapper.selectByCouponCode(req.getCouponCode());
        if (couponInfo != null) {
            resultMap.put("couponInfo", couponInfo);
            CustomerCoupon record = new CustomerCoupon(req.getCouponCode());
            Map overViewDataMap = customerCouponMapper.queryCountForOverViewData(record);
            resultMap.putAll(overViewDataMap);

            //查询访问用户数和新增客户数
            ClientEventRecordVO clientEventRecordVO = new ClientEventRecordVO();
            clientEventRecordVO.setStoreId(String.valueOf(req.getStoreId()));
            clientEventRecordVO.setContentType("coupon");
            clientEventRecordVO.setContentValue(couponInfo.getEncryptedCode());
            List<String> eventTypes = Arrays.stream(EventTypeEnum.values()).map(EventTypeEnum::getCode).collect(Collectors.toList());
            clientEventRecordVO.setEventTypes(eventTypes);
            Map<String, ClientEventRecordDTO> clientEventRecordDTOMap = iClientEventRecordService.getClientEventRecordStatisticsByEvent(clientEventRecordVO);
            if (MapUtils.isNotEmpty(clientEventRecordDTOMap)) {
                ClientEventRecordDTO visitRecord = clientEventRecordDTOMap.get(EventTypeEnum.VISIT.getCode());
                if (null != visitRecord) {
                    resultMap.put("visitUserCount", visitRecord.getUserCount());//访问用户数
                } else {
                    resultMap.put("visitUserCount", 0L);//访问用户数
                }
                //通过登录新增的客户数
                ClientEventRecordDTO loginRecord = clientEventRecordDTOMap.get(EventTypeEnum.LOGIN.getCode());
                Long loginUserCount = 0L;
                if (null != loginRecord) {
                    loginUserCount = loginRecord.getUserCount();
                }
                //通过注册新增的客户数
                Long registeredUserCount = 0L;
                ClientEventRecordDTO registeredRecord = clientEventRecordDTOMap.get(EventTypeEnum.REGISTERED.getCode());
                if (null != registeredRecord) {
                    registeredUserCount = registeredRecord.getUserCount();
                }
                resultMap.put("newUserCount", loginUserCount + registeredUserCount);//新增客户数
            } else {
                resultMap.put("visitUserCount", 0L);
                resultMap.put("newUserCount", 0L);
            }
        }

/*
        //限定范围
        if (couponInfo.getScopeType() == 2) {//限定分类
            CouponScopeCategoryExample example = new CouponScopeCategoryExample();
            CouponScopeCategoryExample.Criteria criteria = example.createCriteria();
            criteria.andCouponCodeEqualTo(req.getCouponCode());
            example.setOrderByClause("id desc");
            List<CouponScopeCategory> couponScopeCategories = couponScopeCategoryMapper.selectByExample(example);
            if (CollectionUtils.isNotEmpty(couponScopeCategories)) {
                Long tenantId = couponScopeCategories.get(0).getTenantId();
                Long storeId = couponScopeCategories.get(0).getStoreId();
                List<BusinessCateGoryRespDTO> businessCateGoryRespDTOS = bussinessCategoryService.showBusinessCategory(tenantId, storeId);
                if (CollectionUtils.isNotEmpty(businessCateGoryRespDTOS)) {
                    businessCateGoryRespDTOS.forEach(businessCateGoryRespDTO -> {
                        couponScopeCategories.forEach(couponScopeCategory -> {
                            if (businessCateGoryRespDTO.getCode().equals(couponScopeCategory.getCategoryCode())) {
                                couponScopeCategory.setCategoryName(businessCateGoryRespDTO.getName());
                            }
                        });
                    });
                }
            }

            resultMap.put("couponScopeCategories", couponScopeCategories);
        }*/
        return resultMap;
    }

    /**
     * 抵用券基本信息：名称，金额满，优惠金额， 限定商品/类目
     * 1、访问用户数 2、新增客户数 3、领用数  4、使用数
     *
     * @param req
     * @return
     */
    @Override
    public Map getGettingEffect(CouponRequest req) {
        return null;
    }

    @Autowired
    private CustomerClient customerClient;

    @Override
    public CustomerCouponPageResp getCouponReceiveList(CouponReceiveRecordRequest req) {
        CustomerCouponPageResp customerCouponPageResp = new CustomerCouponPageResp();
        Page<CustomerCouponPO> customerCouponPosPage = new Page<>();
        customerCouponPageResp.setCustomerCouponPOS(customerCouponPosPage);
        CustomerCouponSearch record = new CustomerCouponSearch();
        record.setCouponCode(req.getCouponCode());
        record.setSearchKey(req.getSearchKey());
        if (req.getReceiveType() != null) {
            record.setReceiveType(req.getReceiveType().byteValue());
        }

        PageHelper.startPage(req.getPageNum() + 1, req.getPageSize());
        List<CustomerCouponPO> recordList = null;
        try {
            if (StringUtils.isNotBlank(req.getSearchKey())) {
                CustomerVO vo = new CustomerVO();
                vo.setStoreId(req.getStoreId());
                vo.setTenantId(req.getTenantId());
                vo.setQuery(req.getSearchKey());
                BizBaseResponse<List<CustomerDTO>> crmResult = customerClient.getCustomerByQuery(vo);
                if (crmResult != null && CollectionUtils.isNotEmpty(crmResult.getData())) {
                    List<String> idList = crmResult.getData().stream().map(x -> x.getId()).distinct().collect(Collectors.toList());
                    record.setCustomerIdList(idList);
                } else {
                    return customerCouponPageResp;
                }
            }
            recordList = customerCouponMapper.selectRecordList(record);
            if (CollectionUtils.isNotEmpty(recordList)) {
                CustomerVO vo = new CustomerVO();
                vo.setStoreId(req.getStoreId());
                vo.setTenantId(req.getTenantId());
                List<String> idList = recordList.stream().map(x -> x.getCustomerId()).distinct().collect(Collectors.toList());
                vo.setCustomerList(idList);
                vo.setQuery(req.getSearchKey());
                BizBaseResponse<List<CustomerDTO>> crmResult = customerClient.getCustomerByQuery(vo);
                if (crmResult != null && CollectionUtils.isNotEmpty(crmResult.getData())) {
                    Map<String, CustomerDTO> map = crmResult.getData().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
                    for (CustomerCouponPO x : recordList) {
                        if (map.containsKey(x.getCustomerId())) {
                            CustomerDTO customerDTO = map.get(x.getCustomerId());
                            x.setCustomerName(customerDTO.getName());
                            x.setCustomerTelephone(customerDTO.getPhoneNumber());
                        }

                    }
                }
            }

        } catch (Exception e) {
            log.error("getCouponReceiveList error", e);
        }
        if (CollectionUtils.isNotEmpty(recordList)) {
            List<String> sendUserIdList = Lists.newArrayList();
            recordList.forEach(recordItem -> {
                if (StringUtils.isNotBlank(recordItem.getSendUser())) {

                    sendUserIdList.add(recordItem.getSendUser());
                }
            });
            BizBaseResponse<Map<String, UserDTO>> crmResult = customerClient.getUserInfoMapByIdList(sendUserIdList);
            Map<String, UserDTO> userInfoMap = crmResult != null && crmResult.getData() != null ? crmResult.getData() : new HashMap<>();
            recordList.forEach(recordItem -> {
                //* 查询发券人姓名
                UserDTO dto = userInfoMap.get(recordItem.getSendUser());
                recordItem.setSendUser(dto != null ? dto.getUsername() : "");
                //* 判断券是否已失效
                Date date = new Date();
                if (recordItem.getUseEndTime() != null && date.after(recordItem.getUseEndTime())) {
                    recordItem.setUseStatus((byte) -1);
                }
            });
        }
        customerCouponPosPage.addAll(recordList);
        PageInfo<CustomerCouponPO> customerCouponPOPageInfo = new PageInfo<>(recordList);
        CustomerCouponPageResp.PageInfo pageInfo = new CustomerCouponPageResp.PageInfo();
        pageInfo.setTotal(customerCouponPOPageInfo.getTotal());
        pageInfo.setPages(customerCouponPOPageInfo.getPages());
        pageInfo.setPageNum(req.getPageNum());
        pageInfo.setPageSize(req.getPageSize());
        customerCouponPageResp.setPageInfo(pageInfo);
        if (pageInfo.getPageNum() >= pageInfo.getPages()) {
            customerCouponPageResp.setCustomerCouponPOS(null);
        }
        return customerCouponPageResp;
    }

    /**
     * 送券
     *
     * @param req
     * @return
     */
    @Override
    public Map sendCoupon(SendCouponRequest req) {
        Map result = Maps.newHashMap();
        if (CollectionUtils.isEmpty(req.getCouponCodeList()) || CollectionUtils.isEmpty(req.getCustomerIdList())) {
            result.put("success", false);
            result.put("message", "CouponCode and CustomerId cannot be null");
            return result;
        }
        List<Map> successList = Lists.newArrayList();
        List<Map> failedList = Lists.newArrayList();

        for (String couponCode : req.getCouponCodeList()) {
            try {
                Coupon couponInfo = couponMapper.selectByCouponCode(couponCode);
                Map map = judgeCouponCanReveice(couponInfo);
                if (map.get("success") != null && !(Boolean) map.get("success")) {
                    failedList.add(getSendCouponResultMap(couponCode, "", (String) map.get("message"), (Integer) map.get("failedType")));
                    continue;
                }

                for (String customerId : req.getCustomerIdList()) {
                    Map singleResult = sendCouponSingle(couponInfo, couponCode, customerId, req.getSendUser(), req.getReceiveType());
                    if (singleResult.get("success") != null && !(Boolean) singleResult.get("success")) {
                        failedList.add(getSendCouponResultMap(couponCode, customerId, (String) singleResult.get("message"), (Integer) singleResult.get("failedType")));
                    } else {
                        successList.add(getSendCouponResultMap(couponCode, customerId, (String) singleResult.get("message"), (Integer) singleResult.get("failedType")));
                    }
                }

            } catch (Exception e) {
                log.error("getCouponList error,couponCodeList=" + req.getCouponCodeList() + ",customerList=" + req.getCustomerIdList(), e);
            }
        }
        result.put("success", true);
        result.put("message", "sendCoupon finished");
        result.put("successList", successList);
        result.put("failedList", failedList);
        return result;
    }

    @Override
    public CouponPageResp getCouponList(CouponSearchRequest req, String customerId) {
        if (req.getStoreId() == null) {
            return null;
        }
        CouponPageResp couponResp = new CouponPageResp();
        Page<CouponItemResp> couponItemRespList = new Page<>();
        couponResp.setCouponItemResps(couponItemRespList);

        CouponExample example = new CouponExample();
        CouponExample.Criteria criteria = example.createCriteria();
        criteria.andAllowGetEqualTo((byte) 1);
//        criteria.andStatusEqualTo((byte) 1);
        criteria.andStoreIdEqualTo(req.getStoreId());
        if (StringUtils.isNotBlank(req.getSearchKey())) {
            criteria.andTitleLike("%" + req.getSearchKey() + "%");
        }
        example.setOrderByClause("update_time desc");
        PageHelper.startPage(req.getPageNum() + 1, req.getPageSize());

        List<Coupon> couponRecordList = couponMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(couponRecordList)) {
            List<String> couponCodeList = Lists.newArrayList();
            List<String> limitCategoryCouponCodeList = Lists.newArrayList();
            couponRecordList.forEach(couponRecord -> {
                couponCodeList.add(couponRecord.getCode());
                if (couponRecord.getScopeType() == 2) {//限制分类
                    limitCategoryCouponCodeList.add(couponRecord.getCode());
                }
            });

            //获取抵用券已发送张数， couponCode:已发放或者领取张数
            Map sendNumberMap = getUsedCouponNumberMap(couponCodeList);
            Map receivedCouponMap = getCustomerReceivedCouponMap(couponCodeList, customerId);
            /**
             * 限定分类信息
             */
            Map<String, List<CouponScopeCategory>> couponScopeMap = getCouponScopeCategories(limitCategoryCouponCodeList);

            couponRecordList.forEach(couponRecord -> {
                CouponItemResp item = new CouponItemResp();
                BeanUtils.copyProperties(couponRecord, item);
                /*
                  获取优惠券剩余数量
                  grantNumber==-1: 领取张数不限
                  grantNumber>=0 :  grantNumber - 已发送张数
                 */
                Long leftNumber = -1L; //默认-1,表示剩余数量不限
                if (couponRecord.getGrantNumber() >= 0) {
                    Long sendNumber = (Long) sendNumberMap.get(couponRecord.getCode()) == null ? 0 : (Long) sendNumberMap.get(couponRecord.getCode());
                    leftNumber = couponRecord.getGrantNumber() - sendNumber;
                    if (leftNumber < 0) {
                        leftNumber = 0L;
                    }
                }
                item.setLeftCouponNumber(leftNumber);
                item.setHasReceived(receivedCouponMap.get(couponRecord.getCode()) == null ? false : true);
                item.setCouponScopeCategories(couponScopeMap.get(couponRecord.getCode()));
                couponItemRespList.add(item);
            });

        }

        PageInfo<Coupon> couponPageInfo = new PageInfo<>(couponRecordList);
        CouponPageResp.PageInfo pageInfo = new CouponPageResp.PageInfo();
        pageInfo.setTotal(couponPageInfo.getTotal());
        pageInfo.setPages(couponPageInfo.getPages());
        pageInfo.setPageNum(req.getPageNum());
        pageInfo.setPageSize(req.getPageSize());
        couponResp.setPageInfo(pageInfo);
        if (pageInfo.getPageNum() >= pageInfo.getPages()) {
            couponResp.setCouponItemResps(null);
        }
        return couponResp;
    }

    private Map getUsedCouponNumberMap(List<String> couponCodeList) {
        Map usedNumberMap = Maps.newHashMap();//couponCode:已发放或者领取张数
        if (CollectionUtils.isNotEmpty(couponCodeList)) {
            List<Map<String, Object>> usedNumberList = customerCouponMapper.countGrantNumberByCouponCodeList(couponCodeList, null);
            if (CollectionUtils.isNotEmpty(usedNumberList)) {
                for (Map map : usedNumberList) {
                    usedNumberMap.put(map.get("couponCode"), map.get("number"));
                }
            }
        }

        return usedNumberMap;
    }

    /**
     * 查询用户是否有领取过couponCodeList中优惠券
     *
     * @param couponCodeList
     * @param customerId
     * @return
     */
    private Map getCustomerReceivedCouponMap(List<String> couponCodeList, String customerId) {
        if (CollectionUtils.isEmpty(couponCodeList) || StringUtils.isBlank(customerId)) {
            return Maps.newHashMap();
        }

        List<Map<String, Object>> receivedCouponlist = customerCouponMapper.getRecievedCountByCouponCode(couponCodeList, customerId);
        if (CollectionUtils.isEmpty(receivedCouponlist)) {
            return Maps.newHashMap();
        }
        Map resultMap = Maps.newHashMap();

        for (Map<String, Object> map : receivedCouponlist) {
            resultMap.put(map.get("couponCode"), map.get("recievedCount"));
        }

        return resultMap;
    }

    private Map getSendCouponResultMap(String couponCode, String customerId, String message, Integer failedType) {
        Map curResultMap = Maps.newHashMap();
        curResultMap.put("couponCode", couponCode);
        curResultMap.put("customerId", customerId);
        curResultMap.put("message", message);
        curResultMap.put("failedType", failedType);
        return curResultMap;
    }

    /**
     * 单个couponId,customerId送券
     *
     * @param
     * @return
     */
    private Map sendCoupon(Coupon coupon, Customer customer, String sendUser, int receiveType) {
        Map map = Maps.newHashMap();
        try {
            SendCouponReq sendCouponReq = new SendCouponReq();
            sendCouponReq.setReceiveType(receiveType);
            sendCouponReq.setUserId(sendUser);
            sendCouponReq.setStoreId(coupon.getStoreId());
            sendCouponReq.setTenantId(coupon.getTenantId());
            CommonResp<CustomerCoupon> customerCouponResp = iCouponService.generateCustomerCoupon(coupon, customer, sendCouponReq);
            if (!customerCouponResp.isSuccess()) {
                log.error("sendCouponSingle error,message={}", customerCouponResp.getMessage());
                map.put("success", false);
                map.put("resultType", customerCouponResp.getCode());//
                map.put("message", customerCouponResp.getMessage());
                return map;
            } else {
                customerCouponMapper.insertSelective(customerCouponResp.getData());
            }
        } catch (Exception e) {
            map.put("success", false);
            map.put("resultType", 4004);//
            map.put("message", "领取失败");
            log.error("sendCouponSingle error，couponCode=" + coupon.getCode() + ",customerId=" + customer.getId() + ",receiveType=" + receiveType, e);
            return map;
        }
        map.put("success", true);
        map.put("resultType", 0);//领取成功
        map.put("message", "领取成功");
        return map;
    }

    /**
     * 单个couponId,customerId送券
     *
     * @param
     * @return
     */
    private Map sendCouponSingle(Coupon coupon, String couponCode, String customerId, String sendUser, int receiveType) {
        Map map = Maps.newHashMap();
        try {
            CustomerCouponExample example = new CustomerCouponExample();
            CustomerCouponExample.Criteria criteria = example.createCriteria();
            criteria.andCouponCodeEqualTo(couponCode);
            int count = customerCouponMapper.countByExample(example);
            if (coupon.getGrantNumber() != -1 && coupon.getGrantNumber() <= count) {
                map.put("success", false);
                map.put("failedType", 3);//券已派完
                map.put("message", "券已派完");
                return map;
            }
            CustomerCouponSearch record = new CustomerCouponSearch();
            record.setGrantNumber(coupon.getGrantNumber());
            record.setCouponCode(couponCode);
            record.setCustomerId(customerId);
            record.setReceiveType((byte) receiveType);
            record.setSendUser(sendUser);
            record.setUseStatus((byte) 0);
            record.setCreateTime(new Date());
            if (coupon.getValidityType() == 0) {//指定起止时间
                record.setUseStartTime(coupon.getUseStartTime());
                record.setUseEndTime(coupon.getUseEndTime());
            } else if (coupon.getValidityType() == 1) {//相对时间
                Date startDate = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);//设置起时间
                cal.add(Calendar.DATE, coupon.getRelativeDaysNum());//增加n天
                Date endDate = cal.getTime();
                record.setUseStartTime(startDate);
                record.setUseEndTime(endDate);
            }
            int result = customerCouponMapper.insertBySendCoupon(record);

            if (result == 1) {
                map.put("success", true);
                map.put("message", "领取成功");
            } else {
                map.put("success", false);
                map.put("failedType", 4);//券已派完
                map.put("message", "领取失败");
            }
        } catch (Exception e) {
            map.put("success", false);
            map.put("failedType", 4);//
            map.put("message", "领取失败");
            log.error("sendCouponSingle error，couponCode=" + couponCode + ",customerId=" + customerId + ",receiveType=" + receiveType, e);
        }
        return map;
    }

    /**
     * B端抵用券详情
     * 1、抵用券信息
     *
     * @param req
     * @return
     */
    @Override
    public Map getCouponDetail(CouponRequest req) {
        if (StringUtils.isBlank(req.getCouponCode())) {
            return null;
        }
        Map map = Maps.newHashMap();

        Coupon couponInfo = null;
        try {
            couponInfo = couponMapper.selectByCouponCode(req.getCouponCode());
        } catch (Exception e) {
            log.error("getCouponDetail error", e);
        }
        map.put("couponInfo", couponInfo);

        //限定范围
        if (couponInfo.getScopeType() == 2) {//限定分类
            CouponScopeCategoryExample example = new CouponScopeCategoryExample();
            CouponScopeCategoryExample.Criteria criteria = example.createCriteria();
            criteria.andCouponCodeEqualTo(req.getCouponCode());
            example.setOrderByClause("id desc");
            List<CouponScopeCategory> couponScopeCategories = couponScopeCategoryMapper.selectByExample(example);
            map.put("couponScopeCategories", couponScopeCategories);
        }
        return map;
    }


    /**
     * c端抵用券详情
     * 1、抵用券信息   2、 门店信息
     *
     * @param req
     * @return
     */
    @Override
    public Map getCouponDetailForClient(CouponRequest req, String customerId) {
        if (StringUtils.isBlank(req.getEncryptedCode())) {
            return null;
        }
        Map map = Maps.newHashMap();
        CouponItemResp couponItemResp = new CouponItemResp();
        try {
            Coupon couponInfo = couponMapper.selectByEncryptedCode(req.getEncryptedCode());
            if (couponInfo == null) {
                return map;
            }
            BeanUtils.copyProperties(couponInfo, couponItemResp);
        } catch (Exception e) {
            log.error("getCouponList error", e);
        }


        //获取抵用券已发送张数， couponCode:已发放或者领取张数
        List<String> couponCodeList = Lists.newArrayList();
        couponCodeList.add(couponItemResp.getCode());
        Map sendNumberMap = getUsedCouponNumberMap(couponCodeList);
         /*
                  获取优惠券剩余数量
                  grantNumber==-1: 领取张数不限
                  grantNumber>1 :  grantNumber - 已发送张数
         */
        Long leftNumber = -1L; //默认-1,表示剩余数量不限
        if (couponItemResp.getGrantNumber() >= 0) {
            Long sendNumber = (Long) sendNumberMap.get(couponItemResp.getCode()) == null ? 0 : (Long) sendNumberMap.get(couponItemResp.getCode());
            leftNumber = couponItemResp.getGrantNumber() - sendNumber;
            if (leftNumber < 0) {
                leftNumber = 0L;
            }
        }
        couponItemResp.setLeftCouponNumber(leftNumber);//剩余可领数量

        couponItemResp.setHasReceived(false);//是否已领取
        if (!StringUtils.isBlank(customerId)) {//为登录

            CustomerCouponExample example = new CustomerCouponExample();
            CustomerCouponExample.Criteria criteria = example.createCriteria();
            criteria.andCouponCodeEqualTo(couponItemResp.getCode());
            criteria.andCustomerIdEqualTo(customerId);
            List<CustomerCoupon> customerCoupons = customerCouponMapper.selectByExample(example);
            if (CollectionUtils.isNotEmpty(customerCoupons)) {
                couponItemResp.setHasReceived(true);//是否已领取
                couponItemResp.setUseEndTime(customerCoupons.get(0).getUseEndTime());
            }
        }
        couponItemResp.setCode(null);
        map.put("couponInfo", couponItemResp);
        return map;
    }

    /**
     * 判断当前用户是否领取过该券(不限制领取方式)
     *
     * @param couponInfo
     * @param customerId
     * @return
     */
    private int recievedCouponCount(Coupon couponInfo, String customerId) {
        if (StringUtils.isBlank(customerId)) {//为登录
            return 0;
        }
        CustomerCouponExample example = new CustomerCouponExample();
        CustomerCouponExample.Criteria criteria = example.createCriteria();
        criteria.andCouponCodeEqualTo(couponInfo.getCode());
        criteria.andCustomerIdEqualTo(customerId);
//        criteria.andReceiveTypeEqualTo((byte) 0);
        int customerReceiveCount = customerCouponMapper.countByExample(example);
        return customerReceiveCount;
    }


    /**
     * c端领券
     *
     * @param req
     * @return
     */
    @Override
    @Transactional
    public Map getCoupon(CouponRequest req, String customerId) {
        Map map = Maps.newHashMap();
        Coupon couponInfo = couponMapper.selectByEncryptedCode(req.getEncryptedCode());
        /*
          1、券本身状态，是否允许领取券
          不存在 券禁用  券不允许领取  指定起止时间券，时间已超过结束时间
         */
        map = judgeCouponCanReveice(couponInfo);
        if (map.get("success") != null && !(Boolean) map.get("success")) {
            return map;
        }

        /*
        2、已领取过该券,主动在线领取的数量只能为1
         */
        String lockKey = personalCouponGetNumberPrefix + "_" + req.getEncryptedCode() + "_" + customerId;
        String cacheCountStr = redisTemplate.opsForValue().get(lockKey);//缓存中，已领取数量
        if (StringUtils.isBlank(cacheCountStr)) {//缓存中没值，查数据库判断是否已经领取过
            int customerReceivedCount = recievedCouponCount(couponInfo, customerId);
            if (customerReceivedCount > 0) {
                map.put("success", false);
                map.put("resultType", 4003);//领取过该券
                map.put("message", "你已领取过该券");
                return map;
            }
        } else if (Integer.valueOf(cacheCountStr) > 0) {//缓存中已经有值，说明领取过
            map.put("success", false);
            map.put("resultType", 4003);//领取过该券
            map.put("message", "你已领取过该券");
            return map;
        }

        /*
           防止用户并发性发出重复请求
        */
        Long cacheCount = redisTemplate.opsForValue().increment(lockKey, 1L);
        redisTemplate.expire(lockKey, 3, TimeUnit.DAYS);
        if (cacheCount > 1) {
            map.put("success", false);
            map.put("resultType", 4003);//已领取过，重复操作，同时发送多次领取请求
            map.put("message", "请稍后再试");
            redisTemplate.opsForValue().increment(lockKey, -1L);
            return map;
        }

        /*
        领券
         */
        Customer customer = new Customer(customerId);
        Map singleResult = sendCoupon(couponInfo, customer, "", 0);
        if (singleResult != null && singleResult.get("success") != null && !(Boolean) singleResult.get("success")) {//insert失败，cache值 -1
            redisTemplate.opsForValue().increment(lockKey, -1L);
        }
        return singleResult;
    }

    @Autowired
    private StoreInfoClient storeInfoClient;

    /**
     * 对外开放获取优惠券信息
     *
     * @param code
     * @return
     */
    @Override
    public CouponItemResp openGetCouponInfo(String code) {
        log.info("openGetCouponInfo-> req  {}", code);
        CouponItemResp result = null;
        CustomerCouponExample customerCouponExample = new CustomerCouponExample();
        CustomerCouponExample.Criteria customerCouponExampleCriteria = customerCouponExample.createCriteria();
        customerCouponExampleCriteria.andCodeEqualTo(code);
        List<CustomerCoupon> customerCoupons = customerCouponMapper.selectByExample(customerCouponExample);
        if (CollectionUtils.isNotEmpty(customerCoupons)) {
            CustomerCoupon customerCoupon = customerCoupons.get(0);
            CouponExample example = new CouponExample();
            CouponExample.Criteria criteria = example.createCriteria();
            criteria.andCodeEqualTo(customerCoupon.getCouponCode());
            List<Coupon> coupons = couponMapper.selectByExample(example);
            if (CollectionUtils.isNotEmpty(coupons)) {
                result = new CouponItemResp();
                Coupon coupon = coupons.get(0);
                BeanUtils.copyProperties(coupon, result);
                result.setCustomerCouponStatus(customerCoupon.getUseStatus());
                if (customerCoupon.getUseStatus() != Byte.valueOf((byte) 1) && customerCoupon.getUseEndTime().getTime() < System.currentTimeMillis()) {
                    result.setCustomerCouponStatus(Byte.valueOf((byte) -1));
                }
                result.setUseStartTime(customerCoupon.getUseStartTime());
                result.setUseEndTime(customerCoupon.getUseEndTime());
                //补充门店信息
                ClientStoreVO clientStoreVO = new ClientStoreVO();
                clientStoreVO.setStoreId(coupon.getStoreId());
                clientStoreVO.setTenantId(coupon.getTenantId());
                BizBaseResponse<ClientStoreDTO> resultData = storeInfoClient.getStoreInfoForClient(clientStoreVO);
                if (resultData != null && resultData.getData() != null) {
                    CouponItemResp.StoreInfo storeInfo = new CouponItemResp.StoreInfo();
                    storeInfo.setAddress(resultData.getData().getAddress());
                    storeInfo.setStoreName(resultData.getData().getStoreName());
                    storeInfo.setLat(resultData.getData().getLat());
                    storeInfo.setLon(resultData.getData().getLon());
                    storeInfo.setOpeningEffectiveDate(resultData.getData().getOpeningEffectiveDate());
                    storeInfo.setOpeningExpiryDate(resultData.getData().getOpeningExpiryDate());
                    storeInfo.setMobilePhone(resultData.getData().getMobilePhone());
                    result.setStoreInfo(storeInfo);
                }
            }
        }
        return result;
    }

    @Override
    public CouponResp openGetCouponDetail(String code) {
        CouponExample couponExample = new CouponExample();
        couponExample.createCriteria().andCodeEqualTo(code);
        List<Coupon> couponList = couponMapper.selectByExample(couponExample);
        CouponResp resp = new CouponResp();
        if (null != couponList && !couponList.isEmpty()) {
            Coupon coupon = couponList.get(0);
            BeanUtils.copyProperties(coupon, resp);
            resp.setType(coupon.getType().intValue());
            resp.setValidityType(coupon.getValidityType().intValue());
            resp.setStatus(coupon.getStatus().intValue());
            resp.setAllowGet(coupon.getAllowGet().intValue());
            resp.setScopeType(coupon.getScopeType().intValue());
            //补充门店信息
            ClientStoreVO clientStoreVO = new ClientStoreVO();
            clientStoreVO.setStoreId(coupon.getStoreId());
            clientStoreVO.setTenantId(coupon.getTenantId());
            BizBaseResponse<ClientStoreDTO> resultData = storeInfoClient.getStoreInfoForClient(clientStoreVO);
            if (resultData != null && resultData.getData() != null) {
                CouponResp.StoreInfo storeInfo = new CouponResp.StoreInfo();
                storeInfo.setAddress(resultData.getData().getAddress());
                storeInfo.setStoreName(resultData.getData().getStoreName());
                storeInfo.setLat(resultData.getData().getLat());
                storeInfo.setLon(resultData.getData().getLon());
                storeInfo.setOpeningEffectiveDate(resultData.getData().getOpeningEffectiveDate());
                storeInfo.setOpeningExpiryDate(resultData.getData().getOpeningExpiryDate());
                storeInfo.setMobilePhone(resultData.getData().getMobilePhone());
                resp.setStoreInfo(storeInfo);
            }
        }
        log.info("openGetCouponDetail -> response：{}", GsonTool.toJSONString(resp));
        return resp;
    }

    @Override
    public CustomerCouponPO getCouponDetailv2(CouponRequest req) {
        log.info("getCouponDetailv2-> {} ", req);
        CustomerCouponPO result = null;
        CustomerCouponExample customerCouponExample = new CustomerCouponExample();
        CustomerCouponExample.Criteria customerCouponExampleCriteria = customerCouponExample.createCriteria();
        customerCouponExampleCriteria.andCodeEqualTo(req.getCustomerCouponCode());
        List<CustomerCoupon> customerCoupons = customerCouponMapper.selectByExample(customerCouponExample);
        if (CollectionUtils.isNotEmpty(customerCoupons)) {
            CustomerCoupon customerCoupon = customerCoupons.get(0);
            result = new CustomerCouponPO();
            BeanUtils.copyProperties(customerCoupon, result);

            CouponExample example = new CouponExample();
            CouponExample.Criteria criteria = example.createCriteria();
            criteria.andCodeEqualTo(customerCoupon.getCouponCode()).andStoreIdEqualTo(req.getStoreId())
                    .andTenantIdEqualTo(req.getTenantId());
            List<Coupon> coupons = couponMapper.selectByExample(example);
            if (CollectionUtils.isNotEmpty(coupons)) {
                Coupon coupon = coupons.get(0);
                CouponPO po = new CouponPO();
                BeanUtils.copyProperties(coupon, po);
                result.setCouponInfo(po);
                //获取客户信息
                if (StringUtils.isNotBlank(customerCoupon.getCustomerId())) {
                    BaseIdReqVO baseIdReqVO = new BaseIdReqVO();
                    baseIdReqVO.setId(customerCoupon.getCustomerId());
                    baseIdReqVO.setStoreId(req.getStoreId());
                    baseIdReqVO.setTenantId(req.getTenantId());
                    BizBaseResponse<CustomerDTO> crmResult = customerClient.getCustomerById(baseIdReqVO);
                    if (crmResult != null && crmResult.getData() != null) {
                        result.setCustomerName(crmResult.getData().getName());
                        result.setCustomerTelephone(crmResult.getData().getPhoneNumber());
                    }
                }
                //获取发券人
                if (StringUtils.isNotBlank(customerCoupon.getSendUser())) {
                    BizBaseResponse<Map<String, UserDTO>> crmResult = customerClient.getUserInfoMapByIdList(Lists.newArrayList(customerCoupon.getSendUser()));
                    result.setSendUserName(crmResult != null
                            && crmResult.getData() != null
                            && crmResult.getData().containsKey(customerCoupon.getSendUser())
                            ? crmResult.getData().get(customerCoupon.getSendUser()).getUsername() : null);
                }
                //获取过期状态
                Date date = new Date();
                if (customerCoupon.getUseEndTime() != null && date.after(customerCoupon.getUseEndTime())) {
                    result.setUseStatus((byte) -1);
                }

            } else {
                throw new StoreSaasMarketingException("查询不到该优惠券信息");
            }
        }
        return result;
    }

    /**
     * 对外获取优惠券CODE
     *
     * @param phone
     * @param
     * @return
     */

    @Override
    public byte[] openGetCustomerCouponCodeByPhone(String phone, String code) throws Exception {
        log.info("openGetCustomerCouponCodeByPhone -> {}", phone, code);
        String result = null;
        CustomerCouponExample customerCouponExample = new CustomerCouponExample();
        CustomerCouponExample.Criteria customerCouponExampleCriteria = customerCouponExample.createCriteria();
        customerCouponExampleCriteria.andCodeEqualTo(code);
        List<CustomerCoupon> customerCoupons = customerCouponMapper.selectByExample(customerCouponExample);
        if (CollectionUtils.isNotEmpty(customerCoupons)) {
            CustomerCoupon customerCoupon = customerCoupons.get(0);
            BaseIdReqVO vo = new BaseIdReqVO();
            vo.setId(customerCoupon.getCustomerId());
            BizBaseResponse<CustomerDTO> crmResult = customerClient.getCustomerById(vo);
            if (crmResult != null && crmResult.getData() != null && crmResult.getData().getPhoneNumber().equals(phone)) {
                result = " { \"code\": \"" + customerCoupon.getCode().concat("\", \"type\":1 }");
            } else {
                throw new StoreSaasMarketingException("用户数据校验失败");
            }
        }
        return QrCode.getQRCodeImage(result, 500, 500);
    }

    @Override
    public Integer openGetUseStatusByCode(String code) throws InterruptedException {
        Integer result = null;
        for (int i = 0; null == result && i < 5; i++) {
            if (redisTemplate.opsForHash().hasKey("WRITEOFFMAP", code)) {
                String status = (String) redisTemplate.opsForHash().get("WRITEOFFMAP", code);
                result = Integer.parseInt(status);
                redisTemplate.opsForHash().delete("WRITEOFFMAP", code);
            } else {
                Thread.sleep(1000);
            }
        }
        if (null == result) {
            if (code.startsWith("YHQ")) {
                CustomerCouponExample example = new CustomerCouponExample();
                example.createCriteria().andCodeEqualTo(code);
                List<CustomerCoupon> coupons = customerCouponMapper.selectByExample(example);
                if (null != coupons && !coupons.isEmpty()) {
                    result = coupons.get(0).getUseStatus().intValue();
                } else {
                    throw new StoreSaasMarketingException("该优惠券不存在");
                }
            } else if (code.startsWith("YXHD")) {
                ActivityCustomerExample activityCustomerExample = new ActivityCustomerExample();
                activityCustomerExample.createCriteria().andActivityOrderCodeEqualTo(code);
                List<ActivityCustomer> activityCustomers = activityCustomerMapper.selectByExample(activityCustomerExample);
                if (null != activityCustomers && !activityCustomers.isEmpty()) {
                    result = activityCustomers.get(0).getUseStatus().intValue();
                } else {
                    throw new StoreSaasMarketingException("该营销活动不存在");
                }
            } else {
                throw new StoreSaasMarketingException("code不符合编码规范");
            }
        }
        return result;
    }

    @Override
    public CustomerCouponPageResp getMyCouponList(CouponReceiveRecordRequest req, String customerId) {
        if (req.getStoreId() == null || StringUtils.isBlank(customerId)) {
            return null;
        }
        CustomerCouponPageResp customerCouponPageResp = new CustomerCouponPageResp();
        Page<CustomerCouponPO> customerCouponPosPage = new Page<>();
        customerCouponPageResp.setCustomerCouponPOS(customerCouponPosPage);

        CustomerCouponSearch record = new CustomerCouponSearch();
        record.setUseStatus(req.getUseStatus());
        record.setCustomerId(customerId);
        record.setStoreId(req.getStoreId());

        PageHelper.startPage(req.getPageNum() + 1, req.getPageSize());

        List<CustomerCouponPO> recordList = null;
        try {
            recordList = customerCouponMapper.selectMyRecordList(record);
        } catch (Exception e) {
            log.error("getMyCouponList error", e);
        }

        //设置优惠券信息
        setCouponInfoForCustomerCouponList(req.getStoreId(), recordList);

        customerCouponPosPage.addAll(recordList);
        PageInfo<CustomerCouponPO> customerCouponPOPageInfo = new PageInfo<>(recordList);
        CustomerCouponPageResp.PageInfo pageInfo = new CustomerCouponPageResp.PageInfo();
        pageInfo.setTotal(customerCouponPOPageInfo.getTotal());
        pageInfo.setPages(customerCouponPOPageInfo.getPages());
        pageInfo.setPageNum(req.getPageNum());
        pageInfo.setPageSize(req.getPageSize());
        customerCouponPageResp.setPageInfo(pageInfo);
        if (pageInfo.getPageNum() >= pageInfo.getPages()) {
            customerCouponPageResp.setCustomerCouponPOS(null);
        }

        return customerCouponPageResp;
    }

    /**
     * 获取优惠券基本信息
     *
     * @param storeId
     * @param recordList
     */
    private void setCouponInfoForCustomerCouponList(Long storeId, List<CustomerCouponPO> recordList) {
        if (CollectionUtils.isNotEmpty(recordList)) {
            HashSet<String> coupondeSet = Sets.newHashSet();
            recordList.forEach(recordItem -> {
                coupondeSet.add(recordItem.getCouponCode());
            });

            List<Coupon> couponList = getCouponInfoListByCouponCodeList(storeId, coupondeSet);
            Map<String, Coupon> couponMap = Maps.newHashMap();
            List<String> limitCategoryCouponCodeList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(couponList)) {
                couponList.forEach(coupon -> {
                    if (coupon.getScopeType() == 2) {//限制分类
                        limitCategoryCouponCodeList.add(coupon.getCode());
                    }
                    couponMap.put(coupon.getEncryptedCode(), coupon);
                });
            }

            /**
             * 限定分类信息
             */
            Map<String, List<CouponScopeCategory>> couponScopeMap = getCouponScopeCategories(limitCategoryCouponCodeList);

            recordList.forEach(recordItem -> {
                CouponPO po = new CouponPO();
                Coupon coupon = couponMap.get(recordItem.getEncryptedCode());
                BeanUtils.copyProperties(coupon, po);
                po.setCouponScopeCategories(couponScopeMap.get(po.getCode()));
                po.setCode(null);
                recordItem.setCouponInfo(po);
                recordItem.setCouponCode(null);
            });
        }
    }

    /**
     * 获取每张券的类目限制信息
     *
     * @param couponCodeList
     * @return
     */
    private Map<String, List<CouponScopeCategory>> getCouponScopeCategories(List<String> couponCodeList) {
        if (CollectionUtils.isEmpty(couponCodeList)) {
            return Maps.newHashMap();
        }
        CouponScopeCategoryExample example = new CouponScopeCategoryExample();
        CouponScopeCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andCouponCodeIn(couponCodeList);
        example.setOrderByClause("coupon_code desc");
        List<CouponScopeCategory> couponScopeCategories = couponScopeCategoryMapper.selectByExample(example);
        Map<String, List<CouponScopeCategory>> couponScopeMap = Maps.newHashMap();
        //todo
/*        if (CollectionUtils.isNotEmpty(couponScopeCategories)) {
            Long tenantId = couponScopeCategories.get(0).getTenantId();
            Long storeId = couponScopeCategories.get(0).getStoreId();
            Map<String, BusinessCateGoryRespDTO> businessCateGoryRespDTOSMap = Maps.newHashMap();
            List<BusinessCateGoryRespDTO> businessCateGoryRespDTOS = getBusinessCateGoryRespDTOSFromCache(tenantId, storeId);
            if (CollectionUtils.isNotEmpty(businessCateGoryRespDTOS)) {
                businessCateGoryRespDTOS.forEach(businessCateGoryRespDTO -> {
                    businessCateGoryRespDTOSMap.put(businessCateGoryRespDTO.getCode(), businessCateGoryRespDTO);
                });
            }

            couponScopeCategories.forEach(couponScopeCategory -> {
                String couponCode = couponScopeCategory.getCouponCode();
                couponScopeCategory.setCategoryName(businessCateGoryRespDTOSMap.get(couponScopeCategory.getCategoryCode()) == null ? "" : businessCateGoryRespDTOSMap.get(couponScopeCategory.getCategoryCode()).getName());
                if (!couponScopeMap.containsKey(couponCode)) {
                    couponScopeMap.put(couponCode, Lists.newArrayList());
                }
                couponScopeMap.get(couponCode).add(couponScopeCategory);
            });
        }*/

        return couponScopeMap;

    }

    /**
     * 每个门店的分类信息存redis
     *
     * @param storeId
     * @param tenantId
     * @return
     */
/*    private List<BusinessCateGoryRespDTO> getBusinessCateGoryRespDTOSFromCache(Long storeId, Long tenantId) {
        List<BusinessCateGoryRespDTO> obj = JSONArray.parseArray(redisTemplate.opsForValue().get(BUSSINESS_CATEGORY_DTOS_PREFIX + storeId + tenantId), BusinessCateGoryRespDTO.class);
        if (obj == null) {
            obj = bussinessCategoryService.showBusinessCategory(tenantId, storeId);
            redisTemplate.opsForValue().set(BUSSINESS_CATEGORY_DTOS_PREFIX + storeId + tenantId, JSONObject.toJSONString(obj), 1, TimeUnit.DAYS);
        }

        return obj;
    }*/


    /**
     * 根据d多个优惠券码查询优惠券信息
     */
    private List<Coupon> getCouponInfoListByCouponCodeList(Long storeId, HashSet<String> couponCodeSet) {
        CouponExample example = new CouponExample();
        CouponExample.Criteria criteria = example.createCriteria();
        criteria.andCodeIn(new ArrayList<>(couponCodeSet));
        criteria.andStoreIdEqualTo(storeId);
        List<Coupon> couponList = couponMapper.selectByExample(example);
        return couponList;
    }

    /**
     * 判断券是否可以领取
     *
     * @param couponInfo
     * @return
     */
    private Map judgeCouponCanReveice(Coupon couponInfo) {
        Map map = Maps.newHashMap();
        map.put("success", true);
        if (couponInfo == null) {
            map.put("success", false);
            map.put("resultType", 4001);//不允许领取
            map.put("message", "券不存在");
            return map;
        } else if (couponInfo.getStatus() == 0) { //禁用不能发券，不能领券
            map.put("success", false);
            map.put("resultType", 4001);//不允许领取
            map.put("message", "券禁用");
            return map;
        } else if (couponInfo.getAllowGet() == 0) {
            map.put("success", false);
            map.put("resultType", 4001);//不允许领取
            map.put("message", "券不允许领取");
            return map;
        } else {
            if (couponInfo != null && couponInfo.getValidityType() == 0) {//指定有效期起止时间
                Date endTime = couponInfo.getUseEndTime();
                Date now = new Date();
                if (endTime.before(now)) {
                    map.put("success", false);
                    map.put("resultType", 4001);//不允许领取
                    map.put("message", "券已过期");
                    return map;
                }
            }
        }
        return map;
    }


    /**
     * 保存二维码图片url到数据库
     *
     * @param couponId
     * @param qrUrl
     */
    private void saveQrUrlToDatabase(Long couponId, String qrUrl) {
        try {
            if (StringUtils.isNotBlank(qrUrl)) {
                Coupon record = new Coupon();
                record.setId(couponId);
                record.setWeixinQrUrl(qrUrl);
                int result = couponMapper.updateByPrimaryKeySelective(record);
            }
        } catch (Exception e) {
            log.error("saveQrUrlToDatabase error:", e);
        }
    }

}
