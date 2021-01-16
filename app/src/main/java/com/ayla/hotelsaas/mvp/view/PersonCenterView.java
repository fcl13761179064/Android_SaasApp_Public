package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.PersonCenter;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;

public interface PersonCenterView extends BaseView {

    //错误提示
    void getUserInfoFail(Throwable throwable);

    //操作成功
    void getUserInfoFailSuccess(PersonCenter personCenter);

    void onVersionResult(VersionUpgradeBean baseResult);

    void onVersionCheckFailed(Throwable throwable);
}
