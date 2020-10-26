package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.adapter.FunctionRenameListAdapter;
import com.ayla.hotelsaas.base.BaseView;

import java.util.List;

public interface DeviceMoreView extends BaseView {

    //错误提示
    void renameFailed(String code, String msg);

    //操作成功
    void renameSuccess(String newNickName);

    //移除成功
    void removeSuccess(Boolean is_rename);

    //失败成功
    void removeFailed(String code, String msg);

    //该设备支持的重命名功能列表
    void showFunctions(List<FunctionRenameListAdapter.Bean> attributesBeans);
}
