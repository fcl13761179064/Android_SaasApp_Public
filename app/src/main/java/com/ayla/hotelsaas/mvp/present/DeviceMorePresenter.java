package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;
import com.ayla.hotelsaas.mvp.view.LoginView;
import com.ayla.hotelsaas.utils.PregnancyUtil;
import com.ayla.hotelsaas.utils.ToastUtil;
import com.ayla.hotelsaas.utils.ToastUtils;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/29
 */
public class DeviceMorePresenter extends BasePresenter<DeviceMoreView> {

    public void deviceRenameMethod(String dsn, String nickName) {
        RequestModel.getInstance().deviceRename(dsn, nickName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("修改中...");
                    }
                })
                .subscribe(new RxjavaObserver<Boolean>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);

                    }

                    @Override
                    public void _onNext(Boolean data) {
                        mView.hideProgress();
                        mView.operateSuccess(data);
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.hideProgress();
                        mView.operateError(msg);
                    }
                });
    }


    public void deviceRemove(String deviceId, long scopeId,String scopeType) {
        RequestModel.getInstance().deviceRemove(deviceId, scopeId,scopeType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("移除中...");
                    }
                })
                .subscribe(new RxjavaObserver<Boolean>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);

                    }

                    @Override
                    public void _onNext(Boolean data) {
                        mView.hideProgress();
                        mView.operateRemoveSuccess(data);
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.hideProgress();
                        mView.operateMoveFailSuccess(code,msg);
                    }
                });
    }


}
