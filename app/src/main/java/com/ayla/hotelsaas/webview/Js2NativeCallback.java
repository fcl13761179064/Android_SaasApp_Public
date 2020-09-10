/*
 * create by cairurui on 1/7/19 3:45 PM.
 * Copyright (c) 2019 SunseaIoT. All rights reserved.
 */

package com.ayla.hotelsaas.webview;

/**
 * 负责larkjs和APP之间的交互通信实现
 */
public interface Js2NativeCallback {
    String getCurrentLanguage();

    String getLarkCloudToken();

    void pushDeviceMoreInfo(String dsn);

    void pushCommonTimer(String dsn, String actionName, String schPredicate);

    void pushCycleTimer(String dsn);

    void pushAddDevice();

    /**
     * 保存数据条目
     *
     * @param key
     * @param value
     */
    void storageSet(String key, String value);

    /**
     * 读取保存的数据条目
     *
     * @param key
     * @return
     */
    String storageGet(String key);

    /**
     * 删除数据条目
     *
     * @param key
     */
    void storageDelete(String key);

    /**
     * 打开新页面
     *
     * @param h5PagePath
     */
    void pushCustomWebView(String h5PagePath);

    /**
     * 打开设备群组更多页面
     *
     * @param groupId   设备群组的编号
     * @param groupName 设备群组的名称
     */
    void pushDeviceGroupMoreInfo(String groupId, String groupName);

    /**
     * H5修改了设备属性，会通过这个接口通知到APP层，APP层如果采用遥控器模式，可以提前更新UI。
     *
     * @param dsn
     * @param propertyName
     * @param propertyValue
     */
    void notifyDevicePropertyChanged(String dsn, String propertyName, Object propertyValue);

    /**
     * H5修改了设备群组开关属性，会通过这个接口通知到APP层，APP层如果采用遥控器模式，可以提前更新UI。
     *  @param groupId
     * @param switchState
     */
    void notifyDeviceGroupSwitchChanged(int groupId, boolean switchState);

    /**
     * 打开短信报警页面
     *
     * @param dsn
     * @param displayName  页面名称
     * @param subTitle     副标题
     * @param propertyName 属性名
     * @param triggerType  触发类型[on_change,compare_absolute,always]
     * @param compareType  比对类型[>=,==,<=,>,<]，triggerType=compare_absolute时必选。
     * @param value        期望值，triggerType=compare_absolute时必选。
     * @param content      短信内容
     */
    void pushSmsAlarm(String dsn, String displayName, String subTitle, String propertyName, String triggerType, String compareType, String value, String content);
}
