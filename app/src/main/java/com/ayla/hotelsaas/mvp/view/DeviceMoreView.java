package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;

import java.util.List;

public interface DeviceMoreView extends BaseView {

    //错误提示
    void renameFailed(String code, String msg);

    //操作成功
    void renameSuccess(String newNickName);

    //移除成功
    void removeSuccess(Boolean is_rename);

    //失败成功
    void removeFailed(Throwable throwable);

    void cannotRenameFunction();

    void canRenameFunction();

    void showPurposeCategory(List<PurposeCategoryBean> purposeCategoryBeans);

    void updatePurposeSuccess();

    void updatePurposeFailed(Throwable throwable);
}
