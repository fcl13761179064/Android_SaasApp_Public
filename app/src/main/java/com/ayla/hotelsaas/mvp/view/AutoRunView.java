package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.RuleEngineBean;

public interface AutoRunView extends BaseView {

    void changeSuccess();

    void changeFailed(RuleEngineBean ruleEngineBean);
}
