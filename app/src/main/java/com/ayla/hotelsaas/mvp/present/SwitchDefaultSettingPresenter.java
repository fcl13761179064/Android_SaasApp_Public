package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.PropertyDataPointBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SwitchDefaultSettingView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/29
 */
public class SwitchDefaultSettingPresenter extends BasePresenter<SwitchDefaultSettingView> {
    public void getPropertyDataPoint(String deviceId, String propertyName) {
        Disposable subscribe = RequestModel.getInstance().getPropertyDataPoint(deviceId, propertyName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<PropertyDataPointBean>() {
                    @Override
                    public void accept(PropertyDataPointBean propertyDataPointBean) throws Exception {
                        mView.showData(propertyDataPointBean.getPropertyValue());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showError(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    public void updateProperty(String deviceId, String propertyName, String propertyValue) {
        Disposable subscribe = RequestModel.getInstance().updateProperty(deviceId, propertyName, propertyValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<BaseResult<Boolean>>() {
                    @Override
                    public void accept(BaseResult<Boolean> booleanBaseResult) throws Exception {
                        mView.updateSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showError(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
