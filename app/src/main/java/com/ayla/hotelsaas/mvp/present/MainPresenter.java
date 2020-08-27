package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.MainView;
import com.ayla.hotelsaas.mvp.view.RoomOrderView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class MainPresenter extends BasePresenter<MainView> {

    public void getAuthCode(String RoomId) {
        RequestModel.getInstance().getAuthCode(RoomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxjavaObserver<String>() {
                    @Override
                    public void _onNext(String data) {

                        mView.getAuthCodeSuccess(data);
                    }

                    @Override
                    public void _onError(String code, String msg) {

                        mView.getAuthCodeFail(code,msg);
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addSubscrebe(d);
                    }
                });
    }
}
