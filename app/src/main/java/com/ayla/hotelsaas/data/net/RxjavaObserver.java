package com.ayla.hotelsaas.data.net;


import android.util.Log;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.BaseResult;
import com.blankj.utilcode.util.NetworkUtils;

import io.reactivex.Observer;


/**
 * @description 自定义Rxjava2_Observer    Observable处理使用
 * @user fanchunlei
 * @date 2017/7/2
 */
@Deprecated
public abstract class RxjavaObserver<T> implements Observer<BaseResult<T>> {

    @Override
    public void onNext(BaseResult<T> result) {
        if (result.isSuccess()) {
            _onNext(result.data);
        } else {
            _onError(result.code, result.msg);
        }
    }

    @Override
    public void onError(Throwable t) {
        Log.e("error occur", "onError: ", t);
        if (t instanceof ServerBadException) {
            ServerBadException throwable = (ServerBadException) t;
            _onFlatmap(throwable);
            _onError(throwable.getCode(), throwable.getMsg());
        } else if (!NetworkUtils.isConnected()) {
            _onError("", MyApplication.getResource().getString(R.string.request_not_connect));
        } else {
            _onError("", MyApplication.getResource().getString(R.string.request_error));
        }
    }

    // http://192.168.1.224:8080/api/v1/construction/device/list (844ms)
    @Override
    public void onComplete() {
    }

    protected void _onFlatmap(ServerBadException t) {

    }

    public abstract void _onNext(T data);

    public abstract void _onError(String code, String msg);
}
