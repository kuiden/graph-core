package com.tuhu.store.saas.marketing.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.springcloud.common.util.RedisUtils;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdsReqVO;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.enums.CouponScopeTypeEnum;
import com.tuhu.store.saas.marketing.enums.CouponTypeEnum;
import com.tuhu.store.saas.marketing.enums.CouponValidityTypeEnum;
import com.tuhu.store.saas.marketing.enums.CrmReturnCodeEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CouponMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CouponScopeCategoryMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CustomerCouponMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.OrderCouponMapper;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.response.CouponScopeCategoryResp;
import com.tuhu.store.saas.marketing.response.dto.*;
import com.tuhu.store.saas.marketing.service.ICouponService;
import com.tuhu.store.saas.marketing.service.ICustomerMarketingService;
import com.tuhu.store.saas.marketing.service.IMCouponService;
import com.tuhu.store.saas.marketing.util.*;
import com.tuhu.store.saas.marketing.request.vo.ServiceOrderCouponUseVO;
import com.tuhu.store.saas.marketing.request.vo.ServiceOrderCouponVO;
import com.tuhu.store.saas.marketing.request.vo.ServiceOrderItemVO;
import com.tuhu.store.saas.marketing.response.CommonResp;
import com.tuhu.store.saas.marketing.response.CouponResp;
import com.tuhu.store.saas.marketing.response.CouponStatisticsForCustomerMarketResp;
import com.xiangyun.versionhelper.VersionHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CouponServiceImpl implements ICouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private CouponScopeCategoryMapper couponScopeCategoryMapper;

    @Autowired
    private CodeFactory codeFactory;

    @Autowired
    private CustomerCouponMapper customerCouponMapper;

    /**
     * 优惠券发放数量缓存
     */
    private static final String couponSendNumberPrefix = "COUPON:SENDNUMBER:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    //@Autowired
    //private CustomerMapper customerMapper;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private OrderCouponMapper orderCouponMapper;

    @Autowired
    private IMCouponService imCouponService;

    @Override
    @Transactional
    public AddCouponReq addNewCoupon(AddCouponReq addCouponReq) {
        //校验输入
        String validateResult = this.validateAddCouponReq(addCouponReq);
        if (null != validateResult) {
            throw new StoreSaasMarketingException(validateResult);
        }
        Long storeId = addCouponReq.getStoreId();
        Coupon coupon = convertToCoupon(addCouponReq);
        //生成优惠券编码
        String codeNumber = codeFactory.getCodeNumber(CodeFactory.couponRedisPrefix, storeId);
        String code = codeFactory.generateCouponCode(storeId, codeNumber);
        coupon.setCode(code);
        //生成优惠券编码的密文
        String encryptedCode = Md5Util.md5(code, CodeFactory.codeSalt);
        coupon.setEncryptedCode(encryptedCode);
        couponMapper.insertSelective(coupon);
//        threadPoolTaskExecutor.submit(() -> {
//            //生成分享二维码
//            this.getQrCodeForCoupon(coupon.getId(),encryptedCode);
//        });
//        //如果优惠券适用范围做了限定
//        CouponScopeTypeEnum scopeTypeEnum = CouponScopeTypeEnum.getEnumByCode(addCouponReq.getScopeType());
//        if (CouponScopeTypeEnum.Category.equals(scopeTypeEnum)) {
//            List<CouponScopeCategoryReq> categories = addCouponReq.getCategories();
//            List<CouponScopeCategory> scopeCategoryList = convertToCouponScopeCategory(code, categories, addCouponReq);
//            couponScopeCategoryMapper.insertBatch(scopeCategoryList);
//        }
        //如果不是不限数量，则缓存券发放数量
        if (coupon.getGrantNumber().compareTo(0L) > 0) {
            String key = couponSendNumberPrefix.concat(code);
            redisTemplate.opsForValue().increment(key, 0L);
        }
        return addCouponReq;
    }

    /*
     * 生成优惠券分享二维码
     */
    private String getQrCodeForCoupon(Long couponId, String encryptedCode) {
        QrCodeRequest qrCodeRequest = new QrCodeRequest();
        qrCodeRequest.setCouponId(couponId);
        qrCodeRequest.setWidth(250L);
        qrCodeRequest.setPath("pages/drawCoupon/drawCoupon");
        qrCodeRequest.setScene("encryptedCode=" + encryptedCode);
        return imCouponService.getQrCodeForCoupon(qrCodeRequest);
    }


    /**
     * @param couponCode
     * @param categories
     * @param addCouponReq
     */
    private List<CouponScopeCategory> convertToCouponScopeCategory(String couponCode, List<CouponScopeCategoryReq> categories, AddCouponReq addCouponReq) {
        if (null == couponCode || CollectionUtils.isEmpty(categories)) {
            return null;
        }
        Long storeId = addCouponReq.getStoreId();
        Long tenantId = addCouponReq.getTenantId();
        List<CouponScopeCategory> scopeCategoryList = new ArrayList<>();
        for (CouponScopeCategoryReq scopeCategoryReq : categories) {
            CouponScopeCategory scopeCategory = new CouponScopeCategory();
            scopeCategory.setStoreId(storeId);
            scopeCategory.setTenantId(tenantId);
            scopeCategory.setCouponCode(couponCode);
            scopeCategory.setCategoryCode(scopeCategoryReq.getCategoryCode());
            scopeCategoryList.add(scopeCategory);
        }
        return scopeCategoryList;
    }

    /**
     * 生成优惠券活动对象
     *
     * @param addCouponReq
     */
    private Coupon convertToCoupon(AddCouponReq addCouponReq) {
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(addCouponReq, coupon);
        coupon.setType(addCouponReq.getType().byteValue());
        coupon.setValidityType(addCouponReq.getValidityType().byteValue());
        coupon.setStatus(addCouponReq.getStatus().byteValue());
        //禁用状态时自动禁止领券
        if (coupon.getStatus().equals((byte) 0)) {
            coupon.setAllowGet((byte) 0);
        } else {
            coupon.setAllowGet(addCouponReq.getAllowGet().byteValue());
        }
        coupon.setScopeType(addCouponReq.getScopeType().byteValue());
        coupon.setCreateUser(addCouponReq.getUserId());
        Date date = new Date();
        coupon.setCreateTime(date);
        coupon.setUpdateTime(date);
        return coupon;
    }

    /**
     * 校验新增优惠券活动的入参
     *
     * @param addCouponReq
     * @return
     */
    private String validateAddCouponReq(AddCouponReq addCouponReq) {
        if (null == addCouponReq) {
            return CrmReturnCodeEnum.REQUEST_ARG_IS_EMPTY.getDesc();
        }
        //使用门槛
        BigDecimal conditionLimit = addCouponReq.getConditionLimit();
        if (null == conditionLimit) {
            conditionLimit = new BigDecimal("-1");
            addCouponReq.setConditionLimit(conditionLimit);
        }
//        if (!checkPattern("^((-1)|([1-9]\\d*00))$", conditionLimit.toString())) {
//            return "优惠券使用门槛只能为不限金额或限制金额:正整数";
//        }
        //优惠金额
        BigDecimal contentValue = addCouponReq.getContentValue();
        //优惠券优惠金额不能为空
        Integer type = addCouponReq.getType();
        CouponTypeEnum couponTypeEnum = CouponTypeEnum.getEnumByCode(type);
        if (null == couponTypeEnum) {
            return "优惠券类型无效";
        }
        if (CouponTypeEnum.Money.equals(couponTypeEnum)) {
            if (null == contentValue) {
                return "代金券优惠金额不能为空";
//            } else if (!checkPattern("^[1-9]\\d*00$", contentValue.toString())) {
//                return "代金券优惠金额只能为正整数";
            }
            //如果有使用门槛
            if (conditionLimit.compareTo(BigDecimal.ZERO) > 0) {
                if (contentValue.compareTo(conditionLimit) > 0) {
                    return "使用门槛不能小于优惠金额";
                }
            }
        }
        //折扣率
        BigDecimal discountValue = addCouponReq.getDiscountValue();
        if (CouponTypeEnum.Percentage.equals(couponTypeEnum)) {
            if (null == discountValue) {
                return "折扣券折扣数不能为空";
            } else if (!checkPattern("^(\\d|[1-9]\\d)$", discountValue.toString())) {
                return "折扣券折扣数只支持输入0-9.9数字";
            }
        }

        //适用范围不能为空
        Integer scopeType = addCouponReq.getScopeType();
        CouponScopeTypeEnum scopeTypeEnum = CouponScopeTypeEnum.getEnumByCode(scopeType);
        if (null == scopeTypeEnum) {
            return "优惠券适用范围无效";
        }
        if (CouponScopeTypeEnum.Category.equals(scopeTypeEnum)) {
            if (CollectionUtils.isEmpty(addCouponReq.getCategories())) {
                return "适用范围为限定分类时，分类不能为空";
            }
        }
        //有效期
        Integer validityType = addCouponReq.getValidityType();
        CouponValidityTypeEnum validityTypeEnum = CouponValidityTypeEnum.getEnumByCode(validityType);
        if (null == validityTypeEnum) {
            return "优惠券有效期类型无效";
        }
        if (CouponValidityTypeEnum.Fixed.equals(validityTypeEnum)) {
            Date useStartTime = addCouponReq.getUseStartTime();
            Date useEndTime = addCouponReq.getUseEndTime();
            if (null == useStartTime || null == useEndTime) {
                return "优惠券有效期不能为空";
            }
            if (useEndTime.compareTo(useStartTime) <= 0) {
                return "优惠券有效期-结束日期不能早于开始日期";
            }
            if (useEndTime.compareTo(new Date()) <= 0) {
                return "优惠券有效期-结束日期不能早于当前日期";
            }
        }
        if (CouponValidityTypeEnum.Relative.equals(validityTypeEnum)) {
            if (null == addCouponReq.getRelativeDaysNum() || addCouponReq.getRelativeDaysNum() <= 0) {
                return "领券后有效天数只能为正整数";
            }
        }
        //券数量
        Long grantNumber = addCouponReq.getGrantNumber();
        if (null == grantNumber) {
            grantNumber = -1L;
            addCouponReq.setGrantNumber(grantNumber);
        }
        if (grantNumber.compareTo(0L) < 0 && !grantNumber.equals(-1L)) {
            return "券数量只能为不限或限制（正整数）";
        }
        Integer status = addCouponReq.getStatus();
        if (status.intValue() != 0 && status.intValue() != 1) {
            return "券状态格式错误";
        }
        Integer allowGet = addCouponReq.getAllowGet();
        if (allowGet.intValue() != 0 && allowGet.intValue() != 1) {
            return "允许领券格式错误";
        }

        /**
         * 校验优惠券名称
         */
        /*String tilte = addCouponReq.getTitle();
        CouponExample couponExample = new CouponExample();
        CouponExample.Criteria couponCriteria = couponExample.createCriteria();
        couponCriteria.andTitleEqualTo(tilte);
        couponCriteria.andStoreIdEqualTo(addCouponReq.getStoreId());
        int count = couponMapper.countByExample(couponExample);
        if (count > 0) {
            return String.format("优惠券[%s]已存在同名的记录", tilte);
        }*/
        return null;
    }

    /**
     * 检查指定的内容是否符合正则表达式
     *
     * @param patten
     * @param content
     * @return
     */
    private boolean checkPattern(String patten, String content) {
        return Pattern.matches(patten, content);
    }

    @Override
    public CouponResp getCouponDetailById(Long couponId) {
        log.info("查询优惠券详情请求couponId：{}", couponId);
        if (null == couponId || couponId <= 0) {
            throw new StoreSaasMarketingException("非法的优惠券ID");
        }
        Coupon coupon = couponMapper.selectByPrimaryKey(couponId);
        CouponResp resp = new CouponResp();
        if (null != coupon) {
            BeanUtils.copyProperties(coupon, resp);
            resp.setType(coupon.getType().intValue());
            resp.setValidityType(coupon.getValidityType().intValue());
            resp.setStatus(coupon.getStatus().intValue());
            resp.setAllowGet(coupon.getAllowGet().intValue());
            resp.setScopeType(coupon.getScopeType().intValue());
            //统计已发放数量
            CustomerCouponExample customerCouponExample = new CustomerCouponExample();
            CustomerCouponExample.Criteria criteria = customerCouponExample.createCriteria();
            criteria.andCouponCodeEqualTo(coupon.getCode());
            int sendCount = customerCouponMapper.countByExample(customerCouponExample);
            resp.setSendNumber(Long.valueOf(sendCount + ""));
            resp.setIsMarketingCoupon(customerMarketingService.customerMarketingContainsCoupon(resp.getId(), resp.getTenantId(), resp.getStoreId()));
//            //未获取到分享二维码，则同步生成
//            if (null == coupon.getWeixinQrUrl()){
//                String url = this.getQrCodeForCoupon(couponId, coupon.getEncryptedCode());
//                resp.setWeixinQrUrl(url);
//            }
        }
//        Byte scopeType = coupon.getScopeType();
//        if (CouponScopeTypeEnum.Category.value().equals(scopeType)) {
//            //查询限定的分类
//            CouponScopeCategoryExample couponScopeCategoryExample = new CouponScopeCategoryExample();
//            CouponScopeCategoryExample.Criteria scopeCategoryCriteria = couponScopeCategoryExample.createCriteria();
//            scopeCategoryCriteria.andCouponCodeEqualTo(coupon.getCode());
//            List<CouponScopeCategory> scopeCategoryList = couponScopeCategoryMapper.selectByExample(couponScopeCategoryExample);
//            if (CollectionUtils.isNotEmpty(scopeCategoryList)) {
//                List<CouponScopeCategoryResp> scopeCategoryRespList = new ArrayList<>();
//                scopeCategoryList.forEach(scopeCategory -> {
//                    CouponScopeCategoryResp scopeCategoryResp = new CouponScopeCategoryResp();
//                    BeanUtils.copyProperties(scopeCategory, scopeCategoryResp);
//                    scopeCategoryRespList.add(scopeCategoryResp);
//                });
//                resp.setCategories(scopeCategoryRespList);
//            }
//        }
        log.info("查询优惠券详情响应response：{}", GsonTool.toJSONString(resp));
        return resp;
    }

    @Override
    public PageInfo<CouponResp> listCoupon(CouponListReq couponListReq) {
        log.info("查询优惠券列表请求request：{}", GsonTool.toJSONString(couponListReq));
        PageInfo<CouponResp> couponRespPageInfo = new PageInfo<>();
        CouponExample couponExample = new CouponExample();
        CouponExample.Criteria couponCriteria = couponExample.createCriteria();
        if (StringUtils.isNotBlank(couponListReq.getTitle())) {
            couponCriteria.andTitleLike("%".concat(couponListReq.getTitle()).concat("%"));
        }
        if (null != couponListReq.getStatus()) {
            couponCriteria.andStatusEqualTo(couponListReq.getStatus().byteValue());
        }
        couponCriteria.andStoreIdEqualTo(couponListReq.getStoreId());
        couponCriteria.andTenantIdEqualTo(couponListReq.getTenantId());
        couponExample.setOrderByClause("update_time desc");
        PageHelper.startPage(couponListReq.getPageNum() + 1, couponListReq.getPageSize());
        List<Coupon> couponList = couponMapper.selectByExample(couponExample);
        PageInfo<Coupon> couponPageInfo = new PageInfo<>(couponList);
        BeanUtils.copyProperties(couponPageInfo, couponRespPageInfo);
        List<CouponResp> couponRespList = new ArrayList<>(couponList.size());
        if (!CollectionUtils.isEmpty(couponList)) {
            //优惠券code集合
            List<String> couponCodeList = couponList.stream().map(Coupon::getCode).collect(Collectors.toList());
            //查询优惠券发放情况
            List<Map<String, Object>> grantNumberMapList = customerCouponMapper.countGrantNumberByCouponCodeList(couponCodeList, null);
            Map<String, Long> grantNumberMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(grantNumberMapList)) {
                grantNumberMapList.forEach(grantNumberMapElement -> {
                    Object couponCodeObj = grantNumberMapElement.get("couponCode");
                    if (null != couponCodeObj) {
                        String couponCode = String.valueOf(couponCodeObj);
                        Object grantNumberObj = grantNumberMapElement.get("number");
                        Long grantNumber = 0L;
                        if (null != grantNumberObj) {
                            grantNumber = Long.valueOf(String.valueOf(grantNumberObj));
                        }
                        grantNumberMap.put(couponCode, grantNumber);
                    }
                });
            }
            //查询优惠券使用情况
            List<Map<String, Object>> usedNumberMapList = customerCouponMapper.countGrantNumberByCouponCodeList(couponCodeList, Integer.valueOf(1));
            Map<String, Long> usedNumberMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(usedNumberMapList)) {
                usedNumberMapList.forEach(usedNumberMapElement -> {
                    Object couponCodeObj = usedNumberMapElement.get("couponCode");
                    if (null != couponCodeObj) {
                        String couponCode = String.valueOf(couponCodeObj);
                        Object usedNumberObj = usedNumberMapElement.get("number");
                        Long usedNumber = 0L;
                        if (null != usedNumberObj) {
                            usedNumber = Long.valueOf(String.valueOf(usedNumberObj));
                        }
                        usedNumberMap.put(couponCode, usedNumber);
                    }
                });
            }
            couponList.forEach(coupon -> {
                CouponResp couponResp = new CouponResp();
                BeanUtils.copyProperties(coupon, couponResp);
                couponResp.setType(coupon.getType().intValue());
                couponResp.setValidityType(coupon.getValidityType().intValue());
                couponResp.setStatus(coupon.getStatus().intValue());
                couponResp.setAllowGet(coupon.getAllowGet().intValue());
                couponResp.setScopeType(coupon.getScopeType().intValue());
                couponResp.setSendNumber(grantNumberMap.getOrDefault(couponResp.getCode(), 0L));
                couponResp.setUsedNumber(usedNumberMap.getOrDefault(couponResp.getCode(), 0L));
                couponRespList.add(couponResp);
            });
        }
        couponRespPageInfo.setList(couponRespList);
        return couponRespPageInfo;
    }

    @Autowired
    private ICustomerMarketingService customerMarketingService;

    @Override
    @Transactional
    public EditCouponReq editCoupon(EditCouponReq editCouponReq) {
        if (null == editCouponReq) {
            throw new StoreSaasMarketingException(CrmReturnCodeEnum.REQUEST_ARG_IS_EMPTY.getDesc());
        }
        Long couponId = editCouponReq.getId();
        if (null == couponId || couponId <= 0) {
            throw new StoreSaasMarketingException("优惠券ID不能为空");
        }
        CouponResp oldCoupon = getCouponDetailById(couponId);
        if (null == oldCoupon) {
            throw new StoreSaasMarketingException("优惠券不存在");
        }
         /*   if (customerMarketingService.customerMarketingContainsCoupon(editCouponReq.getId(), editCouponReq.getTenantId(), editCouponReq.getStoreId())) {
             throw new StoreSaasMarketingException("该优惠券已经关联营销活动");
        }
        */
        //校验输入
        String validateResult = this.validateEditCouponReq(oldCoupon, editCouponReq);
        if (null != validateResult) {
            throw new StoreSaasMarketingException(validateResult);
        }
        Coupon editCoupon = convertToEditCoupon(oldCoupon, editCouponReq);
        if (null != editCouponReq.getUpdateTime()) {
            VersionHelper.checkVersion(editCouponReq.getUpdateTime());
        }
        editCoupon.setUpdateTime(new Date());
        couponMapper.updateByPrimaryKeySelective(editCoupon);
        //更新使用范围
        //editCouponScopeCategory(oldCoupon, editCouponReq);
        //券数量如果从不限制改为了限制
        String key = couponSendNumberPrefix.concat(editCoupon.getCode());
        if (oldCoupon.getGrantNumber().compareTo(-1L) == 0 && editCoupon.getGrantNumber().compareTo(0L) > 0) {
            redisTemplate.opsForValue().increment(key, oldCoupon.getSendNumber());
        } else if (oldCoupon.getGrantNumber().compareTo(0L) > 0 && editCoupon.getGrantNumber().compareTo(-1L) == 0) {
            //券数量如果从现在改为了不限制
            redisTemplate.delete(key);
        }
        return editCouponReq;
    }

    /**
     * 更新优惠券适用分类
     *
     * @param oldCoupon
     * @param editCouponReq
     */
    private void editCouponScopeCategory(CouponResp oldCoupon, EditCouponReq editCouponReq) {
        Integer oldScopeType = oldCoupon.getScopeType();
        Map<String, CouponScopeCategoryResp> oldScopeCategoryMap = null;
        if (CouponScopeTypeEnum.Category.getCode().equals(oldScopeType)) {
            List<CouponScopeCategoryResp> oldscopeCategoryList = oldCoupon.getCategories();
            oldScopeCategoryMap = oldscopeCategoryList.stream().collect(Collectors.toMap(CouponScopeCategoryResp::getCategoryCode, scopeCategoryResp -> scopeCategoryResp));
        }
        Integer newScopeType = editCouponReq.getScopeType();
        //新的使用范围不是适用分类
        if (!CouponScopeTypeEnum.Category.getCode().equals(newScopeType)) {
            if (MapUtils.isNotEmpty(oldScopeCategoryMap)) {
                for (CouponScopeCategoryResp scopeCategoryResp : oldScopeCategoryMap.values()) {
                    couponScopeCategoryMapper.deleteByPrimaryKey(scopeCategoryResp.getId());
                }
            }
        } else if (CouponScopeTypeEnum.Category.getCode().equals(newScopeType)) {
            List<CouponScopeCategory> couponScopeCategories = new ArrayList<>();
            //新的使用范围是适用分类
            if (!CouponScopeTypeEnum.Category.getCode().equals(oldScopeType)) {
                //旧新的使用范围不是适用分类,则全部新增
                for (CouponScopeCategoryReq scopeCategoryReq : editCouponReq.getCategories()) {
                    CouponScopeCategory scopeCategory = new CouponScopeCategory();
                    scopeCategory.setCategoryCode(scopeCategoryReq.getCategoryCode());
                    scopeCategory.setCouponCode(oldCoupon.getCode());
                    scopeCategory.setStoreId(editCouponReq.getStoreId());
                    scopeCategory.setTenantId(editCouponReq.getTenantId());
                    couponScopeCategories.add(scopeCategory);
                }
                if (CollectionUtils.isNotEmpty(couponScopeCategories)) {
                    couponScopeCategoryMapper.insertBatch(couponScopeCategories);
                }
            } else if (CouponScopeTypeEnum.Category.getCode().equals(oldScopeType)) {
                //新旧适用范围都是限定分类
                for (CouponScopeCategoryReq scopeCategoryReq : editCouponReq.getCategories()) {
                    String categoryCode = scopeCategoryReq.getCategoryCode();
                    CouponScopeCategoryResp oldScopeCategory = oldScopeCategoryMap.remove(categoryCode);
                    //1、新增的业务分类
                    if (null == oldScopeCategory) {
                        CouponScopeCategory scopeCategory = new CouponScopeCategory();
                        scopeCategory.setCategoryCode(scopeCategoryReq.getCategoryCode());
                        scopeCategory.setCouponCode(oldCoupon.getCode());
                        scopeCategory.setStoreId(editCouponReq.getStoreId());
                        scopeCategory.setTenantId(editCouponReq.getTenantId());
                        couponScopeCategories.add(scopeCategory);
                    }
                }
                //新增的部分业务分类
                if (CollectionUtils.isNotEmpty(couponScopeCategories)) {
                    couponScopeCategoryMapper.insertBatch(couponScopeCategories);
                }
                //剩余未包含的业务分类需要删除
                if (MapUtils.isNotEmpty(oldScopeCategoryMap)) {
                    for (CouponScopeCategoryResp scopeCategoryResp : oldScopeCategoryMap.values()) {
                        couponScopeCategoryMapper.deleteByPrimaryKey(scopeCategoryResp.getId());
                    }
                }
            }
        }
    }

    /**
     * 编辑优惠券
     *
     * @param oldCoupon
     * @param editCouponReq
     * @return
     */
    private Coupon convertToEditCoupon(CouponResp oldCoupon, EditCouponReq editCouponReq) {
        Coupon editCoupon = new Coupon();
        BeanUtils.copyProperties(oldCoupon, editCoupon);
        Long sendNumber = oldCoupon.getSendNumber() + oldCoupon.getOccupyNum();
        //券数量
        editCoupon.setGrantNumber(editCouponReq.getGrantNumber());
        //券状态
        editCoupon.setStatus(editCouponReq.getStatus().byteValue());
        //券禁用时自动禁止领券
        if (editCoupon.getStatus().equals((byte) 0)) {
            //是否允许领券
            editCoupon.setAllowGet((byte) 0);
        } else {
            //是否允许领券
            editCoupon.setAllowGet(editCouponReq.getAllowGet().byteValue());
        }
        if (sendNumber.compareTo(0L) > 0) {
            return editCoupon;
        }
        editCoupon.setType(editCouponReq.getType().byteValue());
        editCoupon.setValidityType(editCouponReq.getValidityType().byteValue());
        editCoupon.setTitle(editCouponReq.getTitle());
        editCoupon.setConditionLimit(editCouponReq.getConditionLimit());
        editCoupon.setContentValue(editCouponReq.getContentValue());
        editCoupon.setDiscountValue(editCouponReq.getDiscountValue());
        editCoupon.setUseStartTime(editCouponReq.getUseStartTime());
        editCoupon.setUseEndTime(editCouponReq.getUseEndTime());
        editCoupon.setRelativeDaysNum(editCouponReq.getRelativeDaysNum());
        editCoupon.setScopeType(editCouponReq.getScopeType().byteValue());
        editCoupon.setRemark(editCouponReq.getRemark());
        editCoupon.setUpdateUser(editCouponReq.getUserId());
        return editCoupon;
    }

    /**
     * 校验修改优惠券活动的入参
     *
     * @param oldCoupon
     * @param editCouponReq
     * @return
     */
    private String validateEditCouponReq(CouponResp oldCoupon, EditCouponReq editCouponReq) {
        //优惠券已发放数量 + 占用数量
        Long number = oldCoupon.getSendNumber() + oldCoupon.getOccupyNum();
        //券数量
        Long grantNumber = editCouponReq.getGrantNumber();
        if (null == grantNumber) {
            grantNumber = -1L;
            editCouponReq.setGrantNumber(grantNumber);
        }
        if (grantNumber.compareTo(0L) < 0 && !grantNumber.equals(-1L)) {
            return "券数量只能为不限或限制（正整数）";
        }
        if (grantNumber.compareTo(0L) > 0 && grantNumber.compareTo(number) < 0) {
            return "券数量不能小于已发送与已占用数量之和";
        }
        Integer status = editCouponReq.getStatus();
        if (status.intValue() != 0 && status.intValue() != 1) {
            return "券状态格式错误";
        }
        Integer allowGet = editCouponReq.getAllowGet();
        if (allowGet.intValue() != 0 && allowGet.intValue() != 1) {
            return "允许领券格式错误";
        }
        if (number.compareTo(0L) > 0) {
            //已发放的券只允许编辑券数量，是否允许领券，券状态；
            if (oldCoupon.getTitle().equals(editCouponReq.getTitle())
                    && oldCoupon.getContentValue().compareTo(editCouponReq.getContentValue()) == 0
                    && oldCoupon.getConditionLimit().compareTo(editCouponReq.getConditionLimit()) == 0
                    && oldCoupon.getRelativeDaysNum().equals(editCouponReq.getRelativeDaysNum())
                    && oldCoupon.getRemark().equals(editCouponReq.getRemark())){
                return null;
            } else {
                return "优惠券已发放，请从券列表重新进入编辑";
            }
        }
        //使用门槛
        BigDecimal conditionLimit = editCouponReq.getConditionLimit();
        if (null == conditionLimit) {
            conditionLimit = new BigDecimal("-1");
            editCouponReq.setConditionLimit(conditionLimit);
        }
//        if (!checkPattern("^((-1)|([1-9]\\d*00))$", conditionLimit.toString())) {
//            return "优惠券使用门槛只能为不限金额或限制金额:正整数";
//        }
        //优惠金额
        BigDecimal contentValue = editCouponReq.getContentValue();
        //优惠券优惠金额不能为空
        Integer type = editCouponReq.getType();
        CouponTypeEnum couponTypeEnum = CouponTypeEnum.getEnumByCode(type);
        if (null == couponTypeEnum) {
            return "优惠券类型无效";
        }
        if (CouponTypeEnum.Money.equals(couponTypeEnum)) {
            if (null == contentValue) {
                return "代金券优惠金额不能为空";
//            } else if (!checkPattern("^[1-9]\\d*00$", contentValue.toString())) {
//                return "代金券优惠金额只能为正整数";
            }
            //如果有使用门槛
            if (conditionLimit.compareTo(BigDecimal.ZERO) > 0) {
                if (contentValue.compareTo(conditionLimit) > 0) {
                    return "使用门槛不能小于优惠金额";
                }
            }
        }
        //折扣率
        BigDecimal discountValue = editCouponReq.getDiscountValue();
        if (CouponTypeEnum.Percentage.equals(couponTypeEnum)) {
            if (null == discountValue) {
                return "折扣券折扣数不能为空";
            } else if (!checkPattern("^(\\d|[1-9]\\d)$", discountValue.toString())) {
                return "折扣券折扣数只支持输入0-9.9数字";
            }
        }

        //适用范围不能为空
        Integer scopeType = editCouponReq.getScopeType();
        CouponScopeTypeEnum scopeTypeEnum = CouponScopeTypeEnum.getEnumByCode(scopeType);
        if (null == scopeTypeEnum) {
            return "优惠券适用范围无效";
        }
        if (CouponScopeTypeEnum.Category.equals(scopeTypeEnum)) {
            if (CollectionUtils.isEmpty(editCouponReq.getCategories())) {
                return "适用范围为限定分类时，分类不能为空";
            }
        }
        //有效期
        Integer validityType = editCouponReq.getValidityType();
        CouponValidityTypeEnum validityTypeEnum = CouponValidityTypeEnum.getEnumByCode(validityType);
        if (null == validityTypeEnum) {
            return "优惠券有效期类型无效";
        }
        if (CouponValidityTypeEnum.Fixed.equals(validityTypeEnum)) {
            Date useStartTime = editCouponReq.getUseStartTime();
            Date useEndTime = editCouponReq.getUseEndTime();
            if (null == useStartTime || null == useEndTime) {
                return "优惠券有效期不能为空";
            }
            if (useEndTime.compareTo(useStartTime) <= 0) {
                return "优惠券有效期-结束日期不能早于开始日期";
            }
            if (useEndTime.compareTo(new Date()) <= 0) {
                return "优惠券有效期-结束日期不能早于当前日期";
            }
        }
        if (CouponValidityTypeEnum.Relative.equals(validityTypeEnum)) {
            if (null == editCouponReq.getRelativeDaysNum() || editCouponReq.getRelativeDaysNum() <= 0) {
                return "领券后有效天数只能为正整数";
            }
        }
        //校验优惠券名称
        /*String newTilte = editCouponReq.getTitle();
        String oldTilte = oldCoupon.getTitle();
        if (!oldTilte.equals(newTilte)) {
            CouponExample couponExample = new CouponExample();
            CouponExample.Criteria couponExampleCriteria = couponExample.createCriteria();
            couponExampleCriteria.andTitleEqualTo(newTilte);
            couponExampleCriteria.andStoreIdEqualTo(editCouponReq.getStoreId());
            List<Coupon> couponList = couponMapper.selectByExample(couponExample);
            if (CollectionUtils.isNotEmpty(couponList)) {
                for (Coupon coupon : couponList) {
                    if (!coupon.getId().equals(editCouponReq.getId())) {
                        return String.format("优惠券[%s]已存在同名的记录", editCouponReq.getTitle());
                    }
                }
            }
        }*/
        return null;
    }

    @Autowired
    private CustomerClient customerClient;

    private static final String occupyNumKeyPrefix = "occupyNumKey";

    @Override
    @Transactional
    public List<CommonResp<CustomerCoupon>> sendCoupon(SendCouponReq sendCouponReq) {
        log.info("sendCoupon-> 绑定优惠券逻辑开始->{}", sendCouponReq);
        List<String> codes = sendCouponReq.getCodes();
        if (CollectionUtils.isEmpty(codes)) {
            throw new StoreSaasMarketingException("发券优惠券编码列表为空");
        }
        if (codes.stream().filter(x -> StringUtils.isBlank(x)).count() > 0) {
            throw new StoreSaasMarketingException("优惠券券码为空");
        }
        List<String> customerIds = sendCouponReq.getCustomerIds();
        if (CollectionUtils.isEmpty(customerIds)) {
            throw new StoreSaasMarketingException("要发券的客户为空");
        }
        BaseIdsReqVO baseIdsReqVO = new BaseIdsReqVO();
        baseIdsReqVO.setId(customerIds);
        baseIdsReqVO.setStoreId(sendCouponReq.getStoreId());
        baseIdsReqVO.setTenantId(sendCouponReq.getTenantId());
        BizBaseResponse<List<CustomerDTO>> crmResult = customerClient.getCustomerByIds(baseIdsReqVO);
        if (crmResult.getData() == null || CollectionUtils.isEmpty(crmResult.getData())) {
            throw new StoreSaasMarketingException("要发券的客户不存在");
        }
        //查询需要发券的客户
        List<CustomerDTO> customerList = crmResult.getData();
        if (customerList.size() > customerIds.size()) {
            throw new StoreSaasMarketingException("要发券的部分客户不存在");
        }
        //查询优惠券列表
        CouponExample couponExample = new CouponExample();
        CouponExample.Criteria couponCriteria = couponExample.createCriteria();
        couponCriteria.andCodeIn(codes);
        List<Coupon> couponList = couponMapper.selectByExample(couponExample);
        // 需要判断余额是否可以发送
        for (Coupon x : couponList) {
            if (!x.getGrantNumber().equals(Long.valueOf(-1))) {
                //改券总共发放数
                long num = (sendCouponReq.getCount().containsKey(x.getCode())
                        ? sendCouponReq.getCount().get(x.getCode()) : 1) * sendCouponReq.getCustomerIds().size();
                if (!sendCouponReq.getReceiveType().equals(2)) {
                    CustomerCouponExample example = new CustomerCouponExample();
                    CustomerCouponExample.Criteria criteria = example.createCriteria();
                    criteria.andCouponCodeEqualTo(x.getCode());
                    int customerReceiveCount = customerCouponMapper.countByExample(example);
                    long count = x.getGrantNumber() - (x.getOccupyNum() + num) - customerReceiveCount;
                    if (count < 0) {
                        throw new StoreSaasMarketingException("优惠券[" + x.getTitle() + "] 余额不足");
                    }
                } else {
                    //占用数-当前发放数不能小于0
                    if (x.getOccupyNum() - num < 0) {
                        throw new StoreSaasMarketingException("优惠券[" + x.getTitle() + "] 余额不足");
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(couponList)) {
            throw new StoreSaasMarketingException("要发券的优惠券不存在");
        }
        if (couponList.size() != codes.size()) {
            throw new StoreSaasMarketingException("要发券的部分优惠券不存在");
        }
        if (sendCouponReq.getCount() == null) {
            sendCouponReq.setCount(new HashMap<>());
        }
        List<Future<CommonResp<CustomerCoupon>>> customerCouponFutureList = new ArrayList<>();
        for (Coupon coupon : couponList) {
            for (CustomerDTO customer : customerList) {
                int count = sendCouponReq.getCount().containsKey(coupon.getCode()) ? sendCouponReq.getCount().get(coupon.getCode()) : 1;
                for (int i = 0; i < count; i++) {
                    //     generateCustomerCoupon(coupon, customer, sendCouponReq);
                    try {
                        Future<CommonResp<CustomerCoupon>> customerCouponFuture = threadPoolTaskExecutor.submit(new Callable<CommonResp<CustomerCoupon>>() {
                            @Override
                            public CommonResp<CustomerCoupon> call() throws Exception {
                                return generateCustomerCoupon(coupon, customer, sendCouponReq);
                            }
                        });
                        customerCouponFutureList.add(customerCouponFuture);
                    } catch (Exception e) {
                        log.error("submit generateCustomerCoupon task failed", e);
                    }
                }
            }
        }
        List<CustomerCoupon> successCustomerCouponList = new ArrayList<>();
        List<CommonResp<CustomerCoupon>> customerCouponList = new ArrayList<>();
        for (Future<CommonResp<CustomerCoupon>> customerCouponFuture : customerCouponFutureList) {
            try {
                CommonResp<CustomerCoupon> customerCouponResp = customerCouponFuture.get(10, TimeUnit.SECONDS);
                if (null != customerCouponResp) {
                    customerCouponList.add(customerCouponResp);
                    if (customerCouponResp.isSuccess()) {
                        successCustomerCouponList.add(customerCouponResp.getData());
                    }
                }
            } catch (Exception e) {
                log.error("generateCustomerCoupon get result failed", e);
            }
        }
        if (successCustomerCouponList.size() < customerCouponFutureList.size()) {
            log.error("优惠券发券部分失败");
            //撤回添加的发券数
            for (CustomerCoupon customerCoupon : successCustomerCouponList) {
                String code = customerCoupon.getCouponCode();
                String key = couponSendNumberPrefix.concat(code);
                String cache = redisTemplate.opsForValue().get(key);
                if (StringUtils.isNotBlank(cache)) {
                    redisTemplate.opsForValue().increment(key, -1L);
                }
            }
        } else {
            //营销发券需要减去预占
            if (sendCouponReq.getReceiveType() == Integer.valueOf(2)) {
                Map<String, List<CustomerCoupon>> map = successCustomerCouponList.stream()
                        .collect(Collectors.groupingBy(x -> x.getCouponCode()));
                for (String key : map.keySet()) {
                    long count = map.get(key).size();
                    //因为没有分布式事务 把扣除库存的操作 放在数据库判断
                    if (customerCouponMapper.updateoccupyNumByCode(count, key) <= 0) {
                        String cache = redisTemplate.opsForValue().get(couponSendNumberPrefix.concat(key));
                        if (StringUtils.isNotBlank(cache)) {
                            redisTemplate.opsForValue().increment(key, 0 - count);
                        }
                        throw new StoreSaasMarketingException(key + "扣除占用数时失败! ");
                    }
                }
            }
            customerCouponMapper.insertBatch(successCustomerCouponList);
        }
        return customerCouponList;
    }

    /**
     * 占用优惠券
     *
     * @param x
     * @param num
     * @return
     */
    @Transactional
    @Override
    public void setOccupyNum(Coupon x, int num) {
        log.info("couponListCheckLock-> req-> {} {}", x, num);
        String occupyNumKey = occupyNumKeyPrefix + "" + x.getStoreId() + x.getTenantId() + x.getCode();
        RedisUtils redisUtils = new RedisUtils(redisTemplate,"occupyNum");
        StoreRedisUtils storeRedisUtils = new StoreRedisUtils(redisUtils, redisTemplate);
        Object value = storeRedisUtils.tryLock(occupyNumKey, 1000, 1000);
        if (value != null) {
            try {
                if(x.getGrantNumber() < 0) {//不限量，直接叠加预占数
                    Coupon u = new Coupon();
                    u.setOccupyNum(x.getOccupyNum() + num);
                    u.setId(x.getId());
                    couponMapper.updateByPrimaryKeySelective(u);
                }else {
                    CustomerCouponExample example = new CustomerCouponExample();
                    CustomerCouponExample.Criteria criteria = example.createCriteria();
                    criteria.andCouponCodeEqualTo(x.getCode());
                    int customerReceiveCount = customerCouponMapper.countByExample(example);
                    long count = x.getGrantNumber() - (x.getOccupyNum() + num) - customerReceiveCount;
                    if (count >= 0) {
                        Coupon u = new Coupon();
                        u.setOccupyNum(x.getOccupyNum() + num);
                        u.setId(x.getId());
                        long result = couponMapper.updateByPrimaryKeySelective(u);
                        if (result <= 0) {
                            throw new StoreSaasMarketingException("预占失败");
                        }
                    } else {
                        throw new StoreSaasMarketingException("余额数量不足");
                    }
                }
            } finally {
                storeRedisUtils.releaseLock(occupyNumKey, value.toString());
            }
        }
    }

    @Override
    public CommonResp<CustomerCoupon> generateCustomerCoupon(Coupon coupon, Customer customer, SendCouponReq sendCouponReq) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        return this.generateCustomerCoupon(coupon, customerDTO, sendCouponReq);
    }

    /**
     * 生成客户优惠券
     *
     * @return
     */
    public CommonResp<CustomerCoupon> generateCustomerCoupon(Coupon coupon, CustomerDTO customer, SendCouponReq sendCouponReq) {
        CommonResp<CustomerCoupon> result = new CommonResp();
        result.setCode(4000);
        result.setSuccess(false);
        String code = coupon.getCode();
        //判断优惠券状态
        Byte status = coupon.getStatus();
        //禁用状态的券，不允许领取和发放
        if (status.equals((byte) 0)) {
            result.setMessage(String.format("\"%s\"已被禁用", coupon.getTitle()));
            result.setCode(4001);
            return result;
        }
        String key = couponSendNumberPrefix.concat(code);
        Byte validityType = coupon.getValidityType();
        Date date = new Date();
        //如果是指定有效期
        if (CouponValidityTypeEnum.Fixed.value().equals(validityType)) {
            Date useEndTime = coupon.getUseEndTime();
            if (null == useEndTime) {
                log.error("优惠券[code={}]使用结束时间无效", code);
                redisTemplate.delete(key);
                result.setMessage(String.format("\"%s\"使用结束时间无效", coupon.getTitle()));
                result.setCode(4001);
                return result;
            } else if (useEndTime.compareTo(date) <= 0) {
                log.error("优惠券[code={}]已过使用结束时间", code);
                redisTemplate.delete(key);
                result.setMessage(String.format("\"%s\"已过使用结束时间", coupon.getTitle()));
                result.setCode(4001);
                return result;
            }

        }
        Long grantNumber = coupon.getGrantNumber();
        //如果不是不限数量
        if (grantNumber.compareTo(-1L) != 0) {
            String sendNumberStr = redisTemplate.opsForValue().get(key);
            if (StringUtils.isBlank(sendNumberStr)) {
                //如果之前未存放发放数量
                CustomerCouponExample customerCouponExample = new CustomerCouponExample();
                CustomerCouponExample.Criteria customerCouponCriteria = customerCouponExample.createCriteria();
                customerCouponCriteria.andCouponCodeEqualTo(code);
                int count = customerCouponMapper.countByExample(customerCouponExample);
                log.info("count ->{}", count);
                if (Long.valueOf(count + "").compareTo(grantNumber) >= 0) {
                    log.warn("优惠券[code={}],已发放完毕", code);
                    redisTemplate.delete(key);
                    result.setMessage(String.format("\"%s\"数量不足", coupon.getTitle()));
                    result.setCode(4002);
                    return result;
                } else if (StringUtils.isBlank(sendNumberStr = redisTemplate.opsForValue().get(key))) {
                    Long initCount = redisTemplate.opsForValue().increment(key, Long.parseLong(count + ""));
                    sendNumberStr = String.valueOf(initCount);
                    if (initCount.compareTo(Long.parseLong(count + "")) != 0) {//初始化值不相等,说明有别的请求进行了初始化
                        sendNumberStr = String.valueOf(redisTemplate.opsForValue().increment(key, Long.parseLong((0 - count) + "")));
                    }
                }
            }
            Long sendNumber = Long.valueOf(sendNumberStr);
            if (sendNumber.compareTo(grantNumber) >= 0) {
                log.warn("优惠券[code={}],已发放完毕", code);
                result.setCode(4002);
                redisTemplate.delete(key);
                result.setMessage(String.format("\"%s\"数量不足", coupon.getTitle()));
                return result;
            }
            Long newSendNumbr = redisTemplate.opsForValue().increment(key, 1L);//记录发一张券
            if (newSendNumbr.compareTo(grantNumber) > 0) {
                log.warn("优惠券[code={}],已发放完毕", code);
                redisTemplate.delete(key);
                result.setMessage(String.format("\"%s\"数量不足", coupon.getTitle()));
                result.setCode(4002);
                return result;
            }
        }

        CustomerCoupon customerCoupon = new CustomerCoupon();
        customerCoupon.setCouponCode(code);
        customerCoupon.setCustomerId(customer.getId());
        customerCoupon.setCreateTime(new Date());
        customerCoupon.setReceiveType(sendCouponReq.getReceiveType().byteValue());
        customerCoupon.setSendUser(sendCouponReq.getUserId());
        customerCoupon.setUseStatus((byte) 0);//未使用
        String codeNumber = codeFactory.getCodeNumberv2(CodeFactory.customerCouponPrefix.concat(code), sendCouponReq.getStoreId());
        customerCoupon.setCode(code + codeNumber);
        if (CouponValidityTypeEnum.Fixed.value().equals(validityType)) {
            customerCoupon.setUseStartTime(coupon.getUseStartTime());
            customerCoupon.setUseEndTime(coupon.getUseEndTime());
        } else {
            Integer relativeDaysNum = coupon.getRelativeDaysNum();
            customerCoupon.setUseStartTime(date);
            customerCoupon.setUseEndTime(DataTimeUtil.getDateByAddDayOfMonth(date, relativeDaysNum - 1));
        }
        customerCoupon.setUseStartTime(DataTimeUtil.getDateStartTime(customerCoupon.getUseStartTime()));
        customerCoupon.setUseEndTime(DataTimeUtil.getDateZeroTime(customerCoupon.getUseEndTime()));
        return new CommonResp(customerCoupon);
    }

    @Override
    public CouponResp getCouponDetailByCode(String couponCode, Long storeId) {
        log.info("查询优惠券详情请求couponCode：{}, storeId: {}", couponCode, storeId);
        if (StringUtils.isBlank(couponCode)) {
            throw new StoreSaasMarketingException("优惠券编码不能为空");
        }
        CouponExample couponExample = new CouponExample();
        CouponExample.Criteria couponExampleCriteria = couponExample.createCriteria();
        couponExampleCriteria.andCodeEqualTo(couponCode);
        couponExampleCriteria.andStoreIdEqualTo(storeId);
        List<Coupon> couponList = couponMapper.selectByExample(couponExample);
        if (CollectionUtils.isEmpty(couponList)) {
            return null;
        }
        Coupon coupon = couponList.get(0);
        CouponResp resp = new CouponResp();
        BeanUtils.copyProperties(coupon, resp);
        resp.setType(coupon.getType().intValue());
        resp.setValidityType(coupon.getValidityType().intValue());
        resp.setStatus(coupon.getStatus().intValue());
        resp.setAllowGet(coupon.getAllowGet().intValue());
        resp.setScopeType(coupon.getScopeType().intValue());
        //统计已发放数量
        CustomerCouponExample customerCouponExample = new CustomerCouponExample();
        CustomerCouponExample.Criteria criteria = customerCouponExample.createCriteria();
        criteria.andCouponCodeEqualTo(coupon.getCode());
        int sendCount = customerCouponMapper.countByExample(customerCouponExample);
        resp.setSendNumber(Long.valueOf(sendCount + ""));
        Byte scopeType = coupon.getScopeType();
        if (CouponScopeTypeEnum.Category.value().equals(scopeType)) {
            //查询限定的分类
            CouponScopeCategoryExample couponScopeCategoryExample = new CouponScopeCategoryExample();
            CouponScopeCategoryExample.Criteria scopeCategoryCriteria = couponScopeCategoryExample.createCriteria();
            scopeCategoryCriteria.andCouponCodeEqualTo(coupon.getCode());
            List<CouponScopeCategory> scopeCategoryList = couponScopeCategoryMapper.selectByExample(couponScopeCategoryExample);
            if (CollectionUtils.isNotEmpty(scopeCategoryList)) {
                List<CouponScopeCategoryResp> scopeCategoryRespList = new ArrayList<>();
                scopeCategoryList.forEach(scopeCategory -> {
                    CouponScopeCategoryResp scopeCategoryResp = new CouponScopeCategoryResp();
                    BeanUtils.copyProperties(scopeCategory, scopeCategoryResp);
                    scopeCategoryRespList.add(scopeCategoryResp);
                });
                resp.setCategories(scopeCategoryRespList);
            }
        }
        log.info("查询优惠券详情响应response：{}", GsonTool.toJSONString(resp));
        return resp;
    }

    @Override
    public List<CustomerCoupon> findAllUnusedCustomerCouponListByCustomerId(String customerId) {
        log.info("查询所有未使用的优惠券信息，customerId={}", customerId);
        if (StringUtils.isBlank(customerId)) {
            return null;
        }
        CustomerCouponExample customerCouponExample = new CustomerCouponExample();
        CustomerCouponExample.Criteria customerCouponCriteria = customerCouponExample.createCriteria();
        customerCouponCriteria.andCustomerIdEqualTo(customerId);
        customerCouponCriteria.andUseStatusEqualTo((byte) 0);
        List<CustomerCoupon> result = customerCouponMapper.selectByExample(customerCouponExample);
        log.info("查询所有未使用的优惠券信息，response={}", GsonTool.toJSONString(result));
        return result;
    }

    @Override
    public List<Coupon> findCouponsByCouponCodeList(List<String> couponCodeList, String storeId) {
        if (CollectionUtils.isEmpty(couponCodeList)) {
            return null;
        }
        int size = couponCodeList.size();
        List<Coupon> couponList = null;
        int loopSize = 100;
        if (size > loopSize) {
            couponList = new ArrayList<>(size);
            int loop = size / loopSize;
            if (size % loopSize > 0) {
                loop += 1;
            }
            for (int i = 0; i < loop; i++) {
                int start = i * loopSize;
                int end = (i + 1) * loopSize;
                if (end > size) {
                    end = size;
                }
                List<String> subCouponCode = couponCodeList.subList(start, end);
                List<Coupon> subCouponList = findCouponsByCouponCodeListWhole(subCouponCode, storeId);
                if (CollectionUtils.isNotEmpty(subCouponList)) {
                    couponList.addAll(subCouponList);
                }
            }
        } else {
            couponList = findCouponsByCouponCodeListWhole(couponCodeList, storeId);
        }
        return couponList;
    }

    private List<Coupon> findCouponsByCouponCodeListWhole(List<String> couponCodeList, String storeId) {
        log.info("根据优惠券编码列表批量查询优惠券信息，codes={}", GsonTool.toJSONString(couponCodeList));
        if (CollectionUtils.isEmpty(couponCodeList)) {
            return null;
        }
        CouponExample couponExample = new CouponExample();
        CouponExample.Criteria couponExampleCriteria = couponExample.createCriteria();
        couponExampleCriteria.andCodeIn(couponCodeList);
        if (StringUtils.isNotBlank(storeId)) {
            couponExampleCriteria.andStoreIdEqualTo(Long.valueOf(storeId));
        }
        List<Coupon> couponList = couponMapper.selectByExample(couponExample);
        return couponList;
    }

    @Override
    public List<CouponScopeCategory> findCouponScopeCategoryListByCouponCode(String couponCode) {
        log.info("根据优惠券编码查询优惠券限定分类,couponCode={}", couponCode);
        if (StringUtils.isBlank(couponCode)) {
            return null;
        }
        CouponScopeCategoryExample couponScopeCategoryExample = new CouponScopeCategoryExample();
        CouponScopeCategoryExample.Criteria couponScopeCategoryExampleCriteria = couponScopeCategoryExample.createCriteria();
        couponScopeCategoryExampleCriteria.andCouponCodeEqualTo(couponCode);
        List<CouponScopeCategory> couponScopeCategoryList = couponScopeCategoryMapper.selectByExample(couponScopeCategoryExample);
        return couponScopeCategoryList;
    }

    @Override
    public ServiceOrderCouponDTO getCouponsForServiceOrder(ServiceOrderCouponVO serviceOrderCouponVO) {
        String result = validateServiceOrderCouponVO(serviceOrderCouponVO);
        if (null != result) {
            throw new StoreSaasMarketingException("根据工单信息查询客户优惠券参数异常:" + result);
        }
        ServiceOrderCouponDTO serviceOrderCouponDTO = new ServiceOrderCouponDTO();
        BeanUtils.copyProperties(serviceOrderCouponVO, serviceOrderCouponDTO);
        CustomerCoupon usedCustomerCoupon = null;
        //查询当前用户所有未使用的优惠券信息
        List<CustomerCoupon> unusedCustomerCouponList = this.findAllUnusedCustomerCouponListByCustomerId(serviceOrderCouponVO.getCustomerId());
        if (StringUtils.isNotBlank(serviceOrderCouponDTO.getOrderId())) {
            if (null == unusedCustomerCouponList) {
                unusedCustomerCouponList = new ArrayList<>();
            }
            usedCustomerCoupon = this.getCustomerCouponByServiceOrderId(serviceOrderCouponDTO.getOrderId());
            if (null != usedCustomerCoupon) {
                log.info("工单[{}]当前已使用的优惠券:{}", serviceOrderCouponDTO.getOrderId(), GsonTool.toJSONString(usedCustomerCoupon));
                unusedCustomerCouponList.add(usedCustomerCoupon);
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(unusedCustomerCouponList)) {
            return serviceOrderCouponDTO;
        }
        //查询用户优惠券对应的券面
        Set<String> couponCodeSet = unusedCustomerCouponList.stream().map(CustomerCoupon::getCouponCode).collect(Collectors.toSet());
        List<Coupon> couponList = this.findCouponsByCouponCodeList(Lists.newArrayList(couponCodeSet), serviceOrderCouponVO.getStoreId());
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(couponList)) {
            return serviceOrderCouponDTO;
        }
        Map<String, Coupon> couponMap = couponList.stream().collect(Collectors.toMap(Coupon::getCode, coupon -> coupon));
        List<CustomerCouponDTO> customerCouponDTOList = convertToCustomerCouponDTOList(unusedCustomerCouponList, couponMap);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(customerCouponDTOList)) {
            return serviceOrderCouponDTO;
        }
        serviceOrderCouponDTO.setUsableCoupons(new ArrayList<>());
        serviceOrderCouponDTO.setUnusableCoupons(new ArrayList<>());
        List<Future<CustomerCouponDTO>> customerCouponFutureList = new ArrayList<>();
        for (CustomerCouponDTO customerCouponDTO : customerCouponDTOList) {
            //校验每一张券是否可用
            Future<CustomerCouponDTO> customerCouponDtoFuture = threadPoolTaskExecutor.submit(new Callable<CustomerCouponDTO>() {
                @Override
                public CustomerCouponDTO call() throws Exception {
                    return validateCustomerCouponForOrder(customerCouponDTO, serviceOrderCouponVO);
                }
            });
            customerCouponFutureList.add(customerCouponDtoFuture);
        }
        for (Future<CustomerCouponDTO> customerCouponFuture : customerCouponFutureList) {
            CustomerCouponDTO customerCouponDTO = null;
            try {
                customerCouponDTO = customerCouponFuture.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("获取优惠券是否可用于工单校验结果异常", e);
            }
            if (null != customerCouponDTO) {
                //已经使用的优惠券
                if (null != usedCustomerCoupon && customerCouponDTO.getId().equals(usedCustomerCoupon.getId())) {
                    serviceOrderCouponDTO.setUsedCoupon(customerCouponDTO);
                } else {
                    if (null == customerCouponDTO.getUnusableType()) {
                        serviceOrderCouponDTO.getUsableCoupons().add(customerCouponDTO);
                    } else {
                        serviceOrderCouponDTO.getUnusableCoupons().add(customerCouponDTO);
                    }
                }
            }
        }
        //排序，优惠券使用结束时间，越快过期的越靠前
        if (serviceOrderCouponDTO.getUsableCoupons().size() > 1) {
            Collections.sort(serviceOrderCouponDTO.getUsableCoupons(), new CustomerCouponDTOComparator());
        }
        if (serviceOrderCouponDTO.getUnusableCoupons().size() > 1) {
            Collections.sort(serviceOrderCouponDTO.getUnusableCoupons(), new CustomerCouponDTOComparator());
        }
        return serviceOrderCouponDTO;
    }

    /**
     * 校验工单查询可用优惠券的入参
     *
     * @param serviceOrderCouponVO
     * @return
     */
    private String validateServiceOrderCouponVO(ServiceOrderCouponVO serviceOrderCouponVO) {
        if (StringUtils.isBlank(serviceOrderCouponVO.getCustomerId())) {
            return "门店客户ID为空";
        }
        if (StringUtils.isBlank(serviceOrderCouponVO.getStoreId())) {
            return "门店ID为空";
        }
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(serviceOrderCouponVO.getItems())) {
            return "工单服务项目及商品列表为空";
        }
        StringBuilder sb = new StringBuilder();
        for (ServiceOrderItemVO serviceOrderItemVO : serviceOrderCouponVO.getItems()) {
            if (null == serviceOrderItemVO) {
                continue;
            }
            /*if (StringUtils.isBlank(serviceOrderItemVO.getCategoryCode())) {
                sb.append("工单服务项目或商品业务分类为空;");
                continue;
            }*/
//            if (StringUtils.isBlank(serviceOrderItemVO.getItemId())) {
//                sb.append("工单服务项目或商品ID为空;");
//                continue;
//            }
            if (null == serviceOrderItemVO.getAmount() || serviceOrderItemVO.getAmount().compareTo(0L) < 0) {
                sb.append("工单服务项目或商品价格异常;");
                continue;
            }
        }
        if (sb.length() > 0) {
            return sb.toString();
        }
        return null;
    }

    /**
     * 构建优惠券返回list
     *
     * @param customerCouponList
     * @param couponMap
     * @return
     */
    private List<CustomerCouponDTO> convertToCustomerCouponDTOList(List<CustomerCoupon> customerCouponList, Map<String, Coupon> couponMap) {
        List<CustomerCouponDTO> customerCouponDTOList = new ArrayList<>();
        for (CustomerCoupon customerCoupon : customerCouponList) {
            Coupon coupon = couponMap.get(customerCoupon.getCouponCode());
            if (null != coupon) {
                CouponDTO couponDTO = new CouponDTO();
                BeanUtils.copyProperties(coupon, couponDTO);
                couponDTO.setType(coupon.getType().intValue());
                couponDTO.setValidityType(coupon.getValidityType().intValue());
                couponDTO.setStatus(coupon.getStatus().intValue());
                couponDTO.setAllowGet(coupon.getAllowGet().intValue());
                couponDTO.setScopeType(coupon.getScopeType().intValue());
                CustomerCouponDTO customerCouponDTO = new CustomerCouponDTO();
                BeanUtils.copyProperties(customerCoupon, customerCouponDTO);
                customerCouponDTO.setReceiveType(customerCoupon.getReceiveType().intValue());
                customerCouponDTO.setUseStatus(customerCoupon.getUseStatus().intValue());
                customerCouponDTO.setCoupon(couponDTO);
                customerCouponDTOList.add(customerCouponDTO);
            }
        }
        return customerCouponDTOList;
    }

    /**
     * 验证优惠券是否可用于工单
     *
     * @return
     */
    private CustomerCouponDTO validateCustomerCouponForOrder(CustomerCouponDTO customerCouponDTO, ServiceOrderCouponVO serviceOrderCouponVO) {
        List<ServiceOrderItemVO> items = serviceOrderCouponVO.getItems();
        CouponDTO couponDTO = customerCouponDTO.getCoupon();
        //适用范围类型
        Integer scopeType = couponDTO.getScopeType();
        List<ServiceOrderItemVO> useableItems = null;
        //不限
        if (CouponScopeTypeEnum.Unlimited.getCode().equals(scopeType)) {
            useableItems = items;
        } else if (CouponScopeTypeEnum.Category.getCode().equals(scopeType)) {
            //限定分类
            List<CouponScopeCategory> couponScopeCategoryList = this.findCouponScopeCategoryListByCouponCode(couponDTO.getCode());
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(couponScopeCategoryList)) {
                List<CouponScopeCategoryDTO> couponScopeCategoryDTOList = new ArrayList<>();
                for (CouponScopeCategory couponScopeCategory : couponScopeCategoryList) {
                    CouponScopeCategoryDTO couponScopeCategoryDTO = new CouponScopeCategoryDTO();
                    BeanUtils.copyProperties(couponScopeCategory, couponScopeCategoryDTO);
                    couponScopeCategoryDTOList.add(couponScopeCategoryDTO);
                }
                couponDTO.setCategories(couponScopeCategoryDTOList);
                List<String> categoryCodeList = couponScopeCategoryList.stream().map(CouponScopeCategory::getCategoryCode).collect(Collectors.toList());
                for (ServiceOrderItemVO itemVO : items) {
                    if (null != itemVO.getCategoryCode() && categoryCodeList.contains(itemVO.getCategoryCode())) {
                        if (null == useableItems) {
                            useableItems = new ArrayList<>();
                        }
                        useableItems.add(itemVO);
                    }
                }
            }
        }
        //如果无可用的商品或服务项目
        if (null == useableItems) {
            customerCouponDTO.setUnusableType(CustomerCouponDTO.UnusableType.NotApplicable);
            return customerCouponDTO;
        }
        List<ServiceOrderItemDTO> serviceOrderItemDTOList = new ArrayList<>();
        for (ServiceOrderItemVO itemVO : useableItems) {
            ServiceOrderItemDTO serviceOrderItemDTO = new ServiceOrderItemDTO();
            BeanUtils.copyProperties(itemVO, serviceOrderItemDTO);
            serviceOrderItemDTOList.add(serviceOrderItemDTO);
        }
        customerCouponDTO.setItems(serviceOrderItemDTOList);
        Date now = new Date();
        //判断优惠券是否到达使用时间
        Date useStartTime = customerCouponDTO.getUseStartTime();
        if (now.compareTo(useStartTime) <= 0) {
            customerCouponDTO.setUnusableType(CustomerCouponDTO.UnusableType.NotStarted);
            return customerCouponDTO;
        }
        //判断优惠券是否过期
        Date useEndTime = customerCouponDTO.getUseEndTime();
        if (useEndTime.compareTo(now) <= 0) {
            customerCouponDTO.setUnusableType(CustomerCouponDTO.UnusableType.Expired);
//            return customerCouponDTO;
        }
        //判断优惠券是否满足使用门槛
        BigDecimal conditionLimit = couponDTO.getConditionLimit();
        //可使用优惠券的工单项目总额
        BigDecimal amount = BigDecimal.ZERO;
        for (ServiceOrderItemDTO serviceOrderItemDTO : customerCouponDTO.getItems()) {
            amount = amount.add(new BigDecimal(serviceOrderItemDTO.getAmount()));
        }
        //如果不是不限门槛
        if (conditionLimit.longValue() != -1l) {
            if (amount.compareTo(conditionLimit) < 0) {
                customerCouponDTO.setUnusableType(CustomerCouponDTO.UnusableType.ThresholdLimited);
            }
        }
        //根据优惠券类型，计算可优惠金额
        Integer couponType = couponDTO.getType();
        //代金券
        if (CouponTypeEnum.Money.getCode().equals(couponType)) {
            BigDecimal contentValue = couponDTO.getContentValue();
            if (amount.compareTo(contentValue) > 0) {
                customerCouponDTO.setCouponDiscountAmount(contentValue);
            } else {
                customerCouponDTO.setCouponDiscountAmount(amount);
            }
        } else if (CouponTypeEnum.Percentage.getCode().equals(couponType)) {
            BigDecimal discountValue = couponDTO.getDiscountValue();
            discountValue = BigDecimal.ONE.subtract(discountValue.divide(new BigDecimal(100)));
            customerCouponDTO.setCouponDiscountAmount(amount.multiply(discountValue));
        }
        return customerCouponDTO;
    }

    @Override
    @Transactional
    public ServiceOrderCouponDTO writeOffCustomerCouponForServiceOrder(ServiceOrderCouponUseVO serviceOrderCouponUseVO) {
        log.info("根据工单核销客户优惠券，入参={}", GsonTool.toJSONString(serviceOrderCouponUseVO));
        String validateResult = this.validateServiceOrderCouponUseVO(serviceOrderCouponUseVO);
        if (null != validateResult) {
            throw new StoreSaasMarketingException("根据工单信息核销客户优惠券参数异常:" + validateResult);
        }
        ServiceOrderCouponDTO serviceOrderCouponDTO = new ServiceOrderCouponDTO();
        BeanUtils.copyProperties(serviceOrderCouponUseVO, serviceOrderCouponDTO);
        //查询当前客户优惠券
        CustomerCoupon customerCoupon = customerCouponMapper.selectByPrimaryKey(serviceOrderCouponUseVO.getCustomerCouponId());
        if (null == customerCoupon) {
            throw new StoreSaasMarketingException("指定的客户优惠券不存在:" + serviceOrderCouponUseVO.getCustomerCouponId());
        }
        //校验优惠券是否与客户ID匹配
        if (!customerCoupon.getCustomerId().equals(serviceOrderCouponUseVO.getCustomerId())) {
            throw new StoreSaasMarketingException("指定的客户优惠券与客户不符:" + serviceOrderCouponUseVO.getCustomerCouponId());
        }
        //校验是否已被使用
        Byte useStatus = customerCoupon.getUseStatus();
        if (useStatus.equals((byte) 1)) {
            throw new StoreSaasMarketingException("指定的客户优惠券已被使用:" + serviceOrderCouponUseVO.getCustomerCouponId());
        }
        //查询用户优惠券对应的券面
        CouponResp couponResp = this.getCouponDetailByCode(customerCoupon.getCouponCode(), Long.valueOf(serviceOrderCouponUseVO.getStoreId()));
        if (null == couponResp) {
            throw new StoreSaasMarketingException("指定的优惠券信息不存在:" + customerCoupon.getCouponCode());
        }
        serviceOrderCouponDTO.setUsableCoupons(new ArrayList<>());
        serviceOrderCouponDTO.setUnusableCoupons(new ArrayList<>());
        //判断工单上的服务项目或商品是否存在，如果未赋值，则不校验
        Date now = new Date();
        if (CollectionUtils.isEmpty(serviceOrderCouponUseVO.getItems())) {
            //校验有效期
            Date useStartTime = customerCoupon.getUseStartTime();
            Date useEndTime = customerCoupon.getUseEndTime();
            if (useStartTime.compareTo(now) > 0 || useEndTime.compareTo(now) <= 0) {
                throw new StoreSaasMarketingException("指定的优惠券不在有效期内:" + customerCoupon.getCouponCode());
            }
        }
        CustomerCouponDTO customerCouponDTO = this.convertToCustomerCouponDTO(customerCoupon, couponResp);
        if (CollectionUtils.isNotEmpty(serviceOrderCouponUseVO.getItems())) {
            validateCustomerCouponForOrder(customerCouponDTO, serviceOrderCouponUseVO);
        }
        //如果不满足使用条件
        if (null != customerCouponDTO.getUnusableType()) {
//            serviceOrderCouponDTO.getUnusableCoupons().add(customerCouponDTO);
//            return serviceOrderCouponDTO;
            throw new StoreSaasMarketingException("指定的优惠券" + customerCouponDTO.getUnusableType().getMsg());
        } else {
            serviceOrderCouponDTO.getUsableCoupons().add(customerCouponDTO);
            //优惠券可以使用
            this.writeOffCustomerCoupon(customerCoupon, serviceOrderCouponUseVO);
        }

        return serviceOrderCouponDTO;
    }

    /**
     * 核销优惠券
     *
     * @param customerCoupon
     * @param serviceOrderCouponUseVO
     */
    @Transactional
    public void writeOffCustomerCoupon(CustomerCoupon customerCoupon, ServiceOrderCouponUseVO serviceOrderCouponUseVO) {
        log.info("核销优惠券持久化开始,customerCoupon={},serviceOrderCouponUseVO={}", GsonTool.toJSONString(customerCoupon), GsonTool.toJSONString(serviceOrderCouponUseVO));
        Date now = new Date();
        long startTime = System.currentTimeMillis();
        customerCoupon.setUseTime(now);
        customerCoupon.setUseStatus((byte) 1);
        CustomerCouponExample customerCouponExample = new CustomerCouponExample();
        CustomerCouponExample.Criteria customerCouponExampleCriteria = customerCouponExample.createCriteria();
        customerCouponExampleCriteria.andIdEqualTo(customerCoupon.getId());
        customerCouponExampleCriteria.andUseTimeIsNull();
        customerCouponExampleCriteria.andUseStatusEqualTo((byte) 0);
        int updateCount = customerCouponMapper.updateByExampleSelective(customerCoupon, customerCouponExample);
        long endTimeOne = System.currentTimeMillis();
        log.info("核销优惠券持久化第一步，serviceOrderCouponUseVO={},duration={}", GsonTool.toJSONString(serviceOrderCouponUseVO), endTimeOne - startTime);
        if (updateCount == 0) {
            throw new StoreSaasMarketingException("指定的客户优惠券已被使用:" + serviceOrderCouponUseVO.getCustomerCouponId());
        }
        //新增工单优惠券记录
        OrderCoupon orderCoupon = new OrderCoupon();
        orderCoupon.setCouponCode(customerCoupon.getCouponCode());
        orderCoupon.setServiceOrderId(serviceOrderCouponUseVO.getOrderId());
        orderCoupon.setCustomerId(serviceOrderCouponUseVO.getCustomerId());
        orderCoupon.setCustomerCouponId(customerCoupon.getId());
        orderCoupon.setStoreId(Long.valueOf(serviceOrderCouponUseVO.getStoreId()));
        orderCoupon.setTenantId(Long.valueOf(serviceOrderCouponUseVO.getTenantId()));
        orderCoupon.setCreateTime(now);
        orderCouponMapper.insertSelective(orderCoupon);
        long endTimeTwo = System.currentTimeMillis();
        log.info("核销优惠券持久化结束,customerCoupon={},serviceOrderCouponUseVO={},duration={}", GsonTool.toJSONString(customerCoupon), GsonTool.toJSONString(serviceOrderCouponUseVO), endTimeTwo - startTime);
    }


    @Transactional
    @Override
    public String writeOffCustomerCouponV2(String code) {
        log.info("核销优惠券持久化开始,->{}", code);
        String result = null;
        String cacheKey = "writeOffCustomerCouponV2" + "" + code;
        RedisUtils redisUtils = new RedisUtils(redisTemplate,"writeOffCustomer");
        StoreRedisUtils storeRedisUtils = new StoreRedisUtils(redisUtils, redisTemplate);
        Object value = storeRedisUtils.tryLock(cacheKey, 1000, 1000);
        if (value != null) {
            try {
                CustomerCouponExample customerCouponExample = new CustomerCouponExample();
                customerCouponExample.createCriteria().andCodeEqualTo(code);
                List<CustomerCoupon> customerCouponList = customerCouponMapper.selectByExample(customerCouponExample);
                if (CollectionUtils.isEmpty(customerCouponList)) {
                    redisTemplate.opsForHash().put("WRITEOFFMAP",code,"-1");
                    throw new StoreSaasMarketingException("优惠券查询失败");
                }
                CustomerCoupon customerCoupon = customerCouponList.get(0);
                if (customerCoupon.getUseStatus() == Byte.valueOf((byte) 1)) {
                    redisTemplate.opsForHash().put("WRITEOFFMAP",code,"-1");
                    throw new StoreSaasMarketingException("优惠券已经被使用");
                }
                if (customerCoupon.getUseEndTime().getTime() < System.currentTimeMillis()) {
                    redisTemplate.opsForHash().put("WRITEOFFMAP",code,"-1");
                    throw new StoreSaasMarketingException("优惠券已经过期");
                }
                Date now = new Date();
                customerCoupon = new CustomerCoupon();
                customerCoupon.setUseTime(now);
                customerCoupon.setUseStatus((byte) 1);
                customerCouponExample = new CustomerCouponExample();
                CustomerCouponExample.Criteria customerCouponExampleCriteria = customerCouponExample.createCriteria();
                customerCouponExampleCriteria.andCodeEqualTo(code);
                customerCouponExampleCriteria.andUseTimeIsNull();
                customerCouponExampleCriteria.andUseStatusEqualTo((byte) 0);
                int updateCount = customerCouponMapper.updateByExampleSelective(customerCoupon, customerCouponExample);
                if (updateCount == 0) {
                    redisTemplate.opsForHash().put("WRITEOFFMAP",code,"-1");
                    throw new StoreSaasMarketingException("指定的客户优惠券已被使用:" + code);
                }
                result = "核销成功";
                redisTemplate.opsForHash().put("WRITEOFFMAP",code,"1");
            } finally {
                storeRedisUtils.releaseLock(cacheKey, value.toString());
            }
        } else {
            result = "请求太频繁";
        }
        return result;

    }

    /**
     * 构建客户优惠券返回对象
     *
     * @param customerCoupon
     * @param couponResp
     * @return
     */
    private CustomerCouponDTO convertToCustomerCouponDTO(CustomerCoupon customerCoupon, CouponResp couponResp) {
        CustomerCouponDTO customerCouponDTO = new CustomerCouponDTO();
        BeanUtils.copyProperties(customerCoupon, customerCouponDTO);
        customerCouponDTO.setReceiveType(customerCoupon.getReceiveType().intValue());
        customerCouponDTO.setUseStatus(customerCoupon.getUseStatus().intValue());
        CouponDTO couponDTO = new CouponDTO();
        BeanUtils.copyProperties(couponResp, couponDTO);
        customerCouponDTO.setCoupon(couponDTO);
        if (CollectionUtils.isNotEmpty(couponResp.getCategories())) {
            List<CouponScopeCategoryDTO> categories = new ArrayList<>();
            for (CouponScopeCategoryResp couponScopeCategoryResp : couponResp.getCategories()) {
                CouponScopeCategoryDTO couponScopeCategoryDTO = new CouponScopeCategoryDTO();
                BeanUtils.copyProperties(couponScopeCategoryResp, couponScopeCategoryDTO);
                categories.add(couponScopeCategoryDTO);
            }
            couponDTO.setCategories(categories);
        }
        return customerCouponDTO;
    }

    /**
     * 校验工单核销优惠券的入参
     *
     * @param serviceOrderCouponUseVO
     * @return
     */
    private String validateServiceOrderCouponUseVO(ServiceOrderCouponUseVO serviceOrderCouponUseVO) {
        if (StringUtils.isBlank(serviceOrderCouponUseVO.getCustomerId())) {
            return "门店客户ID为空";
        }
        if (StringUtils.isBlank(serviceOrderCouponUseVO.getStoreId())) {
            return "门店ID为空";
        }
        if (StringUtils.isBlank(serviceOrderCouponUseVO.getOrderId())) {
            return "工单ID为空";
        }
        if (null == serviceOrderCouponUseVO.getCustomerCouponId() || serviceOrderCouponUseVO.getCustomerCouponId().compareTo(0L) <= 0) {
            return "客户优惠券ID无效";
        }
        if (CollectionUtils.isNotEmpty(serviceOrderCouponUseVO.getItems())) {
            StringBuilder sb = new StringBuilder();
            for (ServiceOrderItemVO serviceOrderItemVO : serviceOrderCouponUseVO.getItems()) {
                if (null == serviceOrderItemVO) {
                    continue;
                }
                /*if (StringUtils.isBlank(serviceOrderItemVO.getCategoryCode())) {
                    sb.append("工单服务项目或商品业务分类为空;");
                    continue;
                }
                if (StringUtils.isBlank(serviceOrderItemVO.getItemId())) {
                    sb.append("工单服务项目或商品ID为空;");
                    continue;
                }*/
                if (null == serviceOrderItemVO.getAmount() || serviceOrderItemVO.getAmount().compareTo(0L) < 0) {
                    sb.append("工单服务项目或商品价格异常;");
                    continue;
                }
            }
            if (sb.length() > 0) {
                return sb.toString();
            }
        }
        return null;
    }

    @Override
    @Transactional
    public ServiceOrderCouponDTO cancelWriteOffCustomerCouponForServiceOrder(ServiceOrderCouponUseVO serviceOrderCouponUseVO) {
        log.info("根据工单取消核销客户优惠券，入参={}", GsonTool.toJSONString(serviceOrderCouponUseVO));
        String validateResult = this.validateServiceOrderCouponUseVO(serviceOrderCouponUseVO);
        if (null != validateResult) {
            throw new StoreSaasMarketingException("根据工单信息核销客户优惠券参数异常:" + validateResult);
        }
        ServiceOrderCouponDTO serviceOrderCouponDTO = new ServiceOrderCouponDTO();
        BeanUtils.copyProperties(serviceOrderCouponUseVO, serviceOrderCouponDTO);
        //查询当前客户优惠券
        CustomerCoupon customerCoupon = customerCouponMapper.selectByPrimaryKey(serviceOrderCouponUseVO.getCustomerCouponId());
        if (null == customerCoupon) {
            throw new StoreSaasMarketingException("指定的客户优惠券不存在:" + serviceOrderCouponUseVO.getCustomerCouponId());
        }
        //校验优惠券是否与客户ID匹配
        if (!customerCoupon.getCustomerId().equals(serviceOrderCouponUseVO.getCustomerId())) {
            throw new StoreSaasMarketingException("指定的客户优惠券与客户不符:" + serviceOrderCouponUseVO.getCustomerCouponId());
        }
        //校验是否已被使用
        Byte useStatus = customerCoupon.getUseStatus();
        if (useStatus.equals((byte) 0)) {
            throw new StoreSaasMarketingException("指定的客户优惠券还未被使用:" + serviceOrderCouponUseVO.getCustomerCouponId());
        }
        //查询用户优惠券对应的券面
        CouponResp couponResp = this.getCouponDetailByCode(customerCoupon.getCouponCode(), Long.valueOf(serviceOrderCouponUseVO.getStoreId()));
        if (null == couponResp) {
            throw new StoreSaasMarketingException("指定的优惠券信息不存在:" + customerCoupon.getCouponCode());
        }
        //取消核销优惠券
        this.cancelWriteOffCustomerCoupon(customerCoupon, serviceOrderCouponUseVO);
        return serviceOrderCouponDTO;
    }

    @Override
    @Transactional
    public void cancelWriteOffCustomerCoupon(CustomerCoupon customerCoupon, ServiceOrderCouponUseVO serviceOrderCouponUseVO) {
        log.info("取消核销优惠券持久化开始,customerCoupon={},serviceOrderCouponUseVO={}", GsonTool.toJSONString(customerCoupon), GsonTool.toJSONString(serviceOrderCouponUseVO));
        long startTime = System.currentTimeMillis();
        customerCoupon.setUseTime(null);
        customerCoupon.setUseStatus((byte) 0);
        CustomerCouponExample customerCouponExample = new CustomerCouponExample();
        CustomerCouponExample.Criteria customerCouponExampleCriteria = customerCouponExample.createCriteria();
        customerCouponExampleCriteria.andIdEqualTo(customerCoupon.getId());
        customerCouponExampleCriteria.andUseTimeIsNotNull();
        customerCouponExampleCriteria.andUseStatusEqualTo((byte) 1);
        int updateCount = customerCouponMapper.updateByExample(customerCoupon, customerCouponExample);
        long endTimeOne = System.currentTimeMillis();
        log.info("取消核销优惠券持久化第一步，serviceOrderCouponUseVO={},duration={}", GsonTool.toJSONString(serviceOrderCouponUseVO), endTimeOne - startTime);
        if (updateCount == 0) {
            throw new StoreSaasMarketingException("指定的客户优惠券还未使用:" + serviceOrderCouponUseVO.getCustomerCouponId());
        }
        //删除工单优惠券记录
        OrderCouponExample orderCouponExample = new OrderCouponExample();
        OrderCouponExample.Criteria orderCouponExampleCriteria = orderCouponExample.createCriteria();
        orderCouponExampleCriteria.andServiceOrderIdEqualTo(serviceOrderCouponUseVO.getOrderId());
        orderCouponExampleCriteria.andCouponCodeEqualTo(customerCoupon.getCouponCode());
        orderCouponExampleCriteria.andCustomerCouponIdEqualTo(customerCoupon.getId());
        orderCouponMapper.deleteByExample(orderCouponExample);
        long endTimeTwo = System.currentTimeMillis();
        log.info("取消核销优惠券持久化结束,customerCoupon={},serviceOrderCouponUseVO={},duration={}", GsonTool.toJSONString(customerCoupon), GsonTool.toJSONString(serviceOrderCouponUseVO), endTimeTwo - startTime);
    }

    @Override
    public CustomerCouponDTO getCouponDetailByCustomerCouponId(String customerCouponId, String storeId) {
        log.info("根据客户优惠券ID查询优惠券详情入参,customerCouponId={},storeId={}", customerCouponId, storeId);
        if (StringUtils.isBlank(customerCouponId) || StringUtils.isBlank(storeId)) {
            throw new StoreSaasMarketingException("根据客户优惠券ID查询优惠券详情入参错误");
        }
        //查询当前客户优惠券
        CustomerCoupon customerCoupon = customerCouponMapper.selectByPrimaryKey(Long.valueOf(customerCouponId));
        if (null == customerCoupon) {
            throw new StoreSaasMarketingException("指定的客户优惠券不存在:" + customerCouponId);
        }
        //查询用户优惠券对应的券面
        CouponResp couponResp = this.getCouponDetailByCode(customerCoupon.getCouponCode(), Long.valueOf(storeId));
        if (null == couponResp) {
            throw new StoreSaasMarketingException("指定的优惠券信息不存在:" + customerCoupon.getCouponCode());
        }
        CustomerCouponDTO customerCouponDTO = this.convertToCustomerCouponDTO(customerCoupon, couponResp);
        return customerCouponDTO;
    }

    @Override
    public CustomerCoupon getCustomerCouponByServiceOrderId(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return null;
        }
        List<CustomerCoupon> customerCouponList = customerCouponMapper.selectByServiceOrderId(orderId);
        if (CollectionUtils.isNotEmpty(customerCouponList)) {
            return customerCouponList.get(0);
        }
        return null;
    }

    @Override
    public CouponStatisticsForCustomerMarketResp getCouponStatisticsForCustomerMarket(String couponCode, List<String> customerIds) {
        log.info("根据客户ID集合及优惠券编码获取用券数据统计，couponCode={},customerIds={}", couponCode, GsonTool.toJSONString(customerIds));
        CouponStatisticsForCustomerMarketResp couponStatisticsForCustomerMarketResp = new CouponStatisticsForCustomerMarketResp();
        if (StringUtils.isBlank(couponCode) || CollectionUtils.isEmpty(customerIds)) {
            return couponStatisticsForCustomerMarketResp;
        }
        CustomerCouponExample customerCouponExample = new CustomerCouponExample();
        CustomerCouponExample.Criteria customerCouponExampleCriteria = customerCouponExample.createCriteria();
        customerCouponExampleCriteria.andCouponCodeEqualTo(couponCode);
        customerCouponExampleCriteria.andReceiveTypeEqualTo((byte) 2);
        customerCouponExampleCriteria.andCustomerIdIn(customerIds);
        List<CustomerCoupon> customerCouponList = customerCouponMapper.selectByExample(customerCouponExample);
        if (CollectionUtils.isEmpty(customerCouponList)) {
            return couponStatisticsForCustomerMarketResp;
        }
        couponStatisticsForCustomerMarketResp.setSendNumber(Long.valueOf(customerCouponList.size()));
        List<Long> usedCustomerCouponIds = customerCouponList.stream().filter(customerCoupon -> customerCoupon.getUseStatus().equals((byte) 1)).map(CustomerCoupon::getId).collect(Collectors.toList());
        couponStatisticsForCustomerMarketResp.setUsedNumber(Long.valueOf(usedCustomerCouponIds.size()));
        if (usedCustomerCouponIds.size() > 0) {
            OrderCouponExample orderCouponExample = new OrderCouponExample();
            OrderCouponExample.Criteria orderCouponExampleCriteria = orderCouponExample.createCriteria();
            orderCouponExampleCriteria.andCustomerCouponIdIn(usedCustomerCouponIds);
            List<OrderCoupon> orderCouponList = orderCouponMapper.selectByExample(orderCouponExample);
            if (CollectionUtils.isNotEmpty(orderCouponList)) {
                List<String> orderIds = orderCouponList.stream().map(OrderCoupon::getServiceOrderId).collect(Collectors.toList());
                couponStatisticsForCustomerMarketResp.setOrderIds(orderIds);
            }
        }
        log.info("根据客户ID集合及优惠券编码获取用券数据统计，couponCode={},customerIds={},result={}", couponCode, GsonTool.toJSONString(customerIds), GsonTool.toJSONString(couponStatisticsForCustomerMarketResp));
        return couponStatisticsForCustomerMarketResp;
    }

    @Override
    public Long getCouponAvailableAccount(Long id, Long storeId) {

        CouponExample couponExample = new CouponExample();
        CouponExample.Criteria couponCriteria = couponExample.createCriteria();
        couponCriteria.andIdEqualTo(id);
        couponCriteria.andStoreIdEqualTo(storeId);
        List<Coupon> coupons = couponMapper.selectByExample(couponExample);

        if (CollectionUtils.isEmpty(coupons)) {
            log.info("优惠券 id={}不存在", id);
            return 0L;
        }

        Coupon coupon = coupons.get(0);

        if(coupon.getGrantNumber().equals(-1L)) {//不限量直接返回
            return -1L;
        }

        //统计已发放数量
        CustomerCouponExample customerCouponExample = new CustomerCouponExample();
        CustomerCouponExample.Criteria criteria = customerCouponExample.createCriteria();
        criteria.andCouponCodeEqualTo(coupon.getCode());
        int sendCount = customerCouponMapper.countByExample(customerCouponExample);


        Long availableAccount = coupon.getGrantNumber() - sendCount - coupon.getOccupyNum();
        if (availableAccount < 1) {
            return 0L;
        }
        return availableAccount;
    }
}
