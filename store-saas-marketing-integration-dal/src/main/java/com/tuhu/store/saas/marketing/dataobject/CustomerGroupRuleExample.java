package com.tuhu.store.saas.marketing.dataobject;

import java.util.ArrayList;
import java.util.List;

public class CustomerGroupRuleExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<CustomerGroupRuleExample.Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public CustomerGroupRuleExample() {
        oredCriteria = new ArrayList<CustomerGroupRuleExample.Criteria>();
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

    public List<CustomerGroupRuleExample.Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(CustomerGroupRuleExample.Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public CustomerGroupRuleExample.Criteria or() {
        CustomerGroupRuleExample.Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public CustomerGroupRuleExample.Criteria createCriteria() {
        CustomerGroupRuleExample.Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected CustomerGroupRuleExample.Criteria createCriteriaInternal() {
        CustomerGroupRuleExample.Criteria criteria = new CustomerGroupRuleExample.Criteria();
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
        protected List<CustomerGroupRuleExample.Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<CustomerGroupRuleExample.Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<CustomerGroupRuleExample.Criterion> getAllCriteria() {
            return criteria;
        }

        public List<CustomerGroupRuleExample.Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new CustomerGroupRuleExample.Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new CustomerGroupRuleExample.Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new CustomerGroupRuleExample.Criterion(condition, value1, value2));
        }

        public CustomerGroupRuleExample.Criteria andStoreIdEqualTo(Long value) {
            addCriterion("store_id =", value, "storeId");
            return (CustomerGroupRuleExample.Criteria) this;
        }

        public CustomerGroupRuleExample.Criteria andGroupIdEqualTo(Long value) {
            addCriterion("group_id =", value, "groupId");
            return (CustomerGroupRuleExample.Criteria) this;
        }

        public CustomerGroupRuleExample.Criteria andStatausEqualTo(String value) {
            addCriterion("status =", value, "status");
            return (CustomerGroupRuleExample.Criteria) this;
        }

        public CustomerGroupRuleExample.Criteria andGroupIdIn(List<Long> values) {
            addCriterion("group_id in", values, "groupId");
            return (CustomerGroupRuleExample.Criteria) this;
        }

    }

    /**
     */
    public static class Criteria extends CustomerGroupRuleExample.GeneratedCriteria {

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
