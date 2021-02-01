package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.localBean.BaseSceneBean;

public interface AutoRunView extends BaseView {

    void changeSuccess();

    void changeFailed(BaseSceneBean ruleEngineBean,Throwable throwable);
}
