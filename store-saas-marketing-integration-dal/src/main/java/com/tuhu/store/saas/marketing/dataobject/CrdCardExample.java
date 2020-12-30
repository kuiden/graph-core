package com.tuhu.store.saas.marketing.dataobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CrdCardExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Long offset;

    private Boolean forUpdate;

    public CrdCardExample() {
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

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getOffset() {
        return offset;
    }

    public void setForUpdate(Boolean forUpdate) {
        this.forUpdate = forUpdate;
    }

    public Boolean getForUpdate() {
        return forUpdate;
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

        public Criteria andCardTemplateIdIsNull() {
            addCriterion("card_template_id is null");
            return (Criteria) this;
        }

        public Criteria andCardTemplateIdIsNotNull() {
            addCriterion("card_template_id is not null");
            return (Criteria) this;
        }

        public Criteria andCardTemplateIdEqualTo(Long value) {
            addCriterion("card_template_id =", value, "cardTemplateId");
            return (Criteria) this;
        }

        public Criteria andCardTemplateIdNotEqualTo(Long value) {
            addCriterion("card_template_id <>", value, "cardTemplateId");
            return (Criteria) this;
        }

        public Criteria andCardTemplateIdGreaterThan(Long value) {
            addCriterion("card_template_id >", value, "cardTemplateId");
            return (Criteria) this;
        }

        public Criteria andCardTemplateIdGreaterThanOrEqualTo(Long value) {
            addCriterion("card_template_id >=", value, "cardTemplateId");
            return (Criteria) this;
        }

        public Criteria andCardTemplateIdLessThan(Long value) {
            addCriterion("card_template_id <", value, "cardTemplateId");
            return (Criteria) this;
        }

        public Criteria andCardTemplateIdLessThanOrEqualTo(Long value) {
            addCriterion("card_template_id <=", value, "cardTemplateId");
            return (Criteria) this;
        }

        public Criteria andCardTemplateIdIn(List<Long> values) {
            addCriterion("card_template_id in", values, "cardTemplateId");
            return (Criteria) this;
        }

        public Criteria andCardTemplateIdNotIn(List<Long> values) {
            addCriterion("card_template_id not in", values, "cardTemplateId");
            return (Criteria) this;
        }

        public Criteria andCardTemplateIdBetween(Long value1, Long value2) {
            addCriterion("card_template_id between", value1, value2, "cardTemplateId");
            return (Criteria) this;
        }

        public Criteria andCardTemplateIdNotBetween(Long value1, Long value2) {
            addCriterion("card_template_id not between", value1, value2, "cardTemplateId");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeIsNull() {
            addCriterion("card_category_code is null");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeIsNotNull() {
            addCriterion("card_category_code is not null");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeEqualTo(String value) {
            addCriterion("card_category_code =", value, "cardCategoryCode");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeNotEqualTo(String value) {
            addCriterion("card_category_code <>", value, "cardCategoryCode");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeGreaterThan(String value) {
            addCriterion("card_category_code >", value, "cardCategoryCode");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeGreaterThanOrEqualTo(String value) {
            addCriterion("card_category_code >=", value, "cardCategoryCode");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeLessThan(String value) {
            addCriterion("card_category_code <", value, "cardCategoryCode");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeLessThanOrEqualTo(String value) {
            addCriterion("card_category_code <=", value, "cardCategoryCode");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeLike(String value) {
            addCriterion("card_category_code like", value, "cardCategoryCode");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeNotLike(String value) {
            addCriterion("card_category_code not like", value, "cardCategoryCode");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeIn(List<String> values) {
            addCriterion("card_category_code in", values, "cardCategoryCode");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeNotIn(List<String> values) {
            addCriterion("card_category_code not in", values, "cardCategoryCode");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeBetween(String value1, String value2) {
            addCriterion("card_category_code between", value1, value2, "cardCategoryCode");
            return (Criteria) this;
        }

        public Criteria andCardCategoryCodeNotBetween(String value1, String value2) {
            addCriterion("card_category_code not between", value1, value2, "cardCategoryCode");
            return (Criteria) this;
        }

        public Criteria andCardNoIsNull() {
            addCriterion("card_no is null");
            return (Criteria) this;
        }

        public Criteria andCardNoIsNotNull() {
            addCriterion("card_no is not null");
            return (Criteria) this;
        }

        public Criteria andCardNoEqualTo(String value) {
            addCriterion("card_no =", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotEqualTo(String value) {
            addCriterion("card_no <>", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoGreaterThan(String value) {
            addCriterion("card_no >", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoGreaterThanOrEqualTo(String value) {
            addCriterion("card_no >=", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoLessThan(String value) {
            addCriterion("card_no <", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoLessThanOrEqualTo(String value) {
            addCriterion("card_no <=", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoLike(String value) {
            addCriterion("card_no like", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotLike(String value) {
            addCriterion("card_no not like", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoIn(List<String> values) {
            addCriterion("card_no in", values, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotIn(List<String> values) {
            addCriterion("card_no not in", values, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoBetween(String value1, String value2) {
            addCriterion("card_no between", value1, value2, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotBetween(String value1, String value2) {
            addCriterion("card_no not between", value1, value2, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNameIsNull() {
            addCriterion("card_name is null");
            return (Criteria) this;
        }

        public Criteria andCardNameIsNotNull() {
            addCriterion("card_name is not null");
            return (Criteria) this;
        }

        public Criteria andCardNameEqualTo(String value) {
            addCriterion("card_name =", value, "cardName");
            return (Criteria) this;
        }

        public Criteria andCardNameNotEqualTo(String value) {
            addCriterion("card_name <>", value, "cardName");
            return (Criteria) this;
        }

        public Criteria andCardNameGreaterThan(String value) {
            addCriterion("card_name >", value, "cardName");
            return (Criteria) this;
        }

        public Criteria andCardNameGreaterThanOrEqualTo(String value) {
            addCriterion("card_name >=", value, "cardName");
            return (Criteria) this;
        }

        public Criteria andCardNameLessThan(String value) {
            addCriterion("card_name <", value, "cardName");
            return (Criteria) this;
        }

        public Criteria andCardNameLessThanOrEqualTo(String value) {
            addCriterion("card_name <=", value, "cardName");
            return (Criteria) this;
        }

        public Criteria andCardNameLike(String value) {
            addCriterion("card_name like", value, "cardName");
            return (Criteria) this;
        }

        public Criteria andCardNameNotLike(String value) {
            addCriterion("card_name not like", value, "cardName");
            return (Criteria) this;
        }

        public Criteria andCardNameIn(List<String> values) {
            addCriterion("card_name in", values, "cardName");
            return (Criteria) this;
        }

        public Criteria andCardNameNotIn(List<String> values) {
            addCriterion("card_name not in", values, "cardName");
            return (Criteria) this;
        }

        public Criteria andCardNameBetween(String value1, String value2) {
            addCriterion("card_name between", value1, value2, "cardName");
            return (Criteria) this;
        }

        public Criteria andCardNameNotBetween(String value1, String value2) {
            addCriterion("card_name not between", value1, value2, "cardName");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeIsNull() {
            addCriterion("card_type_code is null");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeIsNotNull() {
            addCriterion("card_type_code is not null");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeEqualTo(String value) {
            addCriterion("card_type_code =", value, "cardTypeCode");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeNotEqualTo(String value) {
            addCriterion("card_type_code <>", value, "cardTypeCode");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeGreaterThan(String value) {
            addCriterion("card_type_code >", value, "cardTypeCode");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeGreaterThanOrEqualTo(String value) {
            addCriterion("card_type_code >=", value, "cardTypeCode");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeLessThan(String value) {
            addCriterion("card_type_code <", value, "cardTypeCode");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeLessThanOrEqualTo(String value) {
            addCriterion("card_type_code <=", value, "cardTypeCode");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeLike(String value) {
            addCriterion("card_type_code like", value, "cardTypeCode");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeNotLike(String value) {
            addCriterion("card_type_code not like", value, "cardTypeCode");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeIn(List<String> values) {
            addCriterion("card_type_code in", values, "cardTypeCode");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeNotIn(List<String> values) {
            addCriterion("card_type_code not in", values, "cardTypeCode");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeBetween(String value1, String value2) {
            addCriterion("card_type_code between", value1, value2, "cardTypeCode");
            return (Criteria) this;
        }

        public Criteria andCardTypeCodeNotBetween(String value1, String value2) {
            addCriterion("card_type_code not between", value1, value2, "cardTypeCode");
            return (Criteria) this;
        }

        public Criteria andForeverIsNull() {
            addCriterion("forever is null");
            return (Criteria) this;
        }

        public Criteria andForeverIsNotNull() {
            addCriterion("forever is not null");
            return (Criteria) this;
        }

        public Criteria andForeverEqualTo(Byte value) {
            addCriterion("forever =", value, "forever");
            return (Criteria) this;
        }

        public Criteria andForeverNotEqualTo(Byte value) {
            addCriterion("forever <>", value, "forever");
            return (Criteria) this;
        }

        public Criteria andForeverGreaterThan(Byte value) {
            addCriterion("forever >", value, "forever");
            return (Criteria) this;
        }

        public Criteria andForeverGreaterThanOrEqualTo(Byte value) {
            addCriterion("forever >=", value, "forever");
            return (Criteria) this;
        }

        public Criteria andForeverLessThan(Byte value) {
            addCriterion("forever <", value, "forever");
            return (Criteria) this;
        }

        public Criteria andForeverLessThanOrEqualTo(Byte value) {
            addCriterion("forever <=", value, "forever");
            return (Criteria) this;
        }

        public Criteria andForeverIn(List<Byte> values) {
            addCriterion("forever in", values, "forever");
            return (Criteria) this;
        }

        public Criteria andForeverNotIn(List<Byte> values) {
            addCriterion("forever not in", values, "forever");
            return (Criteria) this;
        }

        public Criteria andForeverBetween(Byte value1, Byte value2) {
            addCriterion("forever between", value1, value2, "forever");
            return (Criteria) this;
        }

        public Criteria andForeverNotBetween(Byte value1, Byte value2) {
            addCriterion("forever not between", value1, value2, "forever");
            return (Criteria) this;
        }

        public Criteria andExpiryDateIsNull() {
            addCriterion("expiry_date is null");
            return (Criteria) this;
        }

        public Criteria andExpiryDateIsNotNull() {
            addCriterion("expiry_date is not null");
            return (Criteria) this;
        }

        public Criteria andExpiryDateEqualTo(Date value) {
            addCriterion("expiry_date =", value, "expiryDate");
            return (Criteria) this;
        }

        public Criteria andExpiryDateNotEqualTo(Date value) {
            addCriterion("expiry_date <>", value, "expiryDate");
            return (Criteria) this;
        }

        public Criteria andExpiryDateGreaterThan(Date value) {
            addCriterion("expiry_date >", value, "expiryDate");
            return (Criteria) this;
        }

        public Criteria andExpiryDateGreaterThanOrEqualTo(Date value) {
            addCriterion("expiry_date >=", value, "expiryDate");
            return (Criteria) this;
        }

        public Criteria andExpiryDateLessThan(Date value) {
            addCriterion("expiry_date <", value, "expiryDate");
            return (Criteria) this;
        }

        public Criteria andExpiryDateLessThanOrEqualTo(Date value) {
            addCriterion("expiry_date <=", value, "expiryDate");
            return (Criteria) this;
        }

        public Criteria andExpiryDateIn(List<Date> values) {
            addCriterion("expiry_date in", values, "expiryDate");
            return (Criteria) this;
        }

        public Criteria andExpiryDateNotIn(List<Date> values) {
            addCriterion("expiry_date not in", values, "expiryDate");
            return (Criteria) this;
        }

        public Criteria andExpiryDateBetween(Date value1, Date value2) {
            addCriterion("expiry_date between", value1, value2, "expiryDate");
            return (Criteria) this;
        }

        public Criteria andExpiryDateNotBetween(Date value1, Date value2) {
            addCriterion("expiry_date not between", value1, value2, "expiryDate");
            return (Criteria) this;
        }

        public Criteria andFaceAmountIsNull() {
            addCriterion("face_amount is null");
            return (Criteria) this;
        }

        public Criteria andFaceAmountIsNotNull() {
            addCriterion("face_amount is not null");
            return (Criteria) this;
        }

        public Criteria andFaceAmountEqualTo(BigDecimal value) {
            addCriterion("face_amount =", value, "faceAmount");
            return (Criteria) this;
        }

        public Criteria andFaceAmountNotEqualTo(BigDecimal value) {
            addCriterion("face_amount <>", value, "faceAmount");
            return (Criteria) this;
        }

        public Criteria andFaceAmountGreaterThan(BigDecimal value) {
            addCriterion("face_amount >", value, "faceAmount");
            return (Criteria) this;
        }

        public Criteria andFaceAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("face_amount >=", value, "faceAmount");
            return (Criteria) this;
        }

        public Criteria andFaceAmountLessThan(BigDecimal value) {
            addCriterion("face_amount <", value, "faceAmount");
            return (Criteria) this;
        }

        public Criteria andFaceAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("face_amount <=", value, "faceAmount");
            return (Criteria) this;
        }

        public Criteria andFaceAmountIn(List<BigDecimal> values) {
            addCriterion("face_amount in", values, "faceAmount");
            return (Criteria) this;
        }

        public Criteria andFaceAmountNotIn(List<BigDecimal> values) {
            addCriterion("face_amount not in", values, "faceAmount");
            return (Criteria) this;
        }

        public Criteria andFaceAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("face_amount between", value1, value2, "faceAmount");
            return (Criteria) this;
        }

        public Criteria andFaceAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("face_amount not between", value1, value2, "faceAmount");
            return (Criteria) this;
        }

        public Criteria andDiscountAmountIsNull() {
            addCriterion("discount_amount is null");
            return (Criteria) this;
        }

        public Criteria andDiscountAmountIsNotNull() {
            addCriterion("discount_amount is not null");
            return (Criteria) this;
        }

        public Criteria andDiscountAmountEqualTo(BigDecimal value) {
            addCriterion("discount_amount =", value, "discountAmount");
            return (Criteria) this;
        }

        public Criteria andDiscountAmountNotEqualTo(BigDecimal value) {
            addCriterion("discount_amount <>", value, "discountAmount");
            return (Criteria) this;
        }

        public Criteria andDiscountAmountGreaterThan(BigDecimal value) {
            addCriterion("discount_amount >", value, "discountAmount");
            return (Criteria) this;
        }

        public Criteria andDiscountAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("discount_amount >=", value, "discountAmount");
            return (Criteria) this;
        }

        public Criteria andDiscountAmountLessThan(BigDecimal value) {
            addCriterion("discount_amount <", value, "discountAmount");
            return (Criteria) this;
        }

        public Criteria andDiscountAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("discount_amount <=", value, "discountAmount");
            return (Criteria) this;
        }

        public Criteria andDiscountAmountIn(List<BigDecimal> values) {
            addCriterion("discount_amount in", values, "discountAmount");
            return (Criteria) this;
        }

        public Criteria andDiscountAmountNotIn(List<BigDecimal> values) {
            addCriterion("discount_amount not in", values, "discountAmount");
            return (Criteria) this;
        }

        public Criteria andDiscountAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("discount_amount between", value1, value2, "discountAmount");
            return (Criteria) this;
        }

        public Criteria andDiscountAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("discount_amount not between", value1, value2, "discountAmount");
            return (Criteria) this;
        }

        public Criteria andActualAmountIsNull() {
            addCriterion("actual_amount is null");
            return (Criteria) this;
        }

        public Criteria andActualAmountIsNotNull() {
            addCriterion("actual_amount is not null");
            return (Criteria) this;
        }

        public Criteria andActualAmountEqualTo(BigDecimal value) {
            addCriterion("actual_amount =", value, "actualAmount");
            return (Criteria) this;
        }

        public Criteria andActualAmountNotEqualTo(BigDecimal value) {
            addCriterion("actual_amount <>", value, "actualAmount");
            return (Criteria) this;
        }

        public Criteria andActualAmountGreaterThan(BigDecimal value) {
            addCriterion("actual_amount >", value, "actualAmount");
            return (Criteria) this;
        }

        public Criteria andActualAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("actual_amount >=", value, "actualAmount");
            return (Criteria) this;
        }

        public Criteria andActualAmountLessThan(BigDecimal value) {
            addCriterion("actual_amount <", value, "actualAmount");
            return (Criteria) this;
        }

        public Criteria andActualAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("actual_amount <=", value, "actualAmount");
            return (Criteria) this;
        }

        public Criteria andActualAmountIn(List<BigDecimal> values) {
            addCriterion("actual_amount in", values, "actualAmount");
            return (Criteria) this;
        }

        public Criteria andActualAmountNotIn(List<BigDecimal> values) {
            addCriterion("actual_amount not in", values, "actualAmount");
            return (Criteria) this;
        }

        public Criteria andActualAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("actual_amount between", value1, value2, "actualAmount");
            return (Criteria) this;
        }

        public Criteria andActualAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("actual_amount not between", value1, value2, "actualAmount");
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

        public Criteria andStatusEqualTo(String value) {
            addCriterion("`status` =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(String value) {
            addCriterion("`status` <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(String value) {
            addCriterion("`status` >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(String value) {
            addCriterion("`status` >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(String value) {
            addCriterion("`status` <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(String value) {
            addCriterion("`status` <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLike(String value) {
            addCriterion("`status` like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotLike(String value) {
            addCriterion("`status` not like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<String> values) {
            addCriterion("`status` in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<String> values) {
            addCriterion("`status` not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(String value1, String value2) {
            addCriterion("`status` between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(String value1, String value2) {
            addCriterion("`status` not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andCustomerIdIsNull() {
            addCriterion("customer_id is null");
            return (Criteria) this;
        }

        public Criteria andCustomerIdIsNotNull() {
            addCriterion("customer_id is not null");
            return (Criteria) this;
        }

        public Criteria andCustomerIdEqualTo(String value) {
            addCriterion("customer_id =", value, "customerId");
            return (Criteria) this;
        }

        public Criteria andCustomerIdNotEqualTo(String value) {
            addCriterion("customer_id <>", value, "customerId");
            return (Criteria) this;
        }

        public Criteria andCustomerIdGreaterThan(String value) {
            addCriterion("customer_id >", value, "customerId");
            return (Criteria) this;
        }

        public Criteria andCustomerIdGreaterThanOrEqualTo(String value) {
            addCriterion("customer_id >=", value, "customerId");
            return (Criteria) this;
        }

        public Criteria andCustomerIdLessThan(String value) {
            addCriterion("customer_id <", value, "customerId");
            return (Criteria) this;
        }

        public Criteria andCustomerIdLessThanOrEqualTo(String value) {
            addCriterion("customer_id <=", value, "customerId");
            return (Criteria) this;
        }

        public Criteria andCustomerIdLike(String value) {
            addCriterion("customer_id like", value, "customerId");
            return (Criteria) this;
        }

        public Criteria andCustomerIdNotLike(String value) {
            addCriterion("customer_id not like", value, "customerId");
            return (Criteria) this;
        }

        public Criteria andCustomerIdIn(List<String> values) {
            addCriterion("customer_id in", values, "customerId");
            return (Criteria) this;
        }

        public Criteria andCustomerIdNotIn(List<String> values) {
            addCriterion("customer_id not in", values, "customerId");
            return (Criteria) this;
        }

        public Criteria andCustomerIdBetween(String value1, String value2) {
            addCriterion("customer_id between", value1, value2, "customerId");
            return (Criteria) this;
        }

        public Criteria andCustomerIdNotBetween(String value1, String value2) {
            addCriterion("customer_id not between", value1, value2, "customerId");
            return (Criteria) this;
        }

        public Criteria andCustomerNameIsNull() {
            addCriterion("customer_name is null");
            return (Criteria) this;
        }

        public Criteria andCustomerNameIsNotNull() {
            addCriterion("customer_name is not null");
            return (Criteria) this;
        }

        public Criteria andCustomerNameEqualTo(String value) {
            addCriterion("customer_name =", value, "customerName");
            return (Criteria) this;
        }

        public Criteria andCustomerNameNotEqualTo(String value) {
            addCriterion("customer_name <>", value, "customerName");
            return (Criteria) this;
        }

        public Criteria andCustomerNameGreaterThan(String value) {
            addCriterion("customer_name >", value, "customerName");
            return (Criteria) this;
        }

        public Criteria andCustomerNameGreaterThanOrEqualTo(String value) {
            addCriterion("customer_name >=", value, "customerName");
            return (Criteria) this;
        }

        public Criteria andCustomerNameLessThan(String value) {
            addCriterion("customer_name <", value, "customerName");
            return (Criteria) this;
        }

        public Criteria andCustomerNameLessThanOrEqualTo(String value) {
            addCriterion("customer_name <=", value, "customerName");
            return (Criteria) this;
        }

        public Criteria andCustomerNameLike(String value) {
            addCriterion("customer_name like", value, "customerName");
            return (Criteria) this;
        }

        public Criteria andCustomerNameNotLike(String value) {
            addCriterion("customer_name not like", value, "customerName");
            return (Criteria) this;
        }

        public Criteria andCustomerNameIn(List<String> values) {
            addCriterion("customer_name in", values, "customerName");
            return (Criteria) this;
        }

        public Criteria andCustomerNameNotIn(List<String> values) {
            addCriterion("customer_name not in", values, "customerName");
            return (Criteria) this;
        }

        public Criteria andCustomerNameBetween(String value1, String value2) {
            addCriterion("customer_name between", value1, value2, "customerName");
            return (Criteria) this;
        }

        public Criteria andCustomerNameNotBetween(String value1, String value2) {
            addCriterion("customer_name not between", value1, value2, "customerName");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderIsNull() {
            addCriterion("customer_gender is null");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderIsNotNull() {
            addCriterion("customer_gender is not null");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderEqualTo(String value) {
            addCriterion("customer_gender =", value, "customerGender");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderNotEqualTo(String value) {
            addCriterion("customer_gender <>", value, "customerGender");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderGreaterThan(String value) {
            addCriterion("customer_gender >", value, "customerGender");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderGreaterThanOrEqualTo(String value) {
            addCriterion("customer_gender >=", value, "customerGender");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderLessThan(String value) {
            addCriterion("customer_gender <", value, "customerGender");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderLessThanOrEqualTo(String value) {
            addCriterion("customer_gender <=", value, "customerGender");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderLike(String value) {
            addCriterion("customer_gender like", value, "customerGender");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderNotLike(String value) {
            addCriterion("customer_gender not like", value, "customerGender");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderIn(List<String> values) {
            addCriterion("customer_gender in", values, "customerGender");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderNotIn(List<String> values) {
            addCriterion("customer_gender not in", values, "customerGender");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderBetween(String value1, String value2) {
            addCriterion("customer_gender between", value1, value2, "customerGender");
            return (Criteria) this;
        }

        public Criteria andCustomerGenderNotBetween(String value1, String value2) {
            addCriterion("customer_gender not between", value1, value2, "customerGender");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberIsNull() {
            addCriterion("customer_phone_number is null");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberIsNotNull() {
            addCriterion("customer_phone_number is not null");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberEqualTo(String value) {
            addCriterion("customer_phone_number =", value, "customerPhoneNumber");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberNotEqualTo(String value) {
            addCriterion("customer_phone_number <>", value, "customerPhoneNumber");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberGreaterThan(String value) {
            addCriterion("customer_phone_number >", value, "customerPhoneNumber");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberGreaterThanOrEqualTo(String value) {
            addCriterion("customer_phone_number >=", value, "customerPhoneNumber");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberLessThan(String value) {
            addCriterion("customer_phone_number <", value, "customerPhoneNumber");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberLessThanOrEqualTo(String value) {
            addCriterion("customer_phone_number <=", value, "customerPhoneNumber");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberLike(String value) {
            addCriterion("customer_phone_number like", value, "customerPhoneNumber");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberNotLike(String value) {
            addCriterion("customer_phone_number not like", value, "customerPhoneNumber");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberIn(List<String> values) {
            addCriterion("customer_phone_number in", values, "customerPhoneNumber");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberNotIn(List<String> values) {
            addCriterion("customer_phone_number not in", values, "customerPhoneNumber");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberBetween(String value1, String value2) {
            addCriterion("customer_phone_number between", value1, value2, "customerPhoneNumber");
            return (Criteria) this;
        }

        public Criteria andCustomerPhoneNumberNotBetween(String value1, String value2) {
            addCriterion("customer_phone_number not between", value1, value2, "customerPhoneNumber");
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

        public Criteria andIsDeleteIsNull() {
            addCriterion("is_delete is null");
            return (Criteria) this;
        }

        public Criteria andIsDeleteIsNotNull() {
            addCriterion("is_delete is not null");
            return (Criteria) this;
        }

        public Criteria andIsDeleteEqualTo(Byte value) {
            addCriterion("is_delete =", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteNotEqualTo(Byte value) {
            addCriterion("is_delete <>", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteGreaterThan(Byte value) {
            addCriterion("is_delete >", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteGreaterThanOrEqualTo(Byte value) {
            addCriterion("is_delete >=", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteLessThan(Byte value) {
            addCriterion("is_delete <", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteLessThanOrEqualTo(Byte value) {
            addCriterion("is_delete <=", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteIn(List<Byte> values) {
            addCriterion("is_delete in", values, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteNotIn(List<Byte> values) {
            addCriterion("is_delete not in", values, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteBetween(Byte value1, Byte value2) {
            addCriterion("is_delete between", value1, value2, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteNotBetween(Byte value1, Byte value2) {
            addCriterion("is_delete not between", value1, value2, "isDelete");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdIsNull() {
            addCriterion("seckill_register_recode_id is null");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdIsNotNull() {
            addCriterion("seckill_register_recode_id is not null");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdEqualTo(String value) {
            addCriterion("seckill_register_recode_id =", value, "seckillRegisterRecodeId");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdNotEqualTo(String value) {
            addCriterion("seckill_register_recode_id <>", value, "seckillRegisterRecodeId");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdGreaterThan(String value) {
            addCriterion("seckill_register_recode_id >", value, "seckillRegisterRecodeId");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdGreaterThanOrEqualTo(String value) {
            addCriterion("seckill_register_recode_id >=", value, "seckillRegisterRecodeId");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdLessThan(String value) {
            addCriterion("seckill_register_recode_id <", value, "seckillRegisterRecodeId");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdLessThanOrEqualTo(String value) {
            addCriterion("seckill_register_recode_id <=", value, "seckillRegisterRecodeId");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdLike(String value) {
            addCriterion("seckill_register_recode_id like", value, "seckillRegisterRecodeId");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdNotLike(String value) {
            addCriterion("seckill_register_recode_id not like", value, "seckillRegisterRecodeId");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdIn(List<String> values) {
            addCriterion("seckill_register_recode_id in", values, "seckillRegisterRecodeId");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdNotIn(List<String> values) {
            addCriterion("seckill_register_recode_id not in", values, "seckillRegisterRecodeId");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdBetween(String value1, String value2) {
            addCriterion("seckill_register_recode_id between", value1, value2, "seckillRegisterRecodeId");
            return (Criteria) this;
        }

        public Criteria andSeckillRegisterRecodeIdNotBetween(String value1, String value2) {
            addCriterion("seckill_register_recode_id not between", value1, value2, "seckillRegisterRecodeId");
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