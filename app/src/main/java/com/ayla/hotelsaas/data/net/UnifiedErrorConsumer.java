package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.ui.CustomToast;

import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;

public abstract class UnifiedErrorConsumer implements Consumer<Throwable> {
    @Override
    public final void accept(Throwable throwable) throws Exception {
        handle(throwable);

        if (shouldHandleDefault(throwable)) {
            if (throwable instanceof UnknownHostException) {
                handleUnknownHostException((UnknownHostException) throwable);
            } else if (throwable instanceof ServerBadException) {
                handleServerBadException((ServerBadException) throwable);
            } else {
                handleOtherException(throwable);
            }
        }
    }

    public abstract void handle(Throwable throwable) throws Exception;

    public boolean shouldHandleDefault(Throwable throwable) {
        return true;
    }

    public void handleUnknownHostException(UnknownHostException exception) {
        CustomToast.makeText(getLocalErrorMsg(exception), R.drawable.ic_toast_warming);
    }

    public void handleServerBadException(ServerBadException exception) {
        if (!exception.isSuccess()) {
            CustomToast.makeText(getLocalErrorMsg(exception), R.drawable.ic_toast_warming);
        }
    }

    public void handleOtherException(Throwable throwable) {
        CustomToast.makeText(getLocalErrorMsg(throwable), R.drawable.ic_toast_warming);
    }

    public final String getLocalErrorMsg(Throwable throwable) {
        String msg = "未知错误";
        if (throwable instanceof UnknownHostException) {
            msg = "网络连接失败，请检查网络";
        }
        if (throwable instanceof ServerBadException) {
            msg = ((ServerBadException) throwable).getMsg();
        }
        return msg;
    }
}
