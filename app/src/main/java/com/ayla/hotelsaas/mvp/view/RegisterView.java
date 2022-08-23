package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.User;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2017/8/2
 */
public interface RegisterView extends BaseView {
    //注册成功
    void RegistSuccess(Boolean data);

    void registerFailed(String msg);
}
