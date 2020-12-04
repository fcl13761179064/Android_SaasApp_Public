package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.ForgitView;
import com.ayla.hotelsaas.ui.CustomToast;
import com.blankj.utilcode.util.RegexUtils;

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

    public void modifyPassword() {
        String userName = mView.getUserName();
        String yanzhengma = mView.getYanzhengMa();
        if (TextUtils.isEmpty(userName)) {
            CustomToast.makeText(MyApplication.getContext(), "用户名不能为空", R.drawable.ic_toast_warming);
            mView.errorShake(1, 2, "");
            return;
        }

        if (TextUtils.isEmpty(yanzhengma)) {
            CustomToast.makeText(MyApplication.getContext(), "验证码不能为空", R.drawable.ic_toast_warming);
            mView.errorShake(2, 2, "");
            return;
        }
        if (yanzhengma.length() != 6) {
            CustomToast.makeText(MyApplication.getContext(), "验证码错误", R.drawable.ic_toast_warming);
            mView.errorShake(2, 2, "");
            return;
        }
        if (RegexUtils.isEmail(userName)) {
            modifyforgit(userName, yanzhengma);
        } else if (RegexUtils.isMobileSimple(userName)) {
            modifyforgit(userName, yanzhengma);
        } else {
            CustomToast.makeText(MyApplication.getContext(), R.string.account_forgit_password, R.drawable.ic_toast_warming);
        }
    }


    private void modifyforgit(String user_name, String yanzhengma) {
        RequestModel.getInstance().modifyForgitPassword(user_name, yanzhengma)
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
                .subscribe(new RxjavaObserver<Boolean>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void _onNext(Boolean data) {
                        mView.modifyPasswordSuccess(data);
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.errorShake(0, 2, msg);

                    }
                });
    }


    public void send_sms() {
        String iphone_youxiang = mView.getUserName();
        send_sms(iphone_youxiang);
    }


    private void send_sms(String iphone_youxiang) {
        RequestModel.getInstance().send_sms(iphone_youxiang)
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
                .subscribe(new RxjavaObserver<Boolean>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void _onNext(Boolean data) {
                        mView.sendCodeSuccess(data);
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.errorShake(-1, 2, msg);
                    }
                });
    }


    public void resetPassword(String phone) {
        String new_password = mView.resetPassword();
        if (TextUtils.isEmpty(new_password)) {
            CustomToast.makeText(MyApplication.getContext(), "密码不能为空", R.drawable.ic_toast_warming);
            return;
        } else if (new_password.length() < 6) {
            CustomToast.makeText(MyApplication.getContext(), "密码至少为6位", R.drawable.ic_toast_warming);
            return;
        }
        reset_Password(phone, new_password);
    }

    private void reset_Password(String phone, String new_password) {
        RequestModel.getInstance().resert_passwoed(phone, new_password)
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
                .subscribe(new RxjavaObserver<Boolean>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void _onNext(Boolean data) {
                        mView.resertPasswordSuccess(data);
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        CustomToast.makeText(MyApplication.getInstance(), msg, R.drawable.ic_success);

                    }
                });
    }
}
