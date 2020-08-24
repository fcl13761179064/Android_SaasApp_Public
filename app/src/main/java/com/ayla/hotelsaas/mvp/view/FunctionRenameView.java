package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.adapter.FunctionRenameListAdapter;
import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;

import java.util.List;

public interface FunctionRenameView extends BaseView {

    void showFunctions(List<FunctionRenameListAdapter.Bean> attributesBeans);

    void renameSuccess();

    void renameFailed();
}
