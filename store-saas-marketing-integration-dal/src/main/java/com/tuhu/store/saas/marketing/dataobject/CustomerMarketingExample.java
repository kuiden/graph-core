package com.tuhu.store.saas.marketing.dataobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerMarketingExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public CustomerMarketingExample() {
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

        public Criteria andTaskTypeIsNull() {
            addCriterion("task_type is null");
            return (Criteria) this;
        }

        public Criteria andTaskTypeIsNotNull() {
            addCriterion("task_type is not null");
            return (Criteria) this;
        }

        public Criteria andTaskTypeEqualTo(Byte value) {
            addCriterion("task_type =", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeNotEqualTo(Byte value) {
            addCriterion("task_type <>", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeGreaterThan(Byte value) {
            addCriterion("task_type >", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("task_type >=", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeLessThan(Byte value) {
            addCriterion("task_type <", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeLessThanOrEqualTo(Byte value) {
            addCriterion("task_type <=", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeIn(List<Byte> values) {
            addCriterion("task_type in", values, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeNotIn(List<Byte> values) {
            addCriterion("task_type not in", values, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeBetween(Byte value1, Byte value2) {
            addCriterion("task_type between", value1, value2, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("task_type not between", value1, value2, "taskType");
            return (Criteria) this;
        }

        public Criteria andMarketingMethodIsNull() {
            addCriterion("marketing_method is null");
            return (Criteria) this;
        }

        public Criteria andMarketingMethodIsNotNull() {
            addCriterion("marketing_method is not null");
            return (Criteria) this;
        }

        public Criteria andMarketingMethodEqualTo(Byte value) {
            addCriterion("marketing_method =", value, "marketingMethod");
            return (Criteria) this;
        }

        public Criteria andMarketingMethodNotEqualTo(Byte value) {
            addCriterion("marketing_method <>", value, "marketingMethod");
            return (Criteria) this;
        }

        public Criteria andMarketingMethodGreaterThan(Byte value) {
            addCriterion("marketing_method >", value, "marketingMethod");
            return (Criteria) this;
        }

        public Criteria andMarketingMethodGreaterThanOrEqualTo(Byte value) {
            addCriterion("marketing_method >=", value, "marketingMethod");
            return (Criteria) this;
        }

        public Criteria andMarketingMethodLessThan(Byte value) {
            addCriterion("marketing_method <", value, "marketingMethod");
            return (Criteria) this;
        }

        public Criteria andMarketingMethodLessThanOrEqualTo(Byte value) {
            addCriterion("marketing_method <=", value, "marketingMethod");
            return (Criteria) this;
        }

        public Criteria andMarketingMethodIn(List<Byte> values) {
            addCriterion("marketing_method in", values, "marketingMethod");
            return (Criteria) this;
        }

        public Criteria andMarketingMethodNotIn(List<Byte> values) {
            addCriterion("marketing_method not in", values, "marketingMethod");
            return (Criteria) this;
        }

        public Criteria andMarketingMethodBetween(Byte value1, Byte value2) {
            addCriterion("marketing_method between", value1, value2, "marketingMethod");
            return (Criteria) this;
        }

        public Criteria andMarketingMethodNotBetween(Byte value1, Byte value2) {
            addCriterion("marketing_method not between", value1, value2, "marketingMethod");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdIsNull() {
            addCriterion("customer_group_id is null");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdIsNotNull() {
            addCriterion("customer_group_id is not null");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdEqualTo(String value) {
            addCriterion("customer_group_id =", value, "customerGroupId");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdNotEqualTo(String value) {
            addCriterion("customer_group_id <>", value, "customerGroupId");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdGreaterThan(String value) {
            addCriterion("customer_group_id >", value, "customerGroupId");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdGreaterThanOrEqualTo(String value) {
            addCriterion("customer_group_id >=", value, "customerGroupId");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdLessThan(String value) {
            addCriterion("customer_group_id <", value, "customerGroupId");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdLessThanOrEqualTo(String value) {
            addCriterion("customer_group_id <=", value, "customerGroupId");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdLike(String value) {
            addCriterion("customer_group_id like", value, "customerGroupId");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdNotLike(String value) {
            addCriterion("customer_group_id not like", value, "customerGroupId");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdIn(List<String> values) {
            addCriterion("customer_group_id in", values, "customerGroupId");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdNotIn(List<String> values) {
            addCriterion("customer_group_id not in", values, "customerGroupId");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdBetween(String value1, String value2) {
            addCriterion("customer_group_id between", value1, value2, "customerGroupId");
            return (Criteria) this;
        }

        public Criteria andCustomerGroupIdNotBetween(String value1, String value2) {
            addCriterion("customer_group_id not between", value1, value2, "customerGroupId");
            return (Criteria) this;
        }

        public Criteria andSendTimeIsNull() {
            addCriterion("send_time is null");
            return (Criteria) this;
        }

        public Criteria andSendTimeIsNotNull() {
            addCriterion("send_time is not null");
            return (Criteria) this;
        }

        public Criteria andSendTimeEqualTo(Date value) {
            addCriterion("send_time =", value, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeNotEqualTo(Date value) {
            addCriterion("send_time <>", value, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeGreaterThan(Date value) {
            addCriterion("send_time >", value, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("send_time >=", value, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeLessThan(Date value) {
            addCriterion("send_time <", value, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeLessThanOrEqualTo(Date value) {
            addCriterion("send_time <=", value, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeIn(List<Date> values) {
            addCriterion("send_time in", values, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeNotIn(List<Date> values) {
            addCriterion("send_time not in", values, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeBetween(Date value1, Date value2) {
            addCriterion("send_time between", value1, value2, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeNotBetween(Date value1, Date value2) {
            addCriterion("send_time not between", value1, value2, "sendTime");
            return (Criteria) this;
        }

        public Criteria andCouponTitleIsNull() {
            addCriterion("coupon_title is null");
            return (Criteria) this;
        }

        public Criteria andCouponTitleIsNotNull() {
            addCriterion("coupon_title is not null");
            return (Criteria) this;
        }

        public Criteria andCouponTitleEqualTo(String value) {
            addCriterion("coupon_title =", value, "couponTitle");
            return (Criteria) this;
        }

        public Criteria andCouponTitleNotEqualTo(String value) {
            addCriterion("coupon_title <>", value, "couponTitle");
            return (Criteria) this;
        }

        public Criteria andCouponTitleGreaterThan(String value) {
            addCriterion("coupon_title >", value, "couponTitle");
            return (Criteria) this;
        }

        public Criteria andCouponTitleGreaterThanOrEqualTo(String value) {
            addCriterion("coupon_title >=", value, "couponTitle");
            return (Criteria) this;
        }

        public Criteria andCouponTitleLessThan(String value) {
            addCriterion("coupon_title <", value, "couponTitle");
            return (Criteria) this;
        }

        public Criteria andCouponTitleLessThanOrEqualTo(String value) {
            addCriterion("coupon_title <=", value, "couponTitle");
            return (Criteria) this;
        }

        public Criteria andCouponTitleLike(String value) {
            addCriterion("coupon_title like", value, "couponTitle");
            return (Criteria) this;
        }

        public Criteria andCouponTitleNotLike(String value) {
            addCriterion("coupon_title not like", value, "couponTitle");
            return (Criteria) this;
        }

        public Criteria andCouponTitleIn(List<String> values) {
            addCriterion("coupon_title in", values, "couponTitle");
            return (Criteria) this;
        }

        public Criteria andCouponTitleNotIn(List<String> values) {
            addCriterion("coupon_title not in", values, "couponTitle");
            return (Criteria) this;
        }

        public Criteria andCouponTitleBetween(String value1, String value2) {
            addCriterion("coupon_title between", value1, value2, "couponTitle");
            return (Criteria) this;
        }

        public Criteria andCouponTitleNotBetween(String value1, String value2) {
            addCriterion("coupon_title not between", value1, value2, "couponTitle");
            return (Criteria) this;
        }

        public Criteria andCouponMessageFlagIsNull() {
            addCriterion("coupon_message_flag is null");
            return (Criteria) this;
        }

        public Criteria andCouponMessageFlagIsNotNull() {
            addCriterion("coupon_message_flag is not null");
            return (Criteria) this;
        }

        public Criteria andCouponMessageFlagEqualTo(Byte value) {
            addCriterion("coupon_message_flag =", value, "couponMessageFlag");
            return (Criteria) this;
        }

        public Criteria andCouponMessageFlagNotEqualTo(Byte value) {
            addCriterion("coupon_message_flag <>", value, "couponMessageFlag");
            return (Criteria) this;
        }

        public Criteria andCouponMessageFlagGreaterThan(Byte value) {
            addCriterion("coupon_message_flag >", value, "couponMessageFlag");
            return (Criteria) this;
        }

        public Criteria andCouponMessageFlagGreaterThanOrEqualTo(Byte value) {
            addCriterion("coupon_message_flag >=", value, "couponMessageFlag");
            return (Criteria) this;
        }

        public Criteria andCouponMessageFlagLessThan(Byte value) {
            addCriterion("coupon_message_flag <", value, "couponMessageFlag");
            return (Criteria) this;
        }

        public Criteria andCouponMessageFlagLessThanOrEqualTo(Byte value) {
            addCriterion("coupon_message_flag <=", value, "couponMessageFlag");
            return (Criteria) this;
        }

        public Criteria andCouponMessageFlagIn(List<Byte> values) {
            addCriterion("coupon_message_flag in", values, "couponMessageFlag");
            return (Criteria) this;
        }

        public Criteria andCouponMessageFlagNotIn(List<Byte> values) {
            addCriterion("coupon_message_flag not in", values, "couponMessageFlag");
            return (Criteria) this;
        }

        public Criteria andCouponMessageFlagBetween(Byte value1, Byte value2) {
            addCriterion("coupon_message_flag between", value1, value2, "couponMessageFlag");
            return (Criteria) this;
        }

        public Criteria andCouponMessageFlagNotBetween(Byte value1, Byte value2) {
            addCriterion("coupon_message_flag not between", value1, value2, "couponMessageFlag");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdIsNull() {
            addCriterion("message_template_id is null");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdIsNotNull() {
            addCriterion("message_template_id is not null");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdEqualTo(String value) {
            addCriterion("message_template_id =", value, "messageTemplateId");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdNotEqualTo(String value) {
            addCriterion("message_template_id <>", value, "messageTemplateId");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdGreaterThan(String value) {
            addCriterion("message_template_id >", value, "messageTemplateId");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdGreaterThanOrEqualTo(String value) {
            addCriterion("message_template_id >=", value, "messageTemplateId");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdLessThan(String value) {
            addCriterion("message_template_id <", value, "messageTemplateId");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdLessThanOrEqualTo(String value) {
            addCriterion("message_template_id <=", value, "messageTemplateId");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdLike(String value) {
            addCriterion("message_template_id like", value, "messageTemplateId");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdNotLike(String value) {
            addCriterion("message_template_id not like", value, "messageTemplateId");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdIn(List<String> values) {
            addCriterion("message_template_id in", values, "messageTemplateId");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdNotIn(List<String> values) {
            addCriterion("message_template_id not in", values, "messageTemplateId");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdBetween(String value1, String value2) {
            addCriterion("message_template_id between", value1, value2, "messageTemplateId");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIdNotBetween(String value1, String value2) {
            addCriterion("message_template_id not between", value1, value2, "messageTemplateId");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIsNull() {
            addCriterion("message_template is null");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIsNotNull() {
            addCriterion("message_template is not null");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateEqualTo(String value) {
            addCriterion("message_template =", value, "messageTemplate");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateNotEqualTo(String value) {
            addCriterion("message_template <>", value, "messageTemplate");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateGreaterThan(String value) {
            addCriterion("message_template >", value, "messageTemplate");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateGreaterThanOrEqualTo(String value) {
            addCriterion("message_template >=", value, "messageTemplate");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateLessThan(String value) {
            addCriterion("message_template <", value, "messageTemplate");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateLessThanOrEqualTo(String value) {
            addCriterion("message_template <=", value, "messageTemplate");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateLike(String value) {
            addCriterion("message_template like", value, "messageTemplate");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateNotLike(String value) {
            addCriterion("message_template not like", value, "messageTemplate");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateIn(List<String> values) {
            addCriterion("message_template in", values, "messageTemplate");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateNotIn(List<String> values) {
            addCriterion("message_template not in", values, "messageTemplate");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateBetween(String value1, String value2) {
            addCriterion("message_template between", value1, value2, "messageTemplate");
            return (Criteria) this;
        }

        public Criteria andMessageTemplateNotBetween(String value1, String value2) {
            addCriterion("message_template not between", value1, value2, "messageTemplate");
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

        public Criteria andSendObjectIsNull() {
            addCriterion("send_object is null");
            return (Criteria) this;
        }

        public Criteria andSendObjectIsNotNull() {
            addCriterion("send_object is not null");
            return (Criteria) this;
        }

        public Criteria andSendObjectEqualTo(String value) {
            addCriterion("send_object =", value, "sendObject");
            return (Criteria) this;
        }

        public Criteria andSendObjectNotEqualTo(String value) {
            addCriterion("send_object <>", value, "sendObject");
            return (Criteria) this;
        }

        public Criteria andSendObjectGreaterThan(String value) {
            addCriterion("send_object >", value, "sendObject");
            return (Criteria) this;
        }

        public Criteria andSendObjectGreaterThanOrEqualTo(String value) {
            addCriterion("send_object >=", value, "sendObject");
            return (Criteria) this;
        }

        public Criteria andSendObjectLessThan(String value) {
            addCriterion("send_object <", value, "sendObject");
            return (Criteria) this;
        }

        public Criteria andSendObjectLessThanOrEqualTo(String value) {
            addCriterion("send_object <=", value, "sendObject");
            return (Criteria) this;
        }

        public Criteria andSendObjectLike(String value) {
            addCriterion("send_object like", value, "sendObject");
            return (Criteria) this;
        }

        public Criteria andSendObjectNotLike(String value) {
            addCriterion("send_object not like", value, "sendObject");
            return (Criteria) this;
        }

        public Criteria andSendObjectIn(List<String> values) {
            addCriterion("send_object in", values, "sendObject");
            return (Criteria) this;
        }

        public Criteria andSendObjectNotIn(List<String> values) {
            addCriterion("send_object not in", values, "sendObject");
            return (Criteria) this;
        }

        public Criteria andSendObjectBetween(String value1, String value2) {
            addCriterion("send_object between", value1, value2, "sendObject");
            return (Criteria) this;
        }

        public Criteria andSendObjectNotBetween(String value1, String value2) {
            addCriterion("send_object not between", value1, value2, "sendObject");
            return (Criteria) this;
        }

        public Criteria andCouponIdIsNull() {
            addCriterion("coupon_id is null");
            return (Criteria) this;
        }

        public Criteria andCouponIdIsNotNull() {
            addCriterion("coupon_id is not null");
            return (Criteria) this;
        }

        public Criteria andCouponIdEqualTo(String value) {
            addCriterion("coupon_id =", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdNotEqualTo(String value) {
            addCriterion("coupon_id <>", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdGreaterThan(String value) {
            addCriterion("coupon_id >", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdGreaterThanOrEqualTo(String value) {
            addCriterion("coupon_id >=", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdLessThan(String value) {
            addCriterion("coupon_id <", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdLessThanOrEqualTo(String value) {
            addCriterion("coupon_id <=", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdLike(String value) {
            addCriterion("coupon_id like", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdNotLike(String value) {
            addCriterion("coupon_id not like", value, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdIn(List<String> values) {
            addCriterion("coupon_id in", values, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdNotIn(List<String> values) {
            addCriterion("coupon_id not in", values, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdBetween(String value1, String value2) {
            addCriterion("coupon_id between", value1, value2, "couponId");
            return (Criteria) this;
        }

        public Criteria andCouponIdNotBetween(String value1, String value2) {
            addCriterion("coupon_id not between", value1, value2, "couponId");
            return (Criteria) this;
        }

        public Criteria andMessageDatasIsNull() {
            addCriterion("message_datas is null");
            return (Criteria) this;
        }

        public Criteria andMessageDatasIsNotNull() {
            addCriterion("message_datas is not null");
            return (Criteria) this;
        }

        public Criteria andMessageDatasEqualTo(String value) {
            addCriterion("message_datas =", value, "messageDatas");
            return (Criteria) this;
        }

        public Criteria andMessageDatasNotEqualTo(String value) {
            addCriterion("message_datas <>", value, "messageDatas");
            return (Criteria) this;
        }

        public Criteria andMessageDatasGreaterThan(String value) {
            addCriterion("message_datas >", value, "messageDatas");
            return (Criteria) this;
        }

        public Criteria andMessageDatasGreaterThanOrEqualTo(String value) {
            addCriterion("message_datas >=", value, "messageDatas");
            return (Criteria) this;
        }

        public Criteria andMessageDatasLessThan(String value) {
            addCriterion("message_datas <", value, "messageDatas");
            return (Criteria) this;
        }

        public Criteria andMessageDatasLessThanOrEqualTo(String value) {
            addCriterion("message_datas <=", value, "messageDatas");
            return (Criteria) this;
        }

        public Criteria andMessageDatasLike(String value) {
            addCriterion("message_datas like", value, "messageDatas");
            return (Criteria) this;
        }

        public Criteria andMessageDatasNotLike(String value) {
            addCriterion("message_datas not like", value, "messageDatas");
            return (Criteria) this;
        }

        public Criteria andMessageDatasIn(List<String> values) {
            addCriterion("message_datas in", values, "messageDatas");
            return (Criteria) this;
        }

        public Criteria andMessageDatasNotIn(List<String> values) {
            addCriterion("message_datas not in", values, "messageDatas");
            return (Criteria) this;
        }

        public Criteria andMessageDatasBetween(String value1, String value2) {
            addCriterion("message_datas between", value1, value2, "messageDatas");
            return (Criteria) this;
        }

        public Criteria andMessageDatasNotBetween(String value1, String value2) {
            addCriterion("message_datas not between", value1, value2, "messageDatas");
            return (Criteria) this;
        }

        public Criteria andCouponCodeIsNull() {
            addCriterion("coupon_code is null");
            return (Criteria) this;
        }

        public Criteria andCouponCodeIsNotNull() {
            addCriterion("coupon_code is not null");
            return (Criteria) this;
        }

        public Criteria andCouponCodeEqualTo(String value) {
            addCriterion("coupon_code =", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeNotEqualTo(String value) {
            addCriterion("coupon_code <>", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeGreaterThan(String value) {
            addCriterion("coupon_code >", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeGreaterThanOrEqualTo(String value) {
            addCriterion("coupon_code >=", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeLessThan(String value) {
            addCriterion("coupon_code <", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeLessThanOrEqualTo(String value) {
            addCriterion("coupon_code <=", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeLike(String value) {
            addCriterion("coupon_code like", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeNotLike(String value) {
            addCriterion("coupon_code not like", value, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeIn(List<String> values) {
            addCriterion("coupon_code in", values, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeNotIn(List<String> values) {
            addCriterion("coupon_code not in", values, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeBetween(String value1, String value2) {
            addCriterion("coupon_code between", value1, value2, "couponCode");
            return (Criteria) this;
        }

        public Criteria andCouponCodeNotBetween(String value1, String value2) {
            addCriterion("coupon_code not between", value1, value2, "couponCode");
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
