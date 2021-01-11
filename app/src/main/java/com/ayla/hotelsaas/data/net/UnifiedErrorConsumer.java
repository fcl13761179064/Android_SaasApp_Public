package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.ui.CustomToast;

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
        CustomToast.makeText(getLocalErrorMsg(throwable), R.drawable.ic_toast_warming);
    }

    public final String getLocalErrorMsg(String defaultMsg, Throwable throwable) {
        String msg = defaultMsg;
        if (throwable instanceof ConnectException) {
            msg = "网络连接失败，请检查网络";
        } else if (throwable instanceof UnknownHostException) {
            msg = "网络连接失败，请检查网络";
        } else if (throwable instanceof ServerBadException) {
            String serverMsg = ((ServerBadException) throwable).getMsg();

            switch (((ServerBadException) throwable).getCode()) {
                case "121001":
                    msg = "登录过期";
                    break;
                case "121004"://登陆账户不存在
                case "121005"://密码错误
                    msg = serverMsg;
                    break;
            }

            if (serverMsg.contains("该设备已经绑定，解绑后方能重新绑定")) {
                msg = "该设备已在别处绑定，请先解绑后再重试";
            } else if ("Devices with the same device name under the same resource".equals(serverMsg)) {
                msg = "设备名称已被占用";
            } else if ("PointName already exists".equals(serverMsg)) {
                msg = "设备点位已被占用";
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
