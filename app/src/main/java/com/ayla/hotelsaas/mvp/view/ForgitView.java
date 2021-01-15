package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2017/8/2
 */
public interface ForgitView extends BaseView {

    //发送验证码成功
    void sendCodeSuccess(Boolean data);

    void sendCodeFailed(Throwable msg);

    //修改密码
    void modifyPasswordSuccess(Boolean data);

    void modifyPasswordFailed(Throwable msg);

    //重置密码
    void resertPasswordSuccess(Boolean data);

    //重置密码
    String resetPassword();

    void resertPasswordFailed(Throwable throwable);
}
