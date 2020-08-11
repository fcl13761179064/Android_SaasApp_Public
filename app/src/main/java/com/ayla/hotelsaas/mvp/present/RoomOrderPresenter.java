package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.RoomOrderView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class RoomOrderPresenter extends BasePresenter<RoomOrderView> {
    //页码
    private int pageNum = 1;
    //拉取数量
    private static int maxNum = 100;

    /**
     * 加载下一页
     */
    public void loadNextPage(String businessId) {
        pageNum++;
        loadData(businessId);
    }

    /**
     * 加载第一页
     */
    public void loadFistPage(String businessId) {
        pageNum = 1;
        loadData(businessId);
    }

    /**
     * 加载列表
     *
     * @param businessId
     */
    public void loadData(String businessId) {
        RequestModel.getInstance().getRoomOrderList(businessId,pageNum,maxNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxjavaObserver<RoomOrderBean>() {
                    @Override
                    public void _onNext(RoomOrderBean data) {
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
