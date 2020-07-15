package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.view.GatewayAddGuideView;
import com.ayla.hotelsaas.utils.LogUtil;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
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
        int i = new Random().nextInt(2) + 3;
        Observable.timer(i, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        LogUtil.d(aLong.toString());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (i % 2 == 0) {
                            mView.bindSuccess();
                        } else {
                            mView.bindFailed();
                        }
                    }
                });
    }
}
