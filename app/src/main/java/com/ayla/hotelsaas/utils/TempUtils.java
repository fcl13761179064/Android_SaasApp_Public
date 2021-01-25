package com.ayla.hotelsaas.utils;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.data.net.SelfMsgException;
import com.ayla.hotelsaas.data.net.ServerBadException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

public class TempUtils {
    /**
     * 判断设备是否为网关
     *
     * @param devicesBean
     * @return
     */
    public static boolean isDeviceGateway(DeviceListBean.DevicesBean devicesBean) {
        if (devicesBean == null) {
            return false;
        }
        return devicesBean.getBindType() == 0 && devicesBean.getProductType() == 1;
    }

    /**
     * 判断是被是否在线
     *
     * @param devicesBean
     * @return
     */
    public static boolean isDeviceOnline(DeviceListBean.DevicesBean devicesBean) {
        if (devicesBean == null) {
            return false;
        }
        return "ONLINE".equals(devicesBean.getDeviceStatus());
    }

    /**
     * 判断是否为艾拉的红外遥控器 家电虚拟遥控器
     *
     * @param deviceBean
     * @return
     */
    public static boolean isINFRARED_VIRTUAL_SUB_DEVICE(DeviceListBean.DevicesBean deviceBean) {
        if (isSWITCH_PURPOSE_SUB_DEVICE(deviceBean)) {//如果是 开关用途设备
            return false;
        }
        return deviceBean.getDeviceUseType() == 1 && deviceBean.getCuId() == 0;
    }

    /**
     * 判断是否为 开关用途设备
     *
     * @param deviceBean
     * @return
     */
    public static boolean isSWITCH_PURPOSE_SUB_DEVICE(DeviceListBean.DevicesBean deviceBean) {
        return deviceBean.getDeviceUseType() == 1 && deviceBean.getPid() != null && deviceBean.getPid().startsWith("PD-NODE-ABP9-");
    }

    public static String getLocalErrorMsg(String defaultMsg, Throwable throwable) {
        if (Constance.isNetworkDebug()) {
            throwable.printStackTrace();
        }
        String msg = defaultMsg;
        if (throwable instanceof ConnectException) {
            msg = "网络连接失败，请检查网络";
        } else if (throwable instanceof UnknownHostException) {
            msg = "网络连接失败，请检查网络";
        } else if (throwable instanceof HttpException) {
            switch (((HttpException) throwable).code()) {
                case 403:
                    msg = "没有访问权限";
                    break;
            }
        } else if (throwable instanceof ServerBadException) {
            String serverMsg = ((ServerBadException) throwable).getMsg();

            if (serverMsg != null && !serverMsg.contains(" ")) {//不包含空格，可能就是中文字符串了。
                msg = serverMsg;
            }

            if (serverMsg != null && serverMsg.contains("该设备已经绑定，解绑后方能重新绑定")) {
                msg = "该设备已在别处绑定，请先解绑后再重试";
            } else if ("Devices with the same device name under the same resource".equals(serverMsg)) {
                msg = "设备名称已被占用";
            } else if ("PointName already exists".equals(serverMsg)) {
                msg = "设备点位已被占用";
            } else if ("device unbinding error".equals(serverMsg)) {
                msg = "设备解绑失败";
            } else if ("Get device register candidates error,not_found ".equals(serverMsg)) {
                msg = "未发现可绑定的节点设备";
            }

            switch (((ServerBadException) throwable).getCode()) {
                case "122001":
                    msg = "登录过期";
                    break;
                case "210000":
                    msg = "检测到方案和当前房型不一致，请更换方案";
                    break;
            }
        } else if (throwable instanceof SelfMsgException) {
            msg = throwable.getLocalizedMessage();
        } else if (throwable instanceof SocketTimeoutException) {
            msg = "请求超时，请稍后重试";
        }
        return msg;
    }

    public static String getLocalErrorMsg(Throwable throwable) {
        return getLocalErrorMsg("操作失败", throwable);
    }
}
