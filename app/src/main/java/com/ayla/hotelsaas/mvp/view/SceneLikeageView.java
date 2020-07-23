package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.RuleEngineBean;


public interface SceneLikeageView extends BaseView {

    void loadDataSuccess(RuleEngineBean data);

    void loadDataFinish() ;

}
