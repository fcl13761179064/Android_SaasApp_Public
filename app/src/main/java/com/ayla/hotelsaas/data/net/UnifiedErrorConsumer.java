package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.utils.TempUtils;
import com.blankj.utilcode.util.Utils;

import io.reactivex.functions.Consumer;

@Deprecated
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

    public final String getLocalErrorMsg(Throwable throwable) {
        return TempUtils.getLocalErrorMsg(getDefaultErrorMsg(), throwable);
    }

    public String getDefaultErrorMsg() {
        return "操作失败";
    }
}
