package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.WorkOrderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class WorkOrderPresenter extends BasePresenter<WorkOrderView> {
    //页码
    private int pageNum = 1;
    //拉取数量
    private static String maxNum = "10";

    /**
     * 加载下一页
     */
    public void loadNextPage() {
        pageNum++;
        loadData();
    }

    /**
     * 加载第一页
     */
    public void loadFistPage() {
        pageNum = 1;
        loadData();
    }

    /**
     * 加载列表
     *
     * @param
     */
    public void loadData() {
        RequestModel.getInstance().getWorkOrderList(pageNum, maxNum)
                .subscribe(new RxjavaObserver<WorkOrderBean>() {
                    @Override
                    public void _onNext(WorkOrderBean data) {

                        mView.loadDataSuccess(data);
                    }

                    @Override
                    public void _onError(String code, String msg) {

                        mView.loadDataFinish();
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addSubscrebe(d);
                    }
                });
    }
}
