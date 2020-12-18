package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface RuleEngineActionTypeGuideView extends BaseView {

    void checkResult(int integer);

    void checkFailed(Throwable throwable);
}
