package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.ui.CustomToast;

import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;

public abstract class UnifiedErrorConsumer<T> implements Consumer<T> {
    @Override
    public final void accept(T throwable) throws Exception {
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

    public abstract void handle(T throwable) throws Exception;

    public boolean shouldHandleDefault(T throwable) {
        return true;
    }

    public void handleUnknownHostException(UnknownHostException exception) {
        CustomToast.makeText(R.string.request_not_connect, R.drawable.ic_toast_warming);
    }

    public void handleServerBadException(ServerBadException exception) {
        CustomToast.makeText(exception.getMsg(), R.drawable.ic_toast_warming);
    }

    public void handleOtherException(T throwable) {

    }
}
