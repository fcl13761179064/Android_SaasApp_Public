package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.ProjectListView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ProjectListPresenter extends BasePresenter<ProjectListView> {
    private int currentPage = 1;

    public void loadData(String tradeId,String processStatus) {
        Disposable subscribe = RequestModel.getInstance().getWorkOrderList(currentPage, 50,tradeId,processStatus)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WorkOrderBean>() {
                    @Override
                    public void accept(WorkOrderBean constructionBillListBean) throws Exception {
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

    public void refresh(String tradeId,String processStatus) {
        currentPage = 1;
        loadData(tradeId,processStatus);
    }


    public void loadHistroyData(String tradeId,String processStatus) {
        Disposable subscribe = RequestModel.getInstance().getHistoryData(currentPage, 50,tradeId,processStatus)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WorkOrderBean>() {
                    @Override
                    public void accept(WorkOrderBean constructionBillListBean) throws Exception {
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
    public void refreshHistryData(String tradeId,String processStatus) {
        currentPage = 1;
        loadHistroyData(tradeId,processStatus);
    }

}
