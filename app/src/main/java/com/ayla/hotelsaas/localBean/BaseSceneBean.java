package com.ayla.hotelsaas.localBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseSceneBean implements Serializable {
    protected long ruleId;

    protected long scopeId;

    protected String ruleName;

    protected String ruleDescription;

    protected String iconPath;

    protected RULE_TYPE ruleType;//1:自动化 2:一键执行

    protected SITE_TYPE siteType;//1:本地 2:云端

    protected RULE_SET_MODE ruleSetMode;//    ALL(2,"多条条件全部命中")   ANY(3,"多条条件任一命中")

    protected boolean enable;//0:不可用 1：可用

    protected List<Condition> conditions;

    protected List<Action> actions;

    protected EnableTime enableTime;//生效时间段

    public BaseSceneBean(SITE_TYPE siteType) {
        this.siteType = siteType;
        conditions = new ArrayList<>();
        actions = new ArrayList<>();
    }

    public long getRuleId() {
        return ruleId;
    }

    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
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

    public RULE_TYPE getRuleType() {
        return ruleType;
    }

    public void setRuleType(RULE_TYPE ruleType) {
        this.ruleType = ruleType;
    }

    public SITE_TYPE getSiteType() {
        return siteType;
    }

    public void setSiteType(SITE_TYPE siteType) {
        this.siteType = siteType;
    }

    public RULE_SET_MODE getRuleSetMode() {
        return ruleSetMode;
    }

    public void setRuleSetMode(RULE_SET_MODE ruleSetMode) {
        this.ruleSetMode = ruleSetMode;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public EnableTime getEnableTime() {
        return enableTime;
    }

    public void setEnableTime(EnableTime enableTime) {
        this.enableTime = enableTime;
    }

    public abstract static class Condition implements Serializable {
        private String functionName;//显示的功能名称
        private String valueName;//显示的值名称

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public String getValueName() {
            return valueName;
        }

        public void setValueName(String valueName) {
            this.valueName = valueName;
        }
    }

    public static class OneKeyCondition extends Condition {

    }

    /**
     * 普通property
     */
    public static class DeviceCondition extends Condition {

        private String operator;
        private String leftValue;
        private String rightValue;
        private String sourceDeviceId;
        private DeviceType sourceDeviceType;//0:艾拉 1：阿里

        private int bit;
        private int compareValue;

        public int getBit() {
            return bit;
        }

        public void setBit(int bit) {
            this.bit = bit;
        }

        public int getCompareValue() {
            return compareValue;
        }

        public void setCompareValue(int compareValue) {
            this.compareValue = compareValue;
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

        public DeviceType getSourceDeviceType() {
            return sourceDeviceType;
        }

        public void setSourceDeviceType(DeviceType sourceDeviceType) {
            this.sourceDeviceType = sourceDeviceType;
        }
    }

    public abstract static class Action implements Serializable {
        private DeviceType targetDeviceType;

        private String targetDeviceId;

        private String leftValue;

        private String operator;

        private String rightValue;

        private int rightValueType;//0:场景类型 1:文本类型 2:整型类型  3:单精度  4:双精度 5:时间戳   6:bool类型    8:Long类型

        private String functionName;//显示的功能名称
        private String valueName;//显示的值名称

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public String getValueName() {
            return valueName;
        }

        public void setValueName(String valueName) {
            this.valueName = valueName;
        }

        public DeviceType getTargetDeviceType() {
            return targetDeviceType;
        }

        public void setTargetDeviceType(DeviceType targetDeviceType) {
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

    public static class DeviceAction extends Action {

    }

    public static class DelayAction extends Action {
        public DelayAction() {
            setOperator("==");
            setLeftValue("SECONDS");
            setRightValueType(8);
            setTargetDeviceId("DELAY");
            setTargetDeviceType(DeviceType.DELAY_ACTION);
        }
    }

    /**
     * 酒店欢迎语动作
     */
    public static class WelcomeAction extends Action {
        public WelcomeAction() {
            setOperator("==");
            setLeftValue("triggerWelcome");
            setRightValueType(7);
            setTargetDeviceId("assistantService");
            setTargetDeviceType(DeviceType.XIAODU_SERVER);
            setRightValue("aylaHotelRoomId");
        }
    }

    public static class EnableTime implements Serializable {
        public EnableTime() {
            isAllDay = true;
            enableWeekDay = new int[]{1, 2, 3, 4, 5, 6, 7};
        }

        int[] enableWeekDay;
        int startHour, startMinute, endHour, endMinute;
        boolean isAllDay;

        public int[] getEnableWeekDay() {
            return enableWeekDay;
        }

        public void setEnableWeekDay(int[] enableWeekDay) {
            this.enableWeekDay = enableWeekDay;
        }

        public int getStartHour() {
            return startHour;
        }

        public void setStartHour(int startHour) {
            this.startHour = startHour;
        }

        public int getStartMinute() {
            return startMinute;
        }

        public void setStartMinute(int startMinute) {
            this.startMinute = startMinute;
        }

        public int getEndHour() {
            return endHour;
        }

        public void setEndHour(int endHour) {
            this.endHour = endHour;
        }

        public int getEndMinute() {
            return endMinute;
        }

        public void setEndMinute(int endMinute) {
            this.endMinute = endMinute;
        }

        public boolean isAllDay() {
            return isAllDay;
        }

        public void setAllDay(boolean allDay) {
            isAllDay = allDay;
        }
    }

    public enum SITE_TYPE implements Serializable {
        LOCAL(1), REMOTE(2);

        public int code;

        SITE_TYPE(int code) {
            this.code = code;
        }
    }

    public enum RULE_TYPE implements Serializable {
        AUTO(1), ONE_KEY(2);

        public int code;

        RULE_TYPE(int code) {
            this.code = code;
        }
    }

    public enum RULE_SET_MODE implements Serializable {
        ALL(2), ANY(3);

        public int code;

        RULE_SET_MODE(int code) {
            this.code = code;
        }
    }
}
