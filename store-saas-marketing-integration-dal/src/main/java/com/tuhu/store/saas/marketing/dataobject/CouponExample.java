package com.tuhu.store.saas.marketing.dataobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CouponExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public CouponExample() {
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

        public Criteria andCodeIsNull() {
            addCriterion("code is null");
            return (Criteria) this;
        }

        public Criteria andCodeIsNotNull() {
            addCriterion("code is not null");
            return (Criteria) this;
        }

        public Criteria andCodeEqualTo(String value) {
            addCriterion("code =", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotEqualTo(String value) {
            addCriterion("code <>", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeGreaterThan(String value) {
            addCriterion("code >", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeGreaterThanOrEqualTo(String value) {
            addCriterion("code >=", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLessThan(String value) {
            addCriterion("code <", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLessThanOrEqualTo(String value) {
            addCriterion("code <=", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLike(String value) {
            addCriterion("code like", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotLike(String value) {
            addCriterion("code not like", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeIn(List<String> values) {
            addCriterion("code in", values, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotIn(List<String> values) {
            addCriterion("code not in", values, "code");
            return (Criteria) this;
        }

        public Criteria andCodeBetween(String value1, String value2) {
            addCriterion("code between", value1, value2, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotBetween(String value1, String value2) {
            addCriterion("code not between", value1, value2, "code");
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

        public Criteria andTenantIdIsNull() {
            addCriterion("tenant_id is null");
            return (Criteria) this;
        }

        public Criteria andTenantIdIsNotNull() {
            addCriterion("tenant_id is not null");
            return (Criteria) this;
        }

        public Criteria andTenantIdEqualTo(Long value) {
            addCriterion("tenant_id =", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdNotEqualTo(Long value) {
            addCriterion("tenant_id <>", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdGreaterThan(Long value) {
            addCriterion("tenant_id >", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdGreaterThanOrEqualTo(Long value) {
            addCriterion("tenant_id >=", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdLessThan(Long value) {
            addCriterion("tenant_id <", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdLessThanOrEqualTo(Long value) {
            addCriterion("tenant_id <=", value, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdIn(List<Long> values) {
            addCriterion("tenant_id in", values, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdNotIn(List<Long> values) {
            addCriterion("tenant_id not in", values, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdBetween(Long value1, Long value2) {
            addCriterion("tenant_id between", value1, value2, "tenantId");
            return (Criteria) this;
        }

        public Criteria andTenantIdNotBetween(Long value1, Long value2) {
            addCriterion("tenant_id not between", value1, value2, "tenantId");
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

        public Criteria andValidityTypeIsNull() {
            addCriterion("validity_type is null");
            return (Criteria) this;
        }

        public Criteria andValidityTypeIsNotNull() {
            addCriterion("validity_type is not null");
            return (Criteria) this;
        }

        public Criteria andValidityTypeEqualTo(Byte value) {
            addCriterion("validity_type =", value, "validityType");
            return (Criteria) this;
        }

        public Criteria andValidityTypeNotEqualTo(Byte value) {
            addCriterion("validity_type <>", value, "validityType");
            return (Criteria) this;
        }

        public Criteria andValidityTypeGreaterThan(Byte value) {
            addCriterion("validity_type >", value, "validityType");
            return (Criteria) this;
        }

        public Criteria andValidityTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("validity_type >=", value, "validityType");
            return (Criteria) this;
        }

        public Criteria andValidityTypeLessThan(Byte value) {
            addCriterion("validity_type <", value, "validityType");
            return (Criteria) this;
        }

        public Criteria andValidityTypeLessThanOrEqualTo(Byte value) {
            addCriterion("validity_type <=", value, "validityType");
            return (Criteria) this;
        }

        public Criteria andValidityTypeIn(List<Byte> values) {
            addCriterion("validity_type in", values, "validityType");
            return (Criteria) this;
        }

        public Criteria andValidityTypeNotIn(List<Byte> values) {
            addCriterion("validity_type not in", values, "validityType");
            return (Criteria) this;
        }

        public Criteria andValidityTypeBetween(Byte value1, Byte value2) {
            addCriterion("validity_type between", value1, value2, "validityType");
            return (Criteria) this;
        }

        public Criteria andValidityTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("validity_type not between", value1, value2, "validityType");
            return (Criteria) this;
        }

        public Criteria andTitleIsNull() {
            addCriterion("title is null");
            return (Criteria) this;
        }

        public Criteria andTitleIsNotNull() {
            addCriterion("title is not null");
            return (Criteria) this;
        }

        public Criteria andTitleEqualTo(String value) {
            addCriterion("title =", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotEqualTo(String value) {
            addCriterion("title <>", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleGreaterThan(String value) {
            addCriterion("title >", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleGreaterThanOrEqualTo(String value) {
            addCriterion("title >=", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLessThan(String value) {
            addCriterion("title <", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLessThanOrEqualTo(String value) {
            addCriterion("title <=", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLike(String value) {
            addCriterion("title like", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotLike(String value) {
            addCriterion("title not like", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleIn(List<String> values) {
            addCriterion("title in", values, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotIn(List<String> values) {
            addCriterion("title not in", values, "title");
            return (Criteria) this;
        }

        public Criteria andTitleBetween(String value1, String value2) {
            addCriterion("title between", value1, value2, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotBetween(String value1, String value2) {
            addCriterion("title not between", value1, value2, "title");
            return (Criteria) this;
        }

        public Criteria andConditionLimitIsNull() {
            addCriterion("condition_limit is null");
            return (Criteria) this;
        }

        public Criteria andConditionLimitIsNotNull() {
            addCriterion("condition_limit is not null");
            return (Criteria) this;
        }

        public Criteria andConditionLimitEqualTo(BigDecimal value) {
            addCriterion("condition_limit =", value, "conditionLimit");
            return (Criteria) this;
        }

        public Criteria andConditionLimitNotEqualTo(BigDecimal value) {
            addCriterion("condition_limit <>", value, "conditionLimit");
            return (Criteria) this;
        }

        public Criteria andConditionLimitGreaterThan(BigDecimal value) {
            addCriterion("condition_limit >", value, "conditionLimit");
            return (Criteria) this;
        }

        public Criteria andConditionLimitGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("condition_limit >=", value, "conditionLimit");
            return (Criteria) this;
        }

        public Criteria andConditionLimitLessThan(BigDecimal value) {
            addCriterion("condition_limit <", value, "conditionLimit");
            return (Criteria) this;
        }

        public Criteria andConditionLimitLessThanOrEqualTo(BigDecimal value) {
            addCriterion("condition_limit <=", value, "conditionLimit");
            return (Criteria) this;
        }

        public Criteria andConditionLimitIn(List<BigDecimal> values) {
            addCriterion("condition_limit in", values, "conditionLimit");
            return (Criteria) this;
        }

        public Criteria andConditionLimitNotIn(List<BigDecimal> values) {
            addCriterion("condition_limit not in", values, "conditionLimit");
            return (Criteria) this;
        }

        public Criteria andConditionLimitBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("condition_limit between", value1, value2, "conditionLimit");
            return (Criteria) this;
        }

        public Criteria andConditionLimitNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("condition_limit not between", value1, value2, "conditionLimit");
            return (Criteria) this;
        }

        public Criteria andContentValueIsNull() {
            addCriterion("content_value is null");
            return (Criteria) this;
        }

        public Criteria andContentValueIsNotNull() {
            addCriterion("content_value is not null");
            return (Criteria) this;
        }

        public Criteria andContentValueEqualTo(BigDecimal value) {
            addCriterion("content_value =", value, "contentValue");
            return (Criteria) this;
        }

        public Criteria andContentValueNotEqualTo(BigDecimal value) {
            addCriterion("content_value <>", value, "contentValue");
            return (Criteria) this;
        }

        public Criteria andContentValueGreaterThan(BigDecimal value) {
            addCriterion("content_value >", value, "contentValue");
            return (Criteria) this;
        }

        public Criteria andContentValueGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("content_value >=", value, "contentValue");
            return (Criteria) this;
        }

        public Criteria andContentValueLessThan(BigDecimal value) {
            addCriterion("content_value <", value, "contentValue");
            return (Criteria) this;
        }

        public Criteria andContentValueLessThanOrEqualTo(BigDecimal value) {
            addCriterion("content_value <=", value, "contentValue");
            return (Criteria) this;
        }

        public Criteria andContentValueIn(List<BigDecimal> values) {
            addCriterion("content_value in", values, "contentValue");
            return (Criteria) this;
        }

        public Criteria andContentValueNotIn(List<BigDecimal> values) {
            addCriterion("content_value not in", values, "contentValue");
            return (Criteria) this;
        }

        public Criteria andContentValueBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("content_value between", value1, value2, "contentValue");
            return (Criteria) this;
        }

        public Criteria andContentValueNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("content_value not between", value1, value2, "contentValue");
            return (Criteria) this;
        }

        public Criteria andDiscountValueIsNull() {
            addCriterion("discount_value is null");
            return (Criteria) this;
        }

        public Criteria andDiscountValueIsNotNull() {
            addCriterion("discount_value is not null");
            return (Criteria) this;
        }

        public Criteria andDiscountValueEqualTo(BigDecimal value) {
            addCriterion("discount_value =", value, "discountValue");
            return (Criteria) this;
        }

        public Criteria andDiscountValueNotEqualTo(BigDecimal value) {
            addCriterion("discount_value <>", value, "discountValue");
            return (Criteria) this;
        }

        public Criteria andDiscountValueGreaterThan(BigDecimal value) {
            addCriterion("discount_value >", value, "discountValue");
            return (Criteria) this;
        }

        public Criteria andDiscountValueGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("discount_value >=", value, "discountValue");
            return (Criteria) this;
        }

        public Criteria andDiscountValueLessThan(BigDecimal value) {
            addCriterion("discount_value <", value, "discountValue");
            return (Criteria) this;
        }

        public Criteria andDiscountValueLessThanOrEqualTo(BigDecimal value) {
            addCriterion("discount_value <=", value, "discountValue");
            return (Criteria) this;
        }

        public Criteria andDiscountValueIn(List<BigDecimal> values) {
            addCriterion("discount_value in", values, "discountValue");
            return (Criteria) this;
        }

        public Criteria andDiscountValueNotIn(List<BigDecimal> values) {
            addCriterion("discount_value not in", values, "discountValue");
            return (Criteria) this;
        }

        public Criteria andDiscountValueBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("discount_value between", value1, value2, "discountValue");
            return (Criteria) this;
        }

        public Criteria andDiscountValueNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("discount_value not between", value1, value2, "discountValue");
            return (Criteria) this;
        }

        public Criteria andUseStartTimeIsNull() {
            addCriterion("use_start_time is null");
            return (Criteria) this;
        }

        public Criteria andUseStartTimeIsNotNull() {
            addCriterion("use_start_time is not null");
            return (Criteria) this;
        }

        public Criteria andUseStartTimeEqualTo(Date value) {
            addCriterion("use_start_time =", value, "useStartTime");
            return (Criteria) this;
        }

        public Criteria andUseStartTimeNotEqualTo(Date value) {
            addCriterion("use_start_time <>", value, "useStartTime");
            return (Criteria) this;
        }

        public Criteria andUseStartTimeGreaterThan(Date value) {
            addCriterion("use_start_time >", value, "useStartTime");
            return (Criteria) this;
        }

        public Criteria andUseStartTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("use_start_time >=", value, "useStartTime");
            return (Criteria) this;
        }

        public Criteria andUseStartTimeLessThan(Date value) {
            addCriterion("use_start_time <", value, "useStartTime");
            return (Criteria) this;
        }

        public Criteria andUseStartTimeLessThanOrEqualTo(Date value) {
            addCriterion("use_start_time <=", value, "useStartTime");
            return (Criteria) this;
        }

        public Criteria andUseStartTimeIn(List<Date> values) {
            addCriterion("use_start_time in", values, "useStartTime");
            return (Criteria) this;
        }

        public Criteria andUseStartTimeNotIn(List<Date> values) {
            addCriterion("use_start_time not in", values, "useStartTime");
            return (Criteria) this;
        }

        public Criteria andUseStartTimeBetween(Date value1, Date value2) {
            addCriterion("use_start_time between", value1, value2, "useStartTime");
            return (Criteria) this;
        }

        public Criteria andUseStartTimeNotBetween(Date value1, Date value2) {
            addCriterion("use_start_time not between", value1, value2, "useStartTime");
            return (Criteria) this;
        }

        public Criteria andUseEndTimeIsNull() {
            addCriterion("use_end_time is null");
            return (Criteria) this;
        }

        public Criteria andUseEndTimeIsNotNull() {
            addCriterion("use_end_time is not null");
            return (Criteria) this;
        }

        public Criteria andUseEndTimeEqualTo(Date value) {
            addCriterion("use_end_time =", value, "useEndTime");
            return (Criteria) this;
        }

        public Criteria andUseEndTimeNotEqualTo(Date value) {
            addCriterion("use_end_time <>", value, "useEndTime");
            return (Criteria) this;
        }

        public Criteria andUseEndTimeGreaterThan(Date value) {
            addCriterion("use_end_time >", value, "useEndTime");
            return (Criteria) this;
        }

        public Criteria andUseEndTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("use_end_time >=", value, "useEndTime");
            return (Criteria) this;
        }

        public Criteria andUseEndTimeLessThan(Date value) {
            addCriterion("use_end_time <", value, "useEndTime");
            return (Criteria) this;
        }

        public Criteria andUseEndTimeLessThanOrEqualTo(Date value) {
            addCriterion("use_end_time <=", value, "useEndTime");
            return (Criteria) this;
        }

        public Criteria andUseEndTimeIn(List<Date> values) {
            addCriterion("use_end_time in", values, "useEndTime");
            return (Criteria) this;
        }

        public Criteria andUseEndTimeNotIn(List<Date> values) {
            addCriterion("use_end_time not in", values, "useEndTime");
            return (Criteria) this;
        }

        public Criteria andUseEndTimeBetween(Date value1, Date value2) {
            addCriterion("use_end_time between", value1, value2, "useEndTime");
            return (Criteria) this;
        }

        public Criteria andUseEndTimeNotBetween(Date value1, Date value2) {
            addCriterion("use_end_time not between", value1, value2, "useEndTime");
            return (Criteria) this;
        }

        public Criteria andRelativeDaysNumIsNull() {
            addCriterion("relative_days_num is null");
            return (Criteria) this;
        }

        public Criteria andRelativeDaysNumIsNotNull() {
            addCriterion("relative_days_num is not null");
            return (Criteria) this;
        }

        public Criteria andRelativeDaysNumEqualTo(Integer value) {
            addCriterion("relative_days_num =", value, "relativeDaysNum");
            return (Criteria) this;
        }

        public Criteria andRelativeDaysNumNotEqualTo(Integer value) {
            addCriterion("relative_days_num <>", value, "relativeDaysNum");
            return (Criteria) this;
        }

        public Criteria andRelativeDaysNumGreaterThan(Integer value) {
            addCriterion("relative_days_num >", value, "relativeDaysNum");
            return (Criteria) this;
        }

        public Criteria andRelativeDaysNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("relative_days_num >=", value, "relativeDaysNum");
            return (Criteria) this;
        }

        public Criteria andRelativeDaysNumLessThan(Integer value) {
            addCriterion("relative_days_num <", value, "relativeDaysNum");
            return (Criteria) this;
        }

        public Criteria andRelativeDaysNumLessThanOrEqualTo(Integer value) {
            addCriterion("relative_days_num <=", value, "relativeDaysNum");
            return (Criteria) this;
        }

        public Criteria andRelativeDaysNumIn(List<Integer> values) {
            addCriterion("relative_days_num in", values, "relativeDaysNum");
            return (Criteria) this;
        }

        public Criteria andRelativeDaysNumNotIn(List<Integer> values) {
            addCriterion("relative_days_num not in", values, "relativeDaysNum");
            return (Criteria) this;
        }

        public Criteria andRelativeDaysNumBetween(Integer value1, Integer value2) {
            addCriterion("relative_days_num between", value1, value2, "relativeDaysNum");
            return (Criteria) this;
        }

        public Criteria andRelativeDaysNumNotBetween(Integer value1, Integer value2) {
            addCriterion("relative_days_num not between", value1, value2, "relativeDaysNum");
            return (Criteria) this;
        }

        public Criteria andGrantNumberIsNull() {
            addCriterion("grant_number is null");
            return (Criteria) this;
        }

        public Criteria andGrantNumberIsNotNull() {
            addCriterion("grant_number is not null");
            return (Criteria) this;
        }

        public Criteria andGrantNumberEqualTo(Long value) {
            addCriterion("grant_number =", value, "grantNumber");
            return (Criteria) this;
        }

        public Criteria andGrantNumberNotEqualTo(Long value) {
            addCriterion("grant_number <>", value, "grantNumber");
            return (Criteria) this;
        }

        public Criteria andGrantNumberGreaterThan(Long value) {
            addCriterion("grant_number >", value, "grantNumber");
            return (Criteria) this;
        }

        public Criteria andGrantNumberGreaterThanOrEqualTo(Long value) {
            addCriterion("grant_number >=", value, "grantNumber");
            return (Criteria) this;
        }

        public Criteria andGrantNumberLessThan(Long value) {
            addCriterion("grant_number <", value, "grantNumber");
            return (Criteria) this;
        }

        public Criteria andGrantNumberLessThanOrEqualTo(Long value) {
            addCriterion("grant_number <=", value, "grantNumber");
            return (Criteria) this;
        }

        public Criteria andGrantNumberIn(List<Long> values) {
            addCriterion("grant_number in", values, "grantNumber");
            return (Criteria) this;
        }

        public Criteria andGrantNumberNotIn(List<Long> values) {
            addCriterion("grant_number not in", values, "grantNumber");
            return (Criteria) this;
        }

        public Criteria andGrantNumberBetween(Long value1, Long value2) {
            addCriterion("grant_number between", value1, value2, "grantNumber");
            return (Criteria) this;
        }

        public Criteria andGrantNumberNotBetween(Long value1, Long value2) {
            addCriterion("grant_number not between", value1, value2, "grantNumber");
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

        public Criteria andStatusEqualTo(Byte value) {
            addCriterion("`status` =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Byte value) {
            addCriterion("`status` <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Byte value) {
            addCriterion("`status` >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("`status` >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Byte value) {
            addCriterion("`status` <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Byte value) {
            addCriterion("`status` <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Byte> values) {
            addCriterion("`status` in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Byte> values) {
            addCriterion("`status` not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Byte value1, Byte value2) {
            addCriterion("`status` between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("`status` not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andAllowGetIsNull() {
            addCriterion("allow_get is null");
            return (Criteria) this;
        }

        public Criteria andAllowGetIsNotNull() {
            addCriterion("allow_get is not null");
            return (Criteria) this;
        }

        public Criteria andAllowGetEqualTo(Byte value) {
            addCriterion("allow_get =", value, "allowGet");
            return (Criteria) this;
        }

        public Criteria andAllowGetNotEqualTo(Byte value) {
            addCriterion("allow_get <>", value, "allowGet");
            return (Criteria) this;
        }

        public Criteria andAllowGetGreaterThan(Byte value) {
            addCriterion("allow_get >", value, "allowGet");
            return (Criteria) this;
        }

        public Criteria andAllowGetGreaterThanOrEqualTo(Byte value) {
            addCriterion("allow_get >=", value, "allowGet");
            return (Criteria) this;
        }

        public Criteria andAllowGetLessThan(Byte value) {
            addCriterion("allow_get <", value, "allowGet");
            return (Criteria) this;
        }

        public Criteria andAllowGetLessThanOrEqualTo(Byte value) {
            addCriterion("allow_get <=", value, "allowGet");
            return (Criteria) this;
        }

        public Criteria andAllowGetIn(List<Byte> values) {
            addCriterion("allow_get in", values, "allowGet");
            return (Criteria) this;
        }

        public Criteria andAllowGetNotIn(List<Byte> values) {
            addCriterion("allow_get not in", values, "allowGet");
            return (Criteria) this;
        }

        public Criteria andAllowGetBetween(Byte value1, Byte value2) {
            addCriterion("allow_get between", value1, value2, "allowGet");
            return (Criteria) this;
        }

        public Criteria andAllowGetNotBetween(Byte value1, Byte value2) {
            addCriterion("allow_get not between", value1, value2, "allowGet");
            return (Criteria) this;
        }

        public Criteria andScopeTypeIsNull() {
            addCriterion("scope_type is null");
            return (Criteria) this;
        }

        public Criteria andScopeTypeIsNotNull() {
            addCriterion("scope_type is not null");
            return (Criteria) this;
        }

        public Criteria andScopeTypeEqualTo(Byte value) {
            addCriterion("scope_type =", value, "scopeType");
            return (Criteria) this;
        }

        public Criteria andScopeTypeNotEqualTo(Byte value) {
            addCriterion("scope_type <>", value, "scopeType");
            return (Criteria) this;
        }

        public Criteria andScopeTypeGreaterThan(Byte value) {
            addCriterion("scope_type >", value, "scopeType");
            return (Criteria) this;
        }

        public Criteria andScopeTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("scope_type >=", value, "scopeType");
            return (Criteria) this;
        }

        public Criteria andScopeTypeLessThan(Byte value) {
            addCriterion("scope_type <", value, "scopeType");
            return (Criteria) this;
        }

        public Criteria andScopeTypeLessThanOrEqualTo(Byte value) {
            addCriterion("scope_type <=", value, "scopeType");
            return (Criteria) this;
        }

        public Criteria andScopeTypeIn(List<Byte> values) {
            addCriterion("scope_type in", values, "scopeType");
            return (Criteria) this;
        }

        public Criteria andScopeTypeNotIn(List<Byte> values) {
            addCriterion("scope_type not in", values, "scopeType");
            return (Criteria) this;
        }

        public Criteria andScopeTypeBetween(Byte value1, Byte value2) {
            addCriterion("scope_type between", value1, value2, "scopeType");
            return (Criteria) this;
        }

        public Criteria andScopeTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("scope_type not between", value1, value2, "scopeType");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
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

        public Criteria andOccupyNumIsNull() {
            addCriterion("occupy_num is null");
            return (Criteria) this;
        }

        public Criteria andOccupyNumIsNotNull() {
            addCriterion("occupy_num is not null");
            return (Criteria) this;
        }

        public Criteria andOccupyNumEqualTo(Long value) {
            addCriterion("occupy_num =", value, "occupyNum");
            return (Criteria) this;
        }

        public Criteria andOccupyNumNotEqualTo(Long value) {
            addCriterion("occupy_num <>", value, "occupyNum");
            return (Criteria) this;
        }

        public Criteria andOccupyNumGreaterThan(Long value) {
            addCriterion("occupy_num >", value, "occupyNum");
            return (Criteria) this;
        }

        public Criteria andOccupyNumGreaterThanOrEqualTo(Long value) {
            addCriterion("occupy_num >=", value, "occupyNum");
            return (Criteria) this;
        }

        public Criteria andOccupyNumLessThan(Long value) {
            addCriterion("occupy_num <", value, "occupyNum");
            return (Criteria) this;
        }

        public Criteria andOccupyNumLessThanOrEqualTo(Long value) {
            addCriterion("occupy_num <=", value, "occupyNum");
            return (Criteria) this;
        }

        public Criteria andOccupyNumIn(List<Long> values) {
            addCriterion("occupy_num in", values, "occupyNum");
            return (Criteria) this;
        }

        public Criteria andOccupyNumNotIn(List<Long> values) {
            addCriterion("occupy_num not in", values, "occupyNum");
            return (Criteria) this;
        }

        public Criteria andOccupyNumBetween(Long value1, Long value2) {
            addCriterion("occupy_num between", value1, value2, "occupyNum");
            return (Criteria) this;
        }

        public Criteria andOccupyNumNotBetween(Long value1, Long value2) {
            addCriterion("occupy_num not between", value1, value2, "occupyNum");
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