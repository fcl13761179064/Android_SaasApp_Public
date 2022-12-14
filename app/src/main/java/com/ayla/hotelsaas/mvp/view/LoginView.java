package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2017/8/2
 */
public interface LoginView extends BaseView {

    //登录成功
    void loginSuccess(User data, String account);

    void loginFailed(Throwable msg);

    void checkVersionFailed(Throwable throwable);

    void checkVersionSuccess(VersionUpgradeBean versionUpgradeBean);
}
