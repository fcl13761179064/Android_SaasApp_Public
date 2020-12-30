package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.data.net.UnifiedErrorConsumer;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.ForgitView;

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
public class ForgitPresenter extends BasePresenter<ForgitView> {

    public void modifyforgit(String user_name, String yanzhengma) {
        Disposable subscribe = RequestModel.getInstance().modifyForgitPassword(user_name, yanzhengma)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        mView.showProgress("修改中...");
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
                        mView.modifyPasswordSuccess(aBoolean);
                    }
                }, new UnifiedErrorConsumer() {
                    @Override
                    public void handleDefault(Throwable throwable) throws Exception {

                    }

                    @Override
                    public void handle(Throwable throwable) throws Exception {
                        mView.modifyPasswordFailed(getLocalErrorMsg(throwable));
                    }
                });
        addSubscrebe(subscribe);
    }

    public void send_sms(String iphone_youxiang) {
        Disposable subscribe = RequestModel.getInstance().send_sms(iphone_youxiang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        mView.showProgress("发送中...");
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
                        mView.sendCodeSuccess(null);
                    }
                }, new UnifiedErrorConsumer() {
                    @Override
                    public void handleDefault(Throwable throwable) throws Exception {

                    }

                    @Override
                    public void handle(Throwable throwable) throws Exception {
                        mView.sendCodeFailed(getLocalErrorMsg(throwable));
                    }
                });
        addSubscrebe(subscribe);
    }

    public void reset_Password(String phone, String new_password) {
        Disposable subscribe = RequestModel.getInstance().resert_passwoed(phone, new_password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        mView.showProgress("重置中...");
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
                        mView.resertPasswordSuccess(aBoolean);
                    }
                }, new UnifiedErrorConsumer() {
                    @Override
                    public void handle(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);
    }
}
