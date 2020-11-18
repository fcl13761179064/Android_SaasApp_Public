package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.ConstructionBillListBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.ProjectListView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ProjectListPresenter extends BasePresenter<ProjectListView> {
    private int currentPage = 1;

    public void loadData() {
        Disposable subscribe = RequestModel.getInstance().getConstructionBillList(currentPage, 50)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ConstructionBillListBean>() {
                    @Override
                    public void accept(ConstructionBillListBean constructionBillListBean) throws Exception {
                        currentPage++;
                        mView.showData(constructionBillListBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onRequestFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    public void refresh() {
        currentPage = 1;
        loadData();
    }
}
