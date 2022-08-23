package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean;

import java.util.List;


public interface RuleEngineView extends BaseView {

    void loadDataSuccess(List<BaseSceneBean> data);

    void loadDataFailed(Throwable throwable);
}
