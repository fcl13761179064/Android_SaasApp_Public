package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.BuildConfig;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.data.net.ServerBadException;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.LoginView;

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
public class LoginPresenter extends BasePresenter<LoginView> {

    public void login(final String account, String password) {
        Disposable subscribe = RequestModel.getInstance().login(account, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        mView.showProgress("登录中...");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        mView.loginSuccess(user);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loginFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    public void checkVersion() {
        Disposable subscribe = RequestModel.getInstance().getAppVersion(BuildConfig.VERSION_CODE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .subscribe(new Consumer<VersionUpgradeBean>() {
                    @Override
                    public void accept(VersionUpgradeBean versionUpgradeBean) throws Exception {
                        mView.checkVersionSuccess(versionUpgradeBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof ServerBadException && ((ServerBadException) throwable).isSuccess()) {
                            mView.checkVersionSuccess(null);
                        } else {
                            mView.checkVersionFailed(throwable);
                        }
                    }
                });
        addSubscrebe(subscribe);
    }
}
