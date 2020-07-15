package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.LoginView;
import com.ayla.hotelsaas.utils.LogUtil;
import com.ayla.hotelsaas.utils.PregnancyUtil;
import com.ayla.hotelsaas.utils.ToastUtil;
import com.ayla.hotelsaas.utils.ToastUtils;

import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2017/8/2
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    public void login() {
        String account = mView.getAccount();
        String password = mView.getPassword();

        if (TextUtils.isEmpty(account)) {
            ToastUtils.showShortToast("登陆账号不能为空");
            mView.errorShake(1, 2);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtils.showShortToast("登陆密码不能为空");
            mView.errorShake(2, 2);
            return;
        }

        if (PregnancyUtil.checkEmail(account)) {
            login(account, password);
        } else if (PregnancyUtil.checkPhoneNum(account)) {
            login(account, password);
        } else {
            ToastUtil.show(MyApplication.getContext(), R.string.account_error);
        }


    }


    private void login(final String account, String password) {
        RequestModel.getInstance().login(account, password)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        mView.showProgress("登录中...");
                    }
                })
                .subscribe(new RxjavaObserver<User>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void _onNext(User data) {
                        mView.hideProgress();
                        MyApplication.getInstance().setUserEntity(data);
                        mView.loginSuccess(data);

                    }

                    @Override
                    public void _onError(String code, String msg) {
                        ToastUtils.showShortToast(msg);
                        mView.errorShake(0, 2);
                        mView.hideProgress();
                    }
                });
    }
}
