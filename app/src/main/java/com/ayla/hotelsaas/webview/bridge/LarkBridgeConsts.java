package com.ayla.hotelsaas.webview.bridge;

/**
 * Lark网页桥接的常量定义
 */
public abstract class LarkBridgeConsts {

    /**
     * 请求响应代码：成功。
     */
    public static final int RESPONSE_CODE_SUCCESS = 200;
    /**
     * 请求响应消息：成功。
     */
    public static final String RESPONSE_MSG_SUCCESS = "ok";

    /**
     * 推送方法：倒计时任务被删除
     */
    public static final String PUSH_COUNTDOWN_SCHEDULE_REMOVED = "larkjs.deviceManager.pushRemovedCountDownSchedule";
    /**
     * 推送方法：设备状态发生变化
     */
    public static final String PUSH_DEVICE_PROPERTIES_CHANGED = "larkjs.deviceManager.pushProperty";
    /**
     * 推送方法：设备LAN模式发生变化
     */
    public static final String PUSH_DEVICE_LAN_MODE_CHANGED = "larkjs.deviceManager.pushProperty";
    /**
     * 推送方法：设备名称发生变化
     */
    public static final String PUSH_DEVICE_NAME_CHANGED = "larkjs.deviceManager.pushProperty";
    /**
     * 推送方法：设备添加
     */
    public static final String PUSH_DEVICE_ADDED = "larkjs.deviceManager.pushDevice";
    /**
     * 推送方法：设备被删除
     */
    public static final String PUSH_DEVICE_REMOVED = "larkjs.deviceManager.pushRemovedDevice";
    /**
     * 推送方法：语言发生变化
     */
    public static final String PUSH_APP_LANGUAGE_CHANGED = "larkjs.configManager.notifyLanguageChanged";
    /**
     * 推送方法：推送自定义通知
     */
    public static final String PUSH_CUSTOM_EVENT = "larkjs.notificationManager.pushCustomEvent";

    /**
     * 请求方法：查询语言
     */
    public static final String REQUEST_QUERY_LANGUAGE = "larkjs.notify.app.get.language";
    /**
     * 请求方法：查询cloud服务token
     */
    public static final String REQUEST_QUERY_LARK_CLOUD_TOKEN = "larkjs.notify.app.get.lark_cloud_token";
    /**
     * 请求方法：获取倒计时任务
     */
    public static final String REQUEST_QUERY_COUNTDOWN_SCHEDULE = "larkjs.notify.device.get.countDownSchedule";
    /**
     * 请求方法：创建倒计时任务
     */
    public static final String REQUEST_CREATE_COUNTDOWN_SCHEDULE = "larkjs.notify.device.create.countDownSchedule";
    /**
     * 请求方法：更新倒计时任务
     */
    public static final String REQUEST_UPDATE_COUNTDOWN_SCHEDULE = "larkjs.notify.device.update.countDownSchedule";
    /**
     * 请求方法：设置设备属性
     */
    public static final String REQUEST_SET_DEVICE_PROPERTY = "larkjs.notify.device.set.property";
    /**
     * 请求方法：批量设置设备属性
     */
    public static final String REQUEST_SET_DEVICE_PROPERTIES = "larkjs.notify.device.set.properties";
    /**
     * 请求方法：设置群组设备属性
     */
    public static final String REQUEST_SET_DEVICE_GROUP_PROPERTY = "larkjs.notify.deviceGroup.set.property";
    /**
     * 请求方法：批量设置群组设备属性
     */
    public static final String REQUEST_SET_DEVICE_GROUP_PROPERTIES = "larkjs.notify.deviceGroup.set.properties";
    /**
     * 请求方法：获取设备所有属性
     */
    public static final String REQUEST_QUERY_DEVICE_PROPERTIES = "larkjs.notify.device.get.properties";
    /**
     * 请求方法：获取设备列表
     */
    public static final String REQUEST_QUERY_DEVICE_LIST = "larkjs.notify.device.get.deviceList";
    /**
     * 请求方法：获取设备属性日志
     */
    public static final String REQUEST_QUERY_DEVICE_LOGS = "larkjs.notify.device.get.logs";
    /**
     * 请求方法：获取设备属性日志
     */
    public static final String REQUEST_QUERY_DEVICE_LOGS_WITH_DURATION = "larkjs.notify.device.get.logs.duration";
    /**
     * 请求方法：页面导航进入
     */
    public static final String REQUEST_NAV_PUSH = "larkjs.notify.app.push.controller";
    /**
     * 请求方法：页面导航退出
     */
    public static final String REQUEST_NAV_POP = "larkjs.notify.app.pop.controller";
    /**
     * 页面导航进入的页面：添加设备
     */
    public static final String REQUEST_NAV_PUSH_PAGE_NEW_DEVICE = "pushAddDevice";
    /**
     * 页面导航进入的页面：短信报警
     */
    public static final String REQUEST_NAV_PUSH_PAGE_SMS_ALARM = "pushSmsAlarmPage";
    /**
     * 页面导航进入的页面：设备详情
     */
    public static final String REQUEST_NAV_PUSH_PAGE_DEVICE_DETAIL = "pushDeviceDetail";
    /**
     * 页面导航进入的页面：设备更多信息
     */
    public static final String REQUEST_NAV_PUSH_PAGE_DEVICE_MORE_INFO = "pushDeviceMoreInfo";
    /**
     * 页面导航进入的页面：设备群组更多信息
     */
    public static final String REQUEST_NAV_PUSH_PAGE_DEVICE_GROUP_MORE_INFO = "pushDeviceGroupMoreInfo";
    /**
     * 页面导航进入的页面：普通定时
     */
    public static final String REQUEST_NAV_PUSH_PAGE_COMMON_TIMER = "pushCommonTimer";
    /**
     * 页面导航进入的页面：循环定时
     */
    public static final String REQUEST_NAV_PUSH_PAGE_CYCLE_TIMER = "pushCycleTimer";
    /**
     * 页面导航返回按键
     */
    public static final String REQUEST_NAV_POP_PAGE_DEVICE_DETAIL = "popDeviceDetail";
    /**
     * 页面导航关闭页面
     */
    public static final String REQUEST_NAV_EXIT_PAGE_DEVICE_DETAIL = "exitDeviceDetail";
    /**
     * 页面导航退出的页面：打开新页面
     */
    public static final String REQUEST_NAV_POP_PAGE_CUSTOM_WEBVIEW = "customWebView";

    /**
     * 请求方法：保存数据项目
     */
    public static final String REQUEST_STORAGE_SET = "larkjs.notify.storage.set.property";
    /**
     * 请求方法：读取数据项目
     */
    public static final String REQUEST_STORAGE_GET = "larkjs.notify.storage.get.property";
    /**
     * 请求方法：删除数据项目
     */
    public static final String REQUEST_STORAGE_DELETE = "larkjs.notify.storage.delete.property";
    /**
     * 请求方法：推送自定义通知
     */
    public static final String REQUEST_CUS_EVENT = "larkjs.notify.post.customEvent";

    /**
     * 更新设备元数据
     */
    public static final String REQUEST_DATUM_UPDATE = "larkjs.notify.device.datum.update";
    /**
     * 删除设备元数据
     */
    public static final String REQUEST_DATUM_DELETE = "larkjs.notify.device.datum.delete";
    /**
     * 创建设备元数据
     */
    public static final String REQUEST_DATUM_CREATE = "larkjs.notify.device.datum.create";
    /**
     * 查询设备元数据
     */
    public static final String REQUEST_DATUM_FETCH_LIST = "larkjs.notify.device.datum.fetch_list";
}
