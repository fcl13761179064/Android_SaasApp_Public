package com.ayla.hotelsaas.utils;

import android.text.TextUtils;

import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.localBean.DeviceType;
import com.ayla.hotelsaas.localBean.LocalSceneBean;
import com.ayla.hotelsaas.localBean.RemoteSceneBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * http bean对象 装换成UIbean对象的工具类
 */
public class BeanObtainCompactUtil {
    public static BaseSceneBean obtainSceneBean(RuleEngineBean ruleEngineBean) {
        BaseSceneBean sceneBean = null;
        int siteType = ruleEngineBean.getSiteType();
        if (siteType == 1) {//本地
            sceneBean = new LocalSceneBean();
            ((LocalSceneBean) sceneBean).setTargetGateway(ruleEngineBean.getTargetGateway());
            ((LocalSceneBean) sceneBean).setTargetGatewayType(ruleEngineBean.getTargetGatewayType() == 0 ? DeviceType.AYLA_DEVICE_ID : DeviceType.ALI_DEVICE_ID);
        } else {//云端
            sceneBean = new RemoteSceneBean();
        }
        sceneBean.setRuleId(ruleEngineBean.getRuleId());
        sceneBean.setScopeId(ruleEngineBean.getScopeId());
        sceneBean.setIconPath(ruleEngineBean.getIconPath());
        sceneBean.setEnable(ruleEngineBean.getStatus() == 1);
        sceneBean.setRuleSetMode(ruleEngineBean.getRuleSetMode() == 2 ? BaseSceneBean.RULE_SET_MODE.ALL : BaseSceneBean.RULE_SET_MODE.ANY);
        sceneBean.setRuleType(ruleEngineBean.getRuleType() == 1 ? BaseSceneBean.RULE_TYPE.AUTO : BaseSceneBean.RULE_TYPE.ONE_KEY);
        sceneBean.setRuleDescription(ruleEngineBean.getRuleDescription());
        sceneBean.setRuleName(ruleEngineBean.getRuleName());

        BaseSceneBean.EnableTime enableTime = new BaseSceneBean.EnableTime();
        if (sceneBean.getRuleType() == BaseSceneBean.RULE_TYPE.AUTO) {//当是自动化时，就要解析生效时间段
            if (ruleEngineBean.getCondition() != null) {
                if (ruleEngineBean.getCondition().getItems() != null) {
                    for (RuleEngineBean.Condition.ConditionItem conditionItem : ruleEngineBean.getCondition().getItems()) {
                        String cronExpression = conditionItem.getCronExpression();
                        if (!TextUtils.isEmpty(cronExpression) && cronExpression.startsWith("1 ")) {
                            cronExpression = cronExpression.substring(2);
                            boolean isAllDay = false;
                            int[] enableWeekDay = new int[0];
                            int startHour = 0, startMinute = 0, endHour = 0, endMinute = 0;
                            String[] split = cronExpression.split(" ");
                            String weeks = split[0];
                            if ("*".equals(weeks)) {
                                enableWeekDay = new int[]{1, 2, 3, 4, 5, 6, 7};
                            } else {
                                String[] week = weeks.split(",");
                                enableWeekDay = new int[week.length];
                                for (int i = 0; i < week.length; i++) {
                                    enableWeekDay[i] = Integer.parseInt(week[i]);
                                }
                            }

                            String time = split[1];
                            if ("*:*".equals(time)) {
                                isAllDay = true;
                            } else {
                                startHour = Integer.parseInt(time.substring(0, 2));
                                startMinute = Integer.parseInt(time.substring(3, 5));
                                endHour = Integer.parseInt(time.substring(6, 8));
                                endMinute = Integer.parseInt(time.substring(9, 11));
                            }
                            enableTime.setAllDay(isAllDay);
                            enableTime.setEnableWeekDay(enableWeekDay);
                            enableTime.setStartHour(startHour);
                            enableTime.setStartMinute(startMinute);
                            enableTime.setEndHour(endHour);
                            enableTime.setEndMinute(endMinute);
                            break;
                        }
                    }
                }
            }
        }
        sceneBean.setEnableTime(enableTime);

        {//赋值条件集合
            if (ruleEngineBean.getCondition() != null) {
                if (ruleEngineBean.getCondition().getItems() != null) {
                    for (RuleEngineBean.Condition.ConditionItem conditionItem : ruleEngineBean.getCondition().getItems()) {
                        if (!TextUtils.isEmpty(conditionItem.getSourceDeviceId())) {
                            BaseSceneBean.DeviceCondition condition = new BaseSceneBean.DeviceCondition();
                            condition.setLeftValue(conditionItem.getLeftValue());
                            condition.setOperator(conditionItem.getOperator());
                            condition.setRightValue(conditionItem.getRightValue());
                            condition.setSourceDeviceId(conditionItem.getSourceDeviceId());
                            condition.setSourceDeviceType(DeviceType.valueOf(conditionItem.getSourceDeviceType()));
                            sceneBean.getConditions().add(condition);
                        }
                    }
                }
            }
            if (ruleEngineBean.getRuleType() == BaseSceneBean.RULE_TYPE.ONE_KEY.code) {
                BaseSceneBean.OneKeyCondition condition = new BaseSceneBean.OneKeyCondition();
                sceneBean.getConditions().add(condition);
            }
        }
        {//赋值动作集合
            if (ruleEngineBean.getAction() != null) {
                if (ruleEngineBean.getAction().getItems() != null) {
                    for (RuleEngineBean.Action.ActionItem actionItem : ruleEngineBean.getAction().getItems()) {
                        BaseSceneBean.Action action;
                        if (actionItem.getTargetDeviceType() == 10) {//delay动作
                            action = new BaseSceneBean.DelayAction();
                        } else {//普通设备动作
                            action = new BaseSceneBean.DeviceAction();
                        }
                        action.setLeftValue(actionItem.getLeftValue());
                        action.setOperator(actionItem.getOperator());
                        action.setRightValue(actionItem.getRightValue());
                        action.setTargetDeviceId(actionItem.getTargetDeviceId());
                        action.setTargetDeviceType(DeviceType.valueOf(actionItem.getTargetDeviceType()));
                        action.setRightValueType(actionItem.getRightValueType());
                        sceneBean.getActions().add(action);
                    }
                }
            }
        }
        return sceneBean;
    }

