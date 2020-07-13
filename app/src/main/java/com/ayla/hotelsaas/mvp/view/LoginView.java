package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.User;

/**
 * @描述
 * @作者 丁军伟
 * @时间 2017/8/2
 */
public interface LoginView extends BaseView {

    //获取账号
    String getAccount();

    //密码
    String getPassword();

    //错误提示
    void errorShake(int type, int CycleTimes);

    //登录成功
    void loginSuccess(User data);
}
