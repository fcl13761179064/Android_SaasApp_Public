package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;

import java.util.List;

public interface SwitchUsageSettingView extends BaseView {
    void showPurposeCategory(List<PurposeCategoryBean> purposeCategoryBeans);

    void saveFailed(Throwable throwable);
    void renameFail(String throwable);

    void saveSuccess();
}
