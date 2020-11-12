package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.view.ProjectListView;

import java.util.ArrayList;
import java.util.List;

public class ProjectListPresenter extends BasePresenter<ProjectListView> {
    public void loadData(){
        List<Object> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add(1);
        }
        mView.showData(data);
    }
}
