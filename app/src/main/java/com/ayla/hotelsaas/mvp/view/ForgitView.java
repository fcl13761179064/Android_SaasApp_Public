package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2017/8/2
 */
public interface ForgitView extends BaseView {

    //获取用户名
    String getUserName();

    //密码
    String getYanzhengMa();

    //错误提示
    void errorShake(int type, int CycleTimes, String code);

    //注册成功
    void RegistSuccess(Boolean data);
}
