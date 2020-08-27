package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.RegisterView;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.utils.PregnancyUtil;

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
public class RegisterPresenter extends BasePresenter<RegisterView> {

    public void register() {
        String userName = mView.getUserName();
        String account = mView.getAccount();
        String password = mView.getPassword();
        if (TextUtils.isEmpty(userName)) {
            CustomToast.makeText(MyApplication.getContext(), "用户名不能为空", R.drawable.ic_toast_warming).show();
            mView.errorShake(1, 2, "");
            return;
        }
        if (TextUtils.isEmpty(account)) {
            CustomToast.makeText(MyApplication.getContext(), "账号不能为空", R.drawable.ic_toast_warming).show();
            mView.errorShake(1, 2, "");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            CustomToast.makeText(MyApplication.getContext(), "密码不能为空", R.drawable.ic_toast_warming).show();
            mView.errorShake(2, 2, "");
            return;
        }
        if (password.length() < 6) {
            CustomToast.makeText(MyApplication.getContext(), "密码长度不能小于6位", R.drawable.ic_toast_warming).show();
            mView.errorShake(2, 2, "");
            return;
        }
        if (PregnancyUtil.checkEmail(account)) {
            register(userName, account, password);
        } else if (PregnancyUtil.checkPhoneNum(account)) {
            register(userName, account, password);
        } else {
            CustomToast.makeText(MyApplication.getContext(), R.string.account_error, R.drawable.ic_toast_warming).show();
        }
    }


    private void register(String user_name, final String account, String password) {
        RequestModel.getInstance().register(user_name, account, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        mView.showProgress("注册中...");
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
                        mView.loginSuccess(data);

                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.errorShake(0, 2, code);
                        mView.hideProgress();
                    }
                });
    }
}
