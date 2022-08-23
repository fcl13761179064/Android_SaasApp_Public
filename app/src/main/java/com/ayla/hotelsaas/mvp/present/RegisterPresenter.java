package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.data.net.UnifiedErrorConsumer;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.RegisterView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2017/8/2
 */
public class RegisterPresenter extends BasePresenter<RegisterView> {
    public void register(String user_name, final String account, String password) {
        Disposable subscribe = RequestModel.getInstance()
                .register(user_name, account, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        mView.showProgress("注册中...");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mView.RegistSuccess(aBoolean);
                    }
                }, new UnifiedErrorConsumer() {
                    @Override
                    public void handleDefault(Throwable throwable) throws Exception {

                    }

                    @Override
                    public void handle(Throwable throwable) {
                        mView.registerFailed(getLocalErrorMsg(throwable));
                    }
                });
        addSubscrebe(subscribe);
    }
}
