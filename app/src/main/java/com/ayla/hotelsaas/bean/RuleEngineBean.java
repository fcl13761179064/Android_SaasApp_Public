package com.ayla.hotelsaas.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 联动实体Bean
 */
public class RuleEngineBean implements Serializable {

    /**
     * scopeId : 1280390357498073088
     * ruleName : 第一条测试规则
     * ruleType : 2
     * condition : {"expression":"","items":[]}
     * action : {"expression":"func.execute('2','GADw3NnUI4Xa54nsr5tYz20000','StatusLightSwitch')","items":[{"targetDeviceType":2,"targetDeviceId":"GADw3NnUI4Xa54nsr5tYz20000","leftValue":"StatusLightSwitch","operator":"==","rightValue":1,"rightValueType":1}]}
     */
    private Long ruleId;

    private long scopeId;

    private String ruleName;

    private String ruleDescription;

    private String iconPath;

    private int ruleType;//1:自动化 2:一键执行

    private int siteType;//1:本地 2:云端

    private int status;//0:不可用 1：可用

    private Condition condition;

    private Action action;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public long getScopeId() {
        return scopeId;
    }

    public void setScopeId(long scopeId) {
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

    public int getSiteType() {
        return siteType;
    }

    public void setSiteType(int siteType) {
        this.siteType = siteType;
    }

    public static class Condition implements Serializable{
        /**
         * expression :
         * items : []
         */

        private String expression;

        private List<ConditionItem> items;

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public List<ConditionItem> getItems() {
            return items;
        }

        public void setItems(List<ConditionItem> items) {
            this.items = items;
        }

        public static class ConditionItem implements Serializable{

            /**
             * joinType : 0
             * operator : ==
             * leftValue : PowerSwitch_1
             * rightValue : 0
             * sourceDeviceId : YY7MbSa6SPU09qumPGxb000000
             * sourceDeviceType : 2
             */

            private int joinType;//0:无逻辑运算连接符 1:并且  2:或者
            private String operator;
            private String leftValue;
            private String rightValue;
            private String sourceDeviceId;
            private int sourceDeviceType;//1:艾拉 2：阿里

            public int getJoinType() {
                return joinType;
            }

            public void setJoinType(int joinType) {
                this.joinType = joinType;
            }

            public String getOperator() {
                return operator;
            }

            public void setOperator(String operator) {
                this.operator = operator;
            }

            public String getLeftValue() {
                return leftValue;
            }

            public void setLeftValue(String leftValue) {
                this.leftValue = leftValue;
            }

            public String getRightValue() {
                return rightValue;
            }

            public void setRightValue(String rightValue) {
                this.rightValue = rightValue;
            }

            public String getSourceDeviceId() {
                return sourceDeviceId;
            }

            public void setSourceDeviceId(String sourceDeviceId) {
                this.sourceDeviceId = sourceDeviceId;
            }

            public int getSourceDeviceType() {
                return sourceDeviceType;
            }

            public void setSourceDeviceType(int sourceDeviceType) {
                this.sourceDeviceType = sourceDeviceType;
            }
        }
    }

    public static class Action implements Serializable{
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

        public static class ActionItem implements Serializable{
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

            private int rightValueType;//1:数值类型 2:action类型  3:文本类型  4:场景类型

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