    public static RuleEngineBean obtainRuleEngineBean(BaseSceneBean baseSceneBean) {
        RuleEngineBean ruleEngineBean = new RuleEngineBean();
        ruleEngineBean.setRuleId(baseSceneBean.getRuleId());
        ruleEngineBean.setRuleSetMode(baseSceneBean.getRuleSetMode().code);
        ruleEngineBean.setStatus(baseSceneBean.isEnable() ? 1 : 0);
        ruleEngineBean.setIconPath(baseSceneBean.getIconPath());
        ruleEngineBean.setSiteType(baseSceneBean.getSiteType().code);
        ruleEngineBean.setRuleType(baseSceneBean.getRuleType().code);
        ruleEngineBean.setRuleDescription(baseSceneBean.getRuleDescription());
        ruleEngineBean.setRuleName(baseSceneBean.getRuleName());
        ruleEngineBean.setScopeId(baseSceneBean.getScopeId());
        if (baseSceneBean instanceof LocalSceneBean) {
            ruleEngineBean.setTargetGateway(((LocalSceneBean) baseSceneBean).getTargetGateway());
            ruleEngineBean.setTargetGatewayType(((LocalSceneBean) baseSceneBean).getTargetGatewayType().code);
        }
        if (baseSceneBean.getRuleType() == BaseSceneBean.RULE_TYPE.AUTO) {//构建条件集合，只有当自动化联动才传入conditions。
            RuleEngineBean.Condition _condition = new RuleEngineBean.Condition();
            ruleEngineBean.setCondition(_condition);
            _condition.setItems(new ArrayList<>());
            for (int i = 0; i < baseSceneBean.getConditions().size(); i++) {
                BaseSceneBean.Condition condition = baseSceneBean.getConditions().get(i);
                if (condition instanceof BaseSceneBean.DeviceCondition) {
                    RuleEngineBean.Condition.ConditionItem conditionItem = new RuleEngineBean.Condition.ConditionItem();
                    conditionItem.setSourceDeviceId(((BaseSceneBean.DeviceCondition) condition).getSourceDeviceId());
                    conditionItem.setSourceDeviceType(((BaseSceneBean.DeviceCondition) condition).getSourceDeviceType().code);
                    conditionItem.setRightValue(((BaseSceneBean.DeviceCondition) condition).getRightValue());
                    conditionItem.setLeftValue(((BaseSceneBean.DeviceCondition) condition).getLeftValue());
                    conditionItem.setOperator(((BaseSceneBean.DeviceCondition) condition).getOperator());
                    _condition.getItems().add(conditionItem);
                }
            }
            for (int i = 0; i < _condition.getItems().size(); i++) {
                RuleEngineBean.Condition.ConditionItem conditionItem = _condition.getItems().get(i);
                if (i == 0) {
                    conditionItem.setJoinType(0);
                } else {
                    if (baseSceneBean.getRuleSetMode() == BaseSceneBean.RULE_SET_MODE.ALL) {
                        conditionItem.setJoinType(1);
                    } else {
                        conditionItem.setJoinType(2);
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ruleEngineBean.getCondition().getItems().size(); i++) {
                if (i == 0) {
                    sb.append("(");
                }
                RuleEngineBean.Condition.ConditionItem conditionItem = ruleEngineBean.getCondition().getItems().get(i);
                sb.append(conditionItem.getJoinType() == 1 ? " && " : conditionItem.getJoinType() == 2 ? " || " : "");
                sb.append(String.format("func.get('%s','%s','%s') %s %s", conditionItem.getSourceDeviceType(), conditionItem.getSourceDeviceId(), conditionItem.getLeftValue(), conditionItem.getOperator(), conditionItem.getRightValue()));
                if (i == ruleEngineBean.getCondition().getItems().size() - 1) {
                    sb.append(")");
                }
            }

            if (baseSceneBean.getRuleType() == BaseSceneBean.RULE_TYPE.AUTO) {//如果是自动化场景，就把生效时间段传进去
                BaseSceneBean.EnableTime enableTime = baseSceneBean.getEnableTime();
                String cronExpression = calculateCronExpression(enableTime);
                RuleEngineBean.Condition.ConditionItem conditionItem = new RuleEngineBean.Condition.ConditionItem();
                conditionItem.setCronExpression("1 " + cronExpression);
                _condition.getItems().add(conditionItem);

                sb.append(" && ");
                sb.append(String.format("func.parseCronExpression('%s')", conditionItem.getCronExpression()));
            }
            _condition.setExpression(sb.toString());
        }

        {//构建动作集合
            RuleEngineBean.Action _action = new RuleEngineBean.Action();
            ruleEngineBean.setAction(_action);
            _action.setItems(new ArrayList<>());
            for (int i = 0; i < baseSceneBean.getActions().size(); i++) {
                BaseSceneBean.Action action = baseSceneBean.getActions().get(i);
                RuleEngineBean.Action.ActionItem actionItem = new RuleEngineBean.Action.ActionItem();
                actionItem.setTargetDeviceId(action.getTargetDeviceId());
                actionItem.setTargetDeviceType(action.getTargetDeviceType().code);
                actionItem.setRightValue(action.getRightValue());
                actionItem.setLeftValue(action.getLeftValue());
                actionItem.setOperator(action.getOperator());
                actionItem.setRightValueType(action.getRightValueType());
                _action.getItems().add(actionItem);
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ruleEngineBean.getAction().getItems().size(); i++) {
                RuleEngineBean.Action.ActionItem actionItem = ruleEngineBean.getAction().getItems().get(i);
                sb.append(String.format("func.%s('%s','%s','%s')", actionItem.getTargetDeviceType() == 10 ? "delay" : "execute", actionItem.getTargetDeviceType(), actionItem.getTargetDeviceId(), actionItem.getLeftValue()));
                if (i < ruleEngineBean.getAction().getItems().size() - 1) {
                    sb.append(" && ");
                }
            }
            _action.setExpression(sb.toString());
        }
        return ruleEngineBean;
    }

    private static String calculateCronExpression(BaseSceneBean.EnableTime enableTime) {
        StringBuilder sb = new StringBuilder();
        if (enableTime.getEnableWeekDay().length == 7) {
            sb.append("*");
        } else {
            for (int i = 0; i < enableTime.getEnableWeekDay().length; i++) {
                int day = enableTime.getEnableWeekDay()[i];
                sb.append(day);
                if (i < enableTime.getEnableWeekDay().length - 1) {
                    sb.append(",");
                }
            }
        }
        sb.append(" ");
        if (enableTime.isAllDay()) {
            sb.append("*:*");
        } else {
            sb.append(formatTime(enableTime.getStartHour(), enableTime.getStartMinute()));
            sb.append("~");
            sb.append(formatTime(enableTime.getEndHour(), enableTime.getEndMinute()));
        }
        return sb.toString();
    }


    private static String formatTime(int hour, int minute) {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        String format = sf.format(calendar.getTime());
        return format;
    }
}
