/*
 * create by cairurui on 2/25/19 3:15 PM.
 * Copyright (c) 2019 SunseaIoT. All rights reserved.
 */

package com.ayla.hotelsaas.webview.bridge.param;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.HashMap;

import static com.ayla.hotelsaas.webview.bridge.LarkBridgeConsts.RESPONSE_CODE_SUCCESS;
import static com.ayla.hotelsaas.webview.bridge.LarkBridgeConsts.RESPONSE_MSG_SUCCESS;


/**
 * Lark桥接传递参数
 */
public abstract class LarkBridgeParam {
    public LarkBridgeParam(boolean successed) {
        if (successed) {
            appendParamItem("code", RESPONSE_CODE_SUCCESS);
            appendParamItem("msg", RESPONSE_MSG_SUCCESS);
        } else {
            appendParamItem("code", 0);
            appendParamItem("msg", "error");
        }
    }

    /**
     * 参数集合
     */
    protected HashMap<String, Object> params = new HashMap<>();

    /**
     * 输出json格式的字符串
     *
     * @return
     */
    public String toJson() {
        return new Gson().toJson(this);
    }

    /**
     * 添加一个参数到参数集合中
     *
     * @param key
     * @param value
     * @return
     */
    public void appendParamItem(String key, Object value) {
        if(params == null){
            params = new HashMap<>();
        }
        params.put(key, value);
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    /**
     * 获取参数集合中的指定名称的整数数值
     *
     * @param key 项目名称
     * @return
     */
    public int intValueFromParams(String key) {
        int result = 0;
        String temp = stringValueFromParams(key);
        if (!TextUtils.isEmpty(temp)) {
            try {
                result = Integer.parseInt(temp);
            } catch (NumberFormatException e) {
            }
        }
        return result;
    }

    /**
     * 获取参数集合中的指定名称的浮点数值
     *
     * @param key 项目名称
     * @return
     */
    public float floatValueFromParams(String key) {
        float result = 0f;
        String temp = stringValueFromParams(key);
        if (!TextUtils.isEmpty(temp)) {
            try {
                result = Float.parseFloat(temp);
            } catch (NumberFormatException e) {
            }
        }
        return result;
    }

    /**
     * 获取参数集合中的指定名称的布尔数值
     *
     * @param key 项目名称
     * @return
     */
    public boolean booleanValueFromParams(String key) {
        boolean result = false;
        String temp = stringValueFromParams(key);
        if (!TextUtils.isEmpty(temp)) {
            result = Boolean.parseBoolean(temp);
        }
        return result;
    }

    /**
     * 获取参数集合中的指定名称的数值
     *
     * @param key 项目名称
     * @return
     */
    public Object objectValueFromParams(String key) {
        if (!TextUtils.isEmpty(key) && params != null) {
            Object o = params.get(key);
            if (o instanceof Double) {
                Double value = (Double) o;
                if (value % 1 == 0) {
                    o = value.longValue();
                }
            }
            return o;
        }
        return null;
    }

    /**
     * 获取参数集合中的指定名称的字符串数值
     *
     * @param key 项目名称
     * @return
     */
    public String stringValueFromParams(String key) {
        Object value = objectValueFromParams(key);
        if (value != null) {
            return value.toString();
        }
        return "";
    }
}