package com.tuhu.store.saas.marketing.po;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public ActivityExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getOffset() {
        return offset;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andActivityCodeIsNull() {
            addCriterion("activity_code is null");
            return (Criteria) this;
        }

        public Criteria andActivityCodeIsNotNull() {
            addCriterion("activity_code is not null");
            return (Criteria) this;
        }

        public Criteria andActivityCodeEqualTo(String value) {
            addCriterion("activity_code =", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeNotEqualTo(String value) {
            addCriterion("activity_code <>", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeGreaterThan(String value) {
            addCriterion("activity_code >", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeGreaterThanOrEqualTo(String value) {
            addCriterion("activity_code >=", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeLessThan(String value) {
            addCriterion("activity_code <", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeLessThanOrEqualTo(String value) {
            addCriterion("activity_code <=", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeLike(String value) {
            addCriterion("activity_code like", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeNotLike(String value) {
            addCriterion("activity_code not like", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeIn(List<String> values) {
            addCriterion("activity_code in", values, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeNotIn(List<String> values) {
            addCriterion("activity_code not in", values, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeBetween(String value1, String value2) {
            addCriterion("activity_code between", value1, value2, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeNotBetween(String value1, String value2) {
            addCriterion("activity_code not between", value1, value2, "activityCode");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeIsNull() {
            addCriterion("encrypted_code is null");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeIsNotNull() {
            addCriterion("encrypted_code is not null");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeEqualTo(String value) {
            addCriterion("encrypted_code =", value, "encryptedCode");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeNotEqualTo(String value) {
            addCriterion("encrypted_code <>", value, "encryptedCode");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeGreaterThan(String value) {
            addCriterion("encrypted_code >", value, "encryptedCode");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeGreaterThanOrEqualTo(String value) {
            addCriterion("encrypted_code >=", value, "encryptedCode");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeLessThan(String value) {
            addCriterion("encrypted_code <", value, "encryptedCode");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeLessThanOrEqualTo(String value) {
            addCriterion("encrypted_code <=", value, "encryptedCode");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeLike(String value) {
            addCriterion("encrypted_code like", value, "encryptedCode");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeNotLike(String value) {
            addCriterion("encrypted_code not like", value, "encryptedCode");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeIn(List<String> values) {
            addCriterion("encrypted_code in", values, "encryptedCode");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeNotIn(List<String> values) {
            addCriterion("encrypted_code not in", values, "encryptedCode");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeBetween(String value1, String value2) {
            addCriterion("encrypted_code between", value1, value2, "encryptedCode");
            return (Criteria) this;
        }

        public Criteria andEncryptedCodeNotBetween(String value1, String value2) {
            addCriterion("encrypted_code not between", value1, value2, "encryptedCode");
            return (Criteria) this;
        }

        public Criteria andStoreIdIsNull() {
            addCriterion("store_id is null");
            return (Criteria) this;
        }

        public Criteria andStoreIdIsNotNull() {
            addCriterion("store_id is not null");
            return (Criteria) this;
        }

        public Criteria andStoreIdEqualTo(Long value) {
            addCriterion("store_id =", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdNotEqualTo(Long value) {
            addCriterion("store_id <>", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdGreaterThan(Long value) {
            addCriterion("store_id >", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdGreaterThanOrEqualTo(Long value) {
            addCriterion("store_id >=", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdLessThan(Long value) {
            addCriterion("store_id <", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdLessThanOrEqualTo(Long value) {
            addCriterion("store_id <=", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdIn(List<Long> values) {
            addCriterion("store_id in", values, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdNotIn(List<Long> values) {
            addCriterion("store_id not in", values, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdBetween(Long value1, Long value2) {
            addCriterion("store_id between", value1, value2, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdNotBetween(Long value1, Long value2) {
            addCriterion("store_id not between", value1, value2, "storeId");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("`type` is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("`type` is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(Byte value) {
            addCriterion("`type` =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(Byte value) {
            addCriterion("`type` <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(Byte value) {
            addCriterion("`type` >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("`type` >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(Byte value) {
            addCriterion("`type` <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(Byte value) {
            addCriterion("`type` <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<Byte> values) {
            addCriterion("`type` in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<Byte> values) {
            addCriterion("`type` not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(Byte value1, Byte value2) {
            addCriterion("`type` between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("`type` not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andPicUrlIsNull() {
            addCriterion("pic_url is null");
            return (Criteria) this;
        }

        public Criteria andPicUrlIsNotNull() {
            addCriterion("pic_url is not null");
            return (Criteria) this;
        }

        public Criteria andPicUrlEqualTo(String value) {
            addCriterion("pic_url =", value, "picUrl");
            return (Criteria) this;
        }

        public Criteria andPicUrlNotEqualTo(String value) {
            addCriterion("pic_url <>", value, "picUrl");
            return (Criteria) this;
        }

        public Criteria andPicUrlGreaterThan(String value) {
            addCriterion("pic_url >", value, "picUrl");
            return (Criteria) this;
        }

        public Criteria andPicUrlGreaterThanOrEqualTo(String value) {
            addCriterion("pic_url >=", value, "picUrl");
            return (Criteria) this;
        }

        public Criteria andPicUrlLessThan(String value) {
            addCriterion("pic_url <", value, "picUrl");
            return (Criteria) this;
        }

        public Criteria andPicUrlLessThanOrEqualTo(String value) {
            addCriterion("pic_url <=", value, "picUrl");
            return (Criteria) this;
        }

        public Criteria andPicUrlLike(String value) {
            addCriterion("pic_url like", value, "picUrl");
            return (Criteria) this;
        }

        public Criteria andPicUrlNotLike(String value) {
            addCriterion("pic_url not like", value, "picUrl");
            return (Criteria) this;
        }

        public Criteria andPicUrlIn(List<String> values) {
            addCriterion("pic_url in", values, "picUrl");
            return (Criteria) this;
        }

        public Criteria andPicUrlNotIn(List<String> values) {
            addCriterion("pic_url not in", values, "picUrl");
            return (Criteria) this;
        }

        public Criteria andPicUrlBetween(String value1, String value2) {
            addCriterion("pic_url between", value1, value2, "picUrl");
            return (Criteria) this;
        }

        public Criteria andPicUrlNotBetween(String value1, String value2) {
            addCriterion("pic_url not between", value1, value2, "picUrl");
            return (Criteria) this;
        }

        public Criteria andActivityTitleIsNull() {
            addCriterion("activity_title is null");
            return (Criteria) this;
        }

        public Criteria andActivityTitleIsNotNull() {
            addCriterion("activity_title is not null");
            return (Criteria) this;
        }

        public Criteria andActivityTitleEqualTo(String value) {
            addCriterion("activity_title =", value, "activityTitle");
            return (Criteria) this;
        }

        public Criteria andActivityTitleNotEqualTo(String value) {
            addCriterion("activity_title <>", value, "activityTitle");
            return (Criteria) this;
        }

        public Criteria andActivityTitleGreaterThan(String value) {
            addCriterion("activity_title >", value, "activityTitle");
            return (Criteria) this;
        }

        public Criteria andActivityTitleGreaterThanOrEqualTo(String value) {
            addCriterion("activity_title >=", value, "activityTitle");
            return (Criteria) this;
        }

        public Criteria andActivityTitleLessThan(String value) {
            addCriterion("activity_title <", value, "activityTitle");
            return (Criteria) this;
        }

        public Criteria andActivityTitleLessThanOrEqualTo(String value) {
            addCriterion("activity_title <=", value, "activityTitle");
            return (Criteria) this;
        }

        public Criteria andActivityTitleLike(String value) {
            addCriterion("activity_title like", value, "activityTitle");
            return (Criteria) this;
        }

        public Criteria andActivityTitleNotLike(String value) {
            addCriterion("activity_title not like", value, "activityTitle");
            return (Criteria) this;
        }

        public Criteria andActivityTitleIn(List<String> values) {
            addCriterion("activity_title in", values, "activityTitle");
            return (Criteria) this;
        }

        public Criteria andActivityTitleNotIn(List<String> values) {
            addCriterion("activity_title not in", values, "activityTitle");
            return (Criteria) this;
        }

        public Criteria andActivityTitleBetween(String value1, String value2) {
            addCriterion("activity_title between", value1, value2, "activityTitle");
            return (Criteria) this;
        }

        public Criteria andActivityTitleNotBetween(String value1, String value2) {
            addCriterion("activity_title not between", value1, value2, "activityTitle");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceIsNull() {
            addCriterion("activity_introduce is null");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceIsNotNull() {
            addCriterion("activity_introduce is not null");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceEqualTo(String value) {
            addCriterion("activity_introduce =", value, "activityIntroduce");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceNotEqualTo(String value) {
            addCriterion("activity_introduce <>", value, "activityIntroduce");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceGreaterThan(String value) {
            addCriterion("activity_introduce >", value, "activityIntroduce");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceGreaterThanOrEqualTo(String value) {
            addCriterion("activity_introduce >=", value, "activityIntroduce");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceLessThan(String value) {
            addCriterion("activity_introduce <", value, "activityIntroduce");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceLessThanOrEqualTo(String value) {
            addCriterion("activity_introduce <=", value, "activityIntroduce");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceLike(String value) {
            addCriterion("activity_introduce like", value, "activityIntroduce");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceNotLike(String value) {
            addCriterion("activity_introduce not like", value, "activityIntroduce");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceIn(List<String> values) {
            addCriterion("activity_introduce in", values, "activityIntroduce");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceNotIn(List<String> values) {
            addCriterion("activity_introduce not in", values, "activityIntroduce");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceBetween(String value1, String value2) {
            addCriterion("activity_introduce between", value1, value2, "activityIntroduce");
            return (Criteria) this;
        }

        public Criteria andActivityIntroduceNotBetween(String value1, String value2) {
            addCriterion("activity_introduce not between", value1, value2, "activityIntroduce");
            return (Criteria) this;
        }

        public Criteria andApplyNumberIsNull() {
            addCriterion("apply_number is null");
            return (Criteria) this;
        }

        public Criteria andApplyNumberIsNotNull() {
            addCriterion("apply_number is not null");
            return (Criteria) this;
        }

        public Criteria andApplyNumberEqualTo(Long value) {
            addCriterion("apply_number =", value, "applyNumber");
            return (Criteria) this;
        }

        public Criteria andApplyNumberNotEqualTo(Long value) {
            addCriterion("apply_number <>", value, "applyNumber");
            return (Criteria) this;
        }

        public Criteria andApplyNumberGreaterThan(Long value) {
            addCriterion("apply_number >", value, "applyNumber");
            return (Criteria) this;
        }

        public Criteria andApplyNumberGreaterThanOrEqualTo(Long value) {
            addCriterion("apply_number >=", value, "applyNumber");
            return (Criteria) this;
        }

        public Criteria andApplyNumberLessThan(Long value) {
            addCriterion("apply_number <", value, "applyNumber");
            return (Criteria) this;
        }

        public Criteria andApplyNumberLessThanOrEqualTo(Long value) {
            addCriterion("apply_number <=", value, "applyNumber");
            return (Criteria) this;
        }

        public Criteria andApplyNumberIn(List<Long> values) {
            addCriterion("apply_number in", values, "applyNumber");
            return (Criteria) this;
        }

        public Criteria andApplyNumberNotIn(List<Long> values) {
            addCriterion("apply_number not in", values, "applyNumber");
            return (Criteria) this;
        }

        public Criteria andApplyNumberBetween(Long value1, Long value2) {
            addCriterion("apply_number between", value1, value2, "applyNumber");
            return (Criteria) this;
        }

        public Criteria andApplyNumberNotBetween(Long value1, Long value2) {
            addCriterion("apply_number not between", value1, value2, "applyNumber");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("`status` is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("`status` is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Boolean value) {
            addCriterion("`status` =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Boolean value) {
            addCriterion("`status` <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Boolean value) {
            addCriterion("`status` >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Boolean value) {
            addCriterion("`status` >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Boolean value) {
            addCriterion("`status` <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Boolean value) {
            addCriterion("`status` <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Boolean> values) {
            addCriterion("`status` in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Boolean> values) {
            addCriterion("`status` not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Boolean value1, Boolean value2) {
            addCriterion("`status` between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Boolean value1, Boolean value2) {
            addCriterion("`status` not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andHotlineIsNull() {
            addCriterion("hotline is null");
            return (Criteria) this;
        }

        public Criteria andHotlineIsNotNull() {
            addCriterion("hotline is not null");
            return (Criteria) this;
        }

        public Criteria andHotlineEqualTo(String value) {
            addCriterion("hotline =", value, "hotline");
            return (Criteria) this;
        }

        public Criteria andHotlineNotEqualTo(String value) {
            addCriterion("hotline <>", value, "hotline");
            return (Criteria) this;
        }

        public Criteria andHotlineGreaterThan(String value) {
            addCriterion("hotline >", value, "hotline");
            return (Criteria) this;
        }

        public Criteria andHotlineGreaterThanOrEqualTo(String value) {
            addCriterion("hotline >=", value, "hotline");
            return (Criteria) this;
        }

        public Criteria andHotlineLessThan(String value) {
            addCriterion("hotline <", value, "hotline");
            return (Criteria) this;
        }

        public Criteria andHotlineLessThanOrEqualTo(String value) {
            addCriterion("hotline <=", value, "hotline");
            return (Criteria) this;
        }

        public Criteria andHotlineLike(String value) {
            addCriterion("hotline like", value, "hotline");
            return (Criteria) this;
        }

        public Criteria andHotlineNotLike(String value) {
            addCriterion("hotline not like", value, "hotline");
            return (Criteria) this;
        }

        public Criteria andHotlineIn(List<String> values) {
            addCriterion("hotline in", values, "hotline");
            return (Criteria) this;
        }

        public Criteria andHotlineNotIn(List<String> values) {
            addCriterion("hotline not in", values, "hotline");
            return (Criteria) this;
        }

        public Criteria andHotlineBetween(String value1, String value2) {
            addCriterion("hotline between", value1, value2, "hotline");
            return (Criteria) this;
        }

        public Criteria andHotlineNotBetween(String value1, String value2) {
            addCriterion("hotline not between", value1, value2, "hotline");
            return (Criteria) this;
        }

        public Criteria andPayTypeIsNull() {
            addCriterion("pay_type is null");
            return (Criteria) this;
        }

        public Criteria andPayTypeIsNotNull() {
            addCriterion("pay_type is not null");
            return (Criteria) this;
        }

        public Criteria andPayTypeEqualTo(Boolean value) {
            addCriterion("pay_type =", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeNotEqualTo(Boolean value) {
            addCriterion("pay_type <>", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeGreaterThan(Boolean value) {
            addCriterion("pay_type >", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeGreaterThanOrEqualTo(Boolean value) {
            addCriterion("pay_type >=", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeLessThan(Boolean value) {
            addCriterion("pay_type <", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeLessThanOrEqualTo(Boolean value) {
            addCriterion("pay_type <=", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeIn(List<Boolean> values) {
            addCriterion("pay_type in", values, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeNotIn(List<Boolean> values) {
            addCriterion("pay_type not in", values, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeBetween(Boolean value1, Boolean value2) {
            addCriterion("pay_type between", value1, value2, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeNotBetween(Boolean value1, Boolean value2) {
            addCriterion("pay_type not between", value1, value2, "payType");
            return (Criteria) this;
        }

        public Criteria andStartTimeIsNull() {
            addCriterion("start_time is null");
            return (Criteria) this;
        }

        public Criteria andStartTimeIsNotNull() {
            addCriterion("start_time is not null");
            return (Criteria) this;
        }

        public Criteria andStartTimeEqualTo(Date value) {
            addCriterion("start_time =", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotEqualTo(Date value) {
            addCriterion("start_time <>", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeGreaterThan(Date value) {
            addCriterion("start_time >", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("start_time >=", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeLessThan(Date value) {
            addCriterion("start_time <", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeLessThanOrEqualTo(Date value) {
            addCriterion("start_time <=", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeIn(List<Date> values) {
            addCriterion("start_time in", values, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotIn(List<Date> values) {
            addCriterion("start_time not in", values, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeBetween(Date value1, Date value2) {
            addCriterion("start_time between", value1, value2, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotBetween(Date value1, Date value2) {
            addCriterion("start_time not between", value1, value2, "startTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNull() {
            addCriterion("end_time is null");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNotNull() {
            addCriterion("end_time is not null");
            return (Criteria) this;
        }

        public Criteria andEndTimeEqualTo(Date value) {
            addCriterion("end_time =", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotEqualTo(Date value) {
            addCriterion("end_time <>", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThan(Date value) {
            addCriterion("end_time >", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("end_time >=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThan(Date value) {
            addCriterion("end_time <", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThanOrEqualTo(Date value) {
            addCriterion("end_time <=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIn(List<Date> values) {
            addCriterion("end_time in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotIn(List<Date> values) {
            addCriterion("end_time not in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeBetween(Date value1, Date value2) {
            addCriterion("end_time between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotBetween(Date value1, Date value2) {
            addCriterion("end_time not between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlIsNull() {
            addCriterion("weixin_qr_url is null");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlIsNotNull() {
            addCriterion("weixin_qr_url is not null");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlEqualTo(String value) {
            addCriterion("weixin_qr_url =", value, "weixinQrUrl");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlNotEqualTo(String value) {
            addCriterion("weixin_qr_url <>", value, "weixinQrUrl");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlGreaterThan(String value) {
            addCriterion("weixin_qr_url >", value, "weixinQrUrl");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlGreaterThanOrEqualTo(String value) {
            addCriterion("weixin_qr_url >=", value, "weixinQrUrl");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlLessThan(String value) {
            addCriterion("weixin_qr_url <", value, "weixinQrUrl");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlLessThanOrEqualTo(String value) {
            addCriterion("weixin_qr_url <=", value, "weixinQrUrl");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlLike(String value) {
            addCriterion("weixin_qr_url like", value, "weixinQrUrl");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlNotLike(String value) {
            addCriterion("weixin_qr_url not like", value, "weixinQrUrl");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlIn(List<String> values) {
            addCriterion("weixin_qr_url in", values, "weixinQrUrl");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlNotIn(List<String> values) {
            addCriterion("weixin_qr_url not in", values, "weixinQrUrl");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlBetween(String value1, String value2) {
            addCriterion("weixin_qr_url between", value1, value2, "weixinQrUrl");
            return (Criteria) this;
        }

        public Criteria andWeixinQrUrlNotBetween(String value1, String value2) {
            addCriterion("weixin_qr_url not between", value1, value2, "weixinQrUrl");
            return (Criteria) this;
        }

        public Criteria andTagstringIsNull() {
            addCriterion("tagString is null");
            return (Criteria) this;
        }

        public Criteria andTagstringIsNotNull() {
            addCriterion("tagString is not null");
            return (Criteria) this;
        }

        public Criteria andTagstringEqualTo(String value) {
            addCriterion("tagString =", value, "tagstring");
            return (Criteria) this;
        }

        public Criteria andTagstringNotEqualTo(String value) {
            addCriterion("tagString <>", value, "tagstring");
            return (Criteria) this;
        }

        public Criteria andTagstringGreaterThan(String value) {
            addCriterion("tagString >", value, "tagstring");
            return (Criteria) this;
        }

        public Criteria andTagstringGreaterThanOrEqualTo(String value) {
            addCriterion("tagString >=", value, "tagstring");
            return (Criteria) this;
        }

        public Criteria andTagstringLessThan(String value) {
            addCriterion("tagString <", value, "tagstring");
            return (Criteria) this;
        }

        public Criteria andTagstringLessThanOrEqualTo(String value) {
            addCriterion("tagString <=", value, "tagstring");
            return (Criteria) this;
        }

        public Criteria andTagstringLike(String value) {
            addCriterion("tagString like", value, "tagstring");
            return (Criteria) this;
        }

        public Criteria andTagstringNotLike(String value) {
            addCriterion("tagString not like", value, "tagstring");
            return (Criteria) this;
        }

        public Criteria andTagstringIn(List<String> values) {
            addCriterion("tagString in", values, "tagstring");
            return (Criteria) this;
        }

        public Criteria andTagstringNotIn(List<String> values) {
            addCriterion("tagString not in", values, "tagstring");
            return (Criteria) this;
        }

        public Criteria andTagstringBetween(String value1, String value2) {
            addCriterion("tagString between", value1, value2, "tagstring");
            return (Criteria) this;
        }

        public Criteria andTagstringNotBetween(String value1, String value2) {
            addCriterion("tagString not between", value1, value2, "tagstring");
            return (Criteria) this;
        }

        public Criteria andActivityTemplateIdIsNull() {
            addCriterion("activity_template_id is null");
            return (Criteria) this;
        }

        public Criteria andActivityTemplateIdIsNotNull() {
            addCriterion("activity_template_id is not null");
            return (Criteria) this;
        }

        public Criteria andActivityTemplateIdEqualTo(Long value) {
            addCriterion("activity_template_id =", value, "activityTemplateId");
            return (Criteria) this;
        }

        public Criteria andActivityTemplateIdNotEqualTo(Long value) {
            addCriterion("activity_template_id <>", value, "activityTemplateId");
            return (Criteria) this;
        }

        public Criteria andActivityTemplateIdGreaterThan(Long value) {
            addCriterion("activity_template_id >", value, "activityTemplateId");
            return (Criteria) this;
        }

        public Criteria andActivityTemplateIdGreaterThanOrEqualTo(Long value) {
            addCriterion("activity_template_id >=", value, "activityTemplateId");
            return (Criteria) this;
        }

        public Criteria andActivityTemplateIdLessThan(Long value) {
            addCriterion("activity_template_id <", value, "activityTemplateId");
            return (Criteria) this;
        }

        public Criteria andActivityTemplateIdLessThanOrEqualTo(Long value) {
            addCriterion("activity_template_id <=", value, "activityTemplateId");
            return (Criteria) this;
        }

        public Criteria andActivityTemplateIdIn(List<Long> values) {
            addCriterion("activity_template_id in", values, "activityTemplateId");
            return (Criteria) this;
        }

        public Criteria andActivityTemplateIdNotIn(List<Long> values) {
            addCriterion("activity_template_id not in", values, "activityTemplateId");
            return (Criteria) this;
        }

        public Criteria andActivityTemplateIdBetween(Long value1, Long value2) {
            addCriterion("activity_template_id between", value1, value2, "activityTemplateId");
            return (Criteria) this;
        }

        public Criteria andActivityTemplateIdNotBetween(Long value1, Long value2) {
            addCriterion("activity_template_id not between", value1, value2, "activityTemplateId");
            return (Criteria) this;
        }

        public Criteria andPicActivityTemplateIdIsNull() {
            addCriterion("pic_activity_template_id is null");
            return (Criteria) this;
        }

        public Criteria andPicActivityTemplateIdIsNotNull() {
            addCriterion("pic_activity_template_id is not null");
            return (Criteria) this;
        }

        public Criteria andPicActivityTemplateIdEqualTo(Long value) {
            addCriterion("pic_activity_template_id =", value, "picActivityTemplateId");
            return (Criteria) this;
        }

        public Criteria andPicActivityTemplateIdNotEqualTo(Long value) {
            addCriterion("pic_activity_template_id <>", value, "picActivityTemplateId");
            return (Criteria) this;
        }

        public Criteria andPicActivityTemplateIdGreaterThan(Long value) {
            addCriterion("pic_activity_template_id >", value, "picActivityTemplateId");
            return (Criteria) this;
        }

        public Criteria andPicActivityTemplateIdGreaterThanOrEqualTo(Long value) {
            addCriterion("pic_activity_template_id >=", value, "picActivityTemplateId");
            return (Criteria) this;
        }

        public Criteria andPicActivityTemplateIdLessThan(Long value) {
            addCriterion("pic_activity_template_id <", value, "picActivityTemplateId");
            return (Criteria) this;
        }

        public Criteria andPicActivityTemplateIdLessThanOrEqualTo(Long value) {
            addCriterion("pic_activity_template_id <=", value, "picActivityTemplateId");
            return (Criteria) this;
        }

        public Criteria andPicActivityTemplateIdIn(List<Long> values) {
            addCriterion("pic_activity_template_id in", values, "picActivityTemplateId");
            return (Criteria) this;
        }

        public Criteria andPicActivityTemplateIdNotIn(List<Long> values) {
            addCriterion("pic_activity_template_id not in", values, "picActivityTemplateId");
            return (Criteria) this;
        }

        public Criteria andPicActivityTemplateIdBetween(Long value1, Long value2) {
            addCriterion("pic_activity_template_id between", value1, value2, "picActivityTemplateId");
            return (Criteria) this;
        }

        public Criteria andPicActivityTemplateIdNotBetween(Long value1, Long value2) {
            addCriterion("pic_activity_template_id not between", value1, value2, "picActivityTemplateId");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIsNull() {
            addCriterion("update_user is null");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIsNotNull() {
            addCriterion("update_user is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateUserEqualTo(String value) {
            addCriterion("update_user =", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotEqualTo(String value) {
            addCriterion("update_user <>", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserGreaterThan(String value) {
            addCriterion("update_user >", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserGreaterThanOrEqualTo(String value) {
            addCriterion("update_user >=", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLessThan(String value) {
            addCriterion("update_user <", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLessThanOrEqualTo(String value) {
            addCriterion("update_user <=", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLike(String value) {
            addCriterion("update_user like", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotLike(String value) {
            addCriterion("update_user not like", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIn(List<String> values) {
            addCriterion("update_user in", values, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotIn(List<String> values) {
            addCriterion("update_user not in", values, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserBetween(String value1, String value2) {
            addCriterion("update_user between", value1, value2, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotBetween(String value1, String value2) {
            addCriterion("update_user not between", value1, value2, "updateUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNull() {
            addCriterion("create_user is null");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNotNull() {
            addCriterion("create_user is not null");
            return (Criteria) this;
        }

        public Criteria andCreateUserEqualTo(String value) {
            addCriterion("create_user =", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotEqualTo(String value) {
            addCriterion("create_user <>", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThan(String value) {
            addCriterion("create_user >", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThanOrEqualTo(String value) {
            addCriterion("create_user >=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThan(String value) {
            addCriterion("create_user <", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThanOrEqualTo(String value) {
            addCriterion("create_user <=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLike(String value) {
            addCriterion("create_user like", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotLike(String value) {
            addCriterion("create_user not like", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserIn(List<String> values) {
            addCriterion("create_user in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotIn(List<String> values) {
            addCriterion("create_user not in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserBetween(String value1, String value2) {
            addCriterion("create_user between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotBetween(String value1, String value2) {
            addCriterion("create_user not between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andActivityContentIsNull() {
            addCriterion("activity_content is null");
            return (Criteria) this;
        }

        public Criteria andActivityContentIsNotNull() {
            addCriterion("activity_content is not null");
            return (Criteria) this;
        }

        public Criteria andActivityContentEqualTo(String value) {
            addCriterion("activity_content =", value, "activityContent");
            return (Criteria) this;
        }

        public Criteria andActivityContentNotEqualTo(String value) {
            addCriterion("activity_content <>", value, "activityContent");
            return (Criteria) this;
        }

        public Criteria andActivityContentGreaterThan(String value) {
            addCriterion("activity_content >", value, "activityContent");
            return (Criteria) this;
        }

        public Criteria andActivityContentGreaterThanOrEqualTo(String value) {
            addCriterion("activity_content >=", value, "activityContent");
            return (Criteria) this;
        }

        public Criteria andActivityContentLessThan(String value) {
            addCriterion("activity_content <", value, "activityContent");
            return (Criteria) this;
        }

        public Criteria andActivityContentLessThanOrEqualTo(String value) {
            addCriterion("activity_content <=", value, "activityContent");
            return (Criteria) this;
        }

        public Criteria andActivityContentLike(String value) {
            addCriterion("activity_content like", value, "activityContent");
            return (Criteria) this;
        }

        public Criteria andActivityContentNotLike(String value) {
            addCriterion("activity_content not like", value, "activityContent");
            return (Criteria) this;
        }

        public Criteria andActivityContentIn(List<String> values) {
            addCriterion("activity_content in", values, "activityContent");
            return (Criteria) this;
        }

        public Criteria andActivityContentNotIn(List<String> values) {
            addCriterion("activity_content not in", values, "activityContent");
            return (Criteria) this;
        }

        public Criteria andActivityContentBetween(String value1, String value2) {
            addCriterion("activity_content between", value1, value2, "activityContent");
            return (Criteria) this;
        }

        public Criteria andActivityContentNotBetween(String value1, String value2) {
            addCriterion("activity_content not between", value1, value2, "activityContent");
            return (Criteria) this;
        }

        public Criteria andActivityPriceIsNull() {
            addCriterion("activity_price is null");
            return (Criteria) this;
        }

        public Criteria andActivityPriceIsNotNull() {
            addCriterion("activity_price is not null");
            return (Criteria) this;
        }

        public Criteria andActivityPriceEqualTo(BigDecimal value) {
            addCriterion("activity_price =", value, "activityPrice");
            return (Criteria) this;
        }

        public Criteria andActivityPriceNotEqualTo(BigDecimal value) {
            addCriterion("activity_price <>", value, "activityPrice");
            return (Criteria) this;
        }

        public Criteria andActivityPriceGreaterThan(BigDecimal value) {
            addCriterion("activity_price >", value, "activityPrice");
            return (Criteria) this;
        }

        public Criteria andActivityPriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("activity_price >=", value, "activityPrice");
            return (Criteria) this;
        }

        public Criteria andActivityPriceLessThan(BigDecimal value) {
            addCriterion("activity_price <", value, "activityPrice");
            return (Criteria) this;
        }

        public Criteria andActivityPriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("activity_price <=", value, "activityPrice");
            return (Criteria) this;
        }

        public Criteria andActivityPriceIn(List<BigDecimal> values) {
            addCriterion("activity_price in", values, "activityPrice");
            return (Criteria) this;
        }

        public Criteria andActivityPriceNotIn(List<BigDecimal> values) {
            addCriterion("activity_price not in", values, "activityPrice");
            return (Criteria) this;
        }

        public Criteria andActivityPriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("activity_price between", value1, value2, "activityPrice");
            return (Criteria) this;
        }

        public Criteria andActivityPriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("activity_price not between", value1, value2, "activityPrice");
            return (Criteria) this;
        }
    }

    /**
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}