package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.GatewayAddGuideView;

import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GatewayAddGuidePresenter extends BasePresenter<GatewayAddGuideView> {
    /**
     * 通过DSN注册一个设备
     *
     * @param dsn
     */
    public void registerDeviceWithDSN(String dsn) {
        RequestModel.getInstance().bindDeviceWithDSN(dsn, "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxjavaObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void _onNext(Boolean data) {
                        if (new Random().nextBoolean()) {
                            mView.bindSuccess();
                        } else {
                            mView.bindFailed();
                        }
                    }

                    @Override
                    public void _onError(String code, String msg) {

                    }
                });
    }
}
