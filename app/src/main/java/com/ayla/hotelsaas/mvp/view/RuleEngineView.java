package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.RuleEngineBean;

import java.util.List;


public interface RuleEngineView extends BaseView {

    void loadDataSuccess(List<RuleEngineBean> data);

    void loadDataFinish() ;
}
