package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.PersonCenter;

import java.util.List;

public interface ProjectListView extends BaseView {

    void showData(List<Object> data);
}
