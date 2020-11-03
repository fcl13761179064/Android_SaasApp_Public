package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.LoginView;
import com.ayla.hotelsaas.ui.CustomToast;
import com.blankj.utilcode.util.RegexUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
            CustomToast.makeText(MyApplication.getContext(), "登录账号不能为空", R.drawable.ic_toast_warming).show();
            mView.errorShake(1, 2, "");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            CustomToast.makeText(MyApplication.getContext(), "登陆密码不能为空", R.drawable.ic_toast_warming).show();
            mView.errorShake(2, 2, "");
            return;
        }

        if (RegexUtils.isEmail(account)) {
            login(account, password);
        } else if (RegexUtils.isMobileSimple(account)) {
            login(account, password);
        } else {
            CustomToast.makeText(MyApplication.getContext(), R.string.account_error, R.drawable.ic_toast_warming).show();
        }
    }


    private void login(final String account, String password) {
        RequestModel.getInstance().login(account, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                        mView.loginSuccess(data);

                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.errorShake(0, 2, msg);
                        mView.hideProgress();
                    }
                });
    }
}
