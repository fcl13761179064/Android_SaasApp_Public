package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceListView;
import com.ayla.hotelsaas.mvp.view.SceneLikeageView;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class SceneLikeagePresenter extends BasePresenter<SceneLikeageView> {
    //页码
    private int pageNum = 1;
    //拉取数量
    private static String maxNum = "10";

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
    public void loadFistPage(String resourceRoomId) {
        pageNum = 1;
        loadData(resourceRoomId);
    }

    /**
     * 加载列表
     *
     * @param resourceRoomId
     */
    public void loadData(String resourceRoomId) {
        RequestModel.getInstance().getDeviceList(resourceRoomId,pageNum,maxNum)
                .subscribe(new  RxjavaObserver<DeviceListBean>() {
            @Override
            public void _onNext(DeviceListBean data) {

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
