package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.ui.CustomToast;
import com.blankj.utilcode.util.Utils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;

public abstract class UnifiedErrorConsumer implements Consumer<Throwable> {
    @Override
    public final void accept(Throwable throwable) throws Exception {
        handle(throwable);

        handleDefault(throwable);
    }

    public abstract void handle(Throwable throwable) throws Exception;

    public void handleDefault(Throwable throwable) throws Exception {
        if (throwable instanceof ServerBadException) {
            if (((ServerBadException) throwable).isSuccess()) {
                return;
            }
        }
        CustomToast.makeText(Utils.getApp(), getLocalErrorMsg(throwable), R.drawable.ic_toast_warming);
    }

    private String getLocalErrorMsg(String defaultMsg, Throwable throwable) {
        if (Constance.isNetworkDebug()) {
            throwable.printStackTrace();
        }
        String msg = defaultMsg;
        if (throwable instanceof ConnectException) {
            msg = "网络连接失败，请检查网络";
        } else if (throwable instanceof UnknownHostException) {
            msg = "网络连接失败，请检查网络";
        } else if (throwable instanceof ServerBadException) {
            String serverMsg = ((ServerBadException) throwable).getMsg();

            if (serverMsg != null && !serverMsg.contains(" ")) {//不包含空格，可能就是中文字符串了。
                msg = serverMsg;
            }

            switch (((ServerBadException) throwable).getCode()) {
                case "122001":
                    msg = "登录过期";
                    break;
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
        } else if (throwable instanceof SelfMsgException) {
            msg = throwable.getLocalizedMessage();
        } else if (throwable instanceof SocketTimeoutException) {
            msg = "请求超时，请稍后重试";
        }
        return msg;
    }

    public final String getLocalErrorMsg(Throwable throwable) {
        return getLocalErrorMsg(getDefaultErrorMsg(), throwable);
    }

    public String getDefaultErrorMsg() {
        return "操作失败";
    }
}
