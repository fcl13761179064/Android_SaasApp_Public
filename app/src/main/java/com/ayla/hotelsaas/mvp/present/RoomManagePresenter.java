package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.RoomManageBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.RoomManageView;
import com.ayla.hotelsaas.mvp.view.RoomOrderView;
import com.ayla.hotelsaas.mvp.view.WorkOrderView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class RoomManagePresenter extends BasePresenter<RoomManageView> {
    //页码
    private int pageNum = 1;
    //拉取数量
    private static int maxNum = 10;

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
        RequestModel.getInstance().getCreateRoomOrder(pageNum, maxNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxjavaObserver<RoomManageBean>() {
                    @Override
                    public void _onNext(RoomManageBean data) {

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

    public void createRoomNum(String room_name) {
        RequestModel.getInstance().createRoomOrder(room_name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxjavaObserver<String>() {
                    @Override
                    public void _onNext(String data) {

                        mView.createRoomSuccess(data);
                    }

                    @Override
                    public void _onError(String code, String msg) {

                        mView.loadDataFinish();
                        mView.createRoomFail(code,msg);
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addSubscrebe(d);
                    }
                });
    }

}
