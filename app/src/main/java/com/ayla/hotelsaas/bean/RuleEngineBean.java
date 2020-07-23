package com.ayla.hotelsaas.bean;

import java.util.List;

/**
 * 联动实体Bean
 */
public class RuleEngineBean {

    /**
     * scopeId : 1280390357498073088
     * ruleName : 第一条测试规则
     * ruleType : 2
     * condition : {"expression":"","items":[]}
     * action : {"expression":"func.execute('2','GADw3NnUI4Xa54nsr5tYz20000','StatusLightSwitch')","items":[{"targetDeviceType":2,"targetDeviceId":"GADw3NnUI4Xa54nsr5tYz20000","leftValue":"StatusLightSwitch","operator":"==","rightValue":1,"rightValueType":1}]}
     */
    private Integer ruleId;

    private Integer scopeId;

    private String ruleName;

    private String ruleDescription;

    private int ruleType;

    private Condition condition;

    private Action action;

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public Integer getScopeId() {
        return scopeId;
    }

    public void setScopeId(Integer scopeId) {
        this.scopeId = scopeId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public int getRuleType() {
        return ruleType;
    }

    public void setRuleType(int ruleType) {
        this.ruleType = ruleType;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public static class Condition {
        /**
         * expression :
         * items : []
         */

        private String expression;

        private List<?> items;

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public List<?> getItems() {
            return items;
        }

        public void setItems(List<?> items) {
            this.items = items;
        }
    }

    public static class Action {
        /**
         * expression : func.execute('2','GADw3NnUI4Xa54nsr5tYz20000','StatusLightSwitch')
         * items : [{"targetDeviceType":2,"targetDeviceId":"GADw3NnUI4Xa54nsr5tYz20000","leftValue":"StatusLightSwitch","operator":"==","rightValue":1,"rightValueType":1}]
         */

        private String expression;

        private List<ActionItem> items;

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public List<ActionItem> getItems() {
            return items;
        }

        public void setItems(List<ActionItem> items) {
            this.items = items;
        }

        public static class ActionItem {
            /**
             * targetDeviceType : 2
             * targetDeviceId : GADw3NnUI4Xa54nsr5tYz20000
             * leftValue : StatusLightSwitch
             * operator : ==
             * rightValue : 1
             * rightValueType : 1
             */

            private int targetDeviceType;

            private String targetDeviceId;

            private String leftValue;

            private String operator;

            private String rightValue;

            private int rightValueType;

            public int getTargetDeviceType() {
                return targetDeviceType;
            }

            public void setTargetDeviceType(int targetDeviceType) {
                this.targetDeviceType = targetDeviceType;
            }

            public String getTargetDeviceId() {
                return targetDeviceId;
            }

            public void setTargetDeviceId(String targetDeviceId) {
                this.targetDeviceId = targetDeviceId;
            }

            public String getLeftValue() {
                return leftValue;
            }

            public void setLeftValue(String leftValue) {
                this.leftValue = leftValue;
            }

            public String getOperator() {
                return operator;
            }

            public void setOperator(String operator) {
                this.operator = operator;
            }

            public String getRightValue() {
                return rightValue;
            }

            public void setRightValue(String rightValue) {
                this.rightValue = rightValue;
            }

            public int getRightValueType() {
                return rightValueType;
            }

            public void setRightValueType(int rightValueType) {
                this.rightValueType = rightValueType;
            }
        }
    }
}
