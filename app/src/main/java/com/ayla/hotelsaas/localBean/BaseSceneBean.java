package com.ayla.hotelsaas.localBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseSceneBean implements Serializable {
    protected long ruleId;

    protected long scopeId;

    protected String ruleName;//一键执行或者自动化规则的名字

    protected String ruleDescription;

    protected String iconPath;

    protected int ruleType;//1:自动化 2:一键执行

    protected int siteType;//1:本地 2:云端

    protected int ruleSetMode;//    ALL(2,"多条条件全部命中")   ANY(3,"多条条件任一命中")

    protected int status;//0:不可用 1：可用 2：异常 3：待激活

    protected List<Condition> conditions;

    protected List<Action> actions;

    protected EnableTime enableTime;//生效时间段

    protected Map<String, Object> ruleExtendData;

    public BaseSceneBean(int siteType) {
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

    public int getRuleType() {
        return ruleType;
    }

    public void setRuleType(int ruleType) {
        this.ruleType = ruleType;
    }

    public int getSiteType() {
        return siteType;
    }

    public void setSiteType(int siteType) {
        this.siteType = siteType;
    }

    public int getRuleSetMode() {
        return ruleSetMode;
    }

    public void setRuleSetMode(int ruleSetMode) {
        this.ruleSetMode = ruleSetMode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public Map<String, Object> getRuleExtendData() {
        return ruleExtendData;
    }

    public void setRuleExtendData(Map<String, Object> ruleExtendData) {
        this.ruleExtendData = ruleExtendData;
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
        private int sourceDeviceType;//0:艾拉 1：阿里

        private Integer bit;
        private Integer compareValue;

        public Integer getBit() {
            return bit;
        }

        public void setBit(Integer bit) {
            this.bit = bit;
        }

        public Integer getCompareValue() {
            return compareValue;
        }

        public void setCompareValue(Integer compareValue) {
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

        public int getSourceDeviceType() {
            return sourceDeviceType;
        }

        public void setSourceDeviceType(int sourceDeviceType) {
            this.sourceDeviceType = sourceDeviceType;
        }
    }

    public static class Action implements Serializable {
        private int targetDeviceType;

        private String targetDeviceId;

        private String leftValue;

        private String operator;

        private String rightValue;

        private int rightValueType;//0:场景类型 1:文本类型 2:整型类型  3:单精度  4:双精度 5:时间戳   6:bool类型    8:Long类型

        private String functionName;//显示的功能名称
        private String valueName;//显示的值名称
        private String iconpath;//在自动化联动里面设置一键执行icon

        public String getIconpath() {
            return iconpath;
        }

        public void setIconpath(String iconpath) {
            this.iconpath = iconpath;
        }

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

    public static class DeviceAction extends Action implements Serializable {
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

    /**
     * 一键执行添加到自动化里面
     */
    public static class AddOneKeyRuleList extends Action {
        public AddOneKeyRuleList() {
            setOperator("==");
            setRightValueType(0);
            setTargetDeviceType(7);
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

    public interface SITE_TYPE {
        int LOCAL = 1, REMOTE = 2;
    }

    public interface RULE_TYPE {
        int AUTO = 1, ONE_KEY = 2, SCENE_KEY = 4;

    }

    public interface RULE_SET_MODE {
        int ALL = 2, ANY = 3;

    }
}
