package com.ayla.hotelsaas.data.net;


import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.utils.NetworkUtils;

import io.reactivex.Observer;


/**
 * @description 自定义Rxjava2_Observer    Observable处理使用
 * @user dingjunwei
 * @date 2017/7/2
 */
public abstract class RxjavaObserver<T> implements Observer<BaseResult<T>> {

    @Override
    public void onNext(BaseResult<T> result) {
        if (result.isSuccess()) {
            _onNext(result.data);
        } else {
            _onError(result.code, result.error);
        }
    }

    @Override
    public void onError(Throwable t) {
        if (t instanceof RxjavaFlatmapThrowable) {
            RxjavaFlatmapThrowable throwable = (RxjavaFlatmapThrowable) t;
            _onFlatmap(throwable);
            _onError(throwable.getCode(), throwable.getMsg());
        } else if (!NetworkUtils.isNetworkAvailable(MyApplication.getContext())) {
            _onError("", MyApplication.getResource().getString(R.string.request_not_connect));
        } else {
            _onError("", MyApplication.getResource().getString(R.string.request_error));
        }
    }

    @Override
    public void onComplete() {
    }

    protected void _onFlatmap(RxjavaFlatmapThrowable t) {

    }

    public abstract void _onNext(T data);

    public abstract void _onError(String code, String msg);
}
