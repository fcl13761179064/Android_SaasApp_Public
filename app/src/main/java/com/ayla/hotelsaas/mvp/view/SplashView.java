package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;

public interface SplashView extends BaseView {
    void onVersionResult(VersionUpgradeBean baseResult);
}
