package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.BuildConfig;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.data.net.ServerBadException;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SplashView;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SplashPresenter extends BasePresenter<SplashView> {

    public void fetchVersionUpdate() {
        //默认的最少等待时间
        long default_delay_time_millis = 1000;
        long start = System.currentTimeMillis();
        Disposable subscribe = RequestModel.getInstance().getAppVersion(BuildConfig.VERSION_CODE)
                .delay(System.currentTimeMillis() - start > default_delay_time_millis ? 0 : default_delay_time_millis - (System.currentTimeMillis() - start), TimeUnit.MILLISECONDS, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VersionUpgradeBean>() {
                    @Override
                    public void accept(VersionUpgradeBean versionUpgradeBean) throws Exception {
                        mView.onVersionResult(true, versionUpgradeBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof ServerBadException && ((ServerBadException) throwable).isSuccess()) {
                            mView.onVersionResult(true, null);
                        } else {
                            mView.onVersionResult(false, null);
                        }
                    }
                });
        addSubscrebe(subscribe);
    }
}
