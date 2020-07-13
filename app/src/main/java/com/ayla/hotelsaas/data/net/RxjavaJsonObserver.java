package com.ayla.hotelsaas.data.net;

import android.text.TextUtils;
import android.util.Log;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.utils.Logger;
import com.ayla.hotelsaas.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;
import io.reactivex.Observer;

/**
 * @description 自定义Rxjava2_Observer    返回JSONObject
 * @user dingjunwei
 * @date 2017/7/2
 */
public abstract class RxjavaJsonObserver implements Observer<String> {

    @Override
    public void onNext(String str) {
        try {
            JSONObject json = new JSONObject(str);
            if (json.optString("code").equals("0000")) {
                if (!TextUtils.isEmpty(json.optString("data"))) {
                    if (null == json.optJSONObject("data")) {
                        _onNext(json);
                    } else {
                        _onNext(json.getJSONObject("data"));
                    }
                } else {
                    _onNext(new JSONObject());
                }
            } else {
                _onError(json.optString("code"), json.getString("error"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            _onError("", MyApplication.getResource().getString(R.string.request_error));
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
        Logger.d("error",t.getMsg());
    }

    public abstract void _onNext(JSONObject data);

    public abstract void _onError(String code, String msg);
}
