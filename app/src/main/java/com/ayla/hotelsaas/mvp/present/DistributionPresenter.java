package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.RoomManageBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DistributionView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DistributionPresenter extends BasePresenter<DistributionView> {
    public void loadSelfRooms() {
        Disposable subscribe = RequestModel.getInstance()
                .getCreateRoomOrder(1, Integer.MAX_VALUE)
                .map(new Function<BaseResult<RoomManageBean>, List<RoomManageBean.RecordsBean>>() {
                    @Override
                    public List<RoomManageBean.RecordsBean> apply(BaseResult<RoomManageBean> roomManageBeanBaseResult) throws Exception {
                        return roomManageBeanBaseResult.data.getRecords();
                    }
                })//首先找出自定义的房间列表
                .flatMap(new Function<List<RoomManageBean.RecordsBean>, ObservableSource<List<RoomManageBean.RecordsBean>>>() {
                    @Override
                    public ObservableSource<List<RoomManageBean.RecordsBean>> apply(List<RoomManageBean.RecordsBean> recordsBeans) throws Exception {
                        List<Observable<Integer>> jobs = new ArrayList<>();
                        for (RoomManageBean.RecordsBean recordsBean : recordsBeans) {
                            Observable<Integer> getCountObservable = RequestModel.getInstance()
                                    .getDeviceList(recordsBean.getId(), 1, Integer.MAX_VALUE)
                                    .map(new Function<DeviceListBean, Integer>() {
                                        @Override
                                        public Integer apply(@NonNull DeviceListBean deviceListBean) throws Exception {
                                            return deviceListBean.getDevices().size();
                                        }
                                    });
                            jobs.add(getCountObservable);
                        }
                        if (jobs.size() == 0) {
                            return Observable.just(recordsBeans);
                        } else {
                            return Observable
                                    .zip(jobs, new Function<Object[], List<RoomManageBean.RecordsBean>>() {
                                        @Override
                                        public List<RoomManageBean.RecordsBean> apply(Object[] objects) throws Exception {
                                            List<RoomManageBean.RecordsBean> data = new ArrayList<>();
                                            for (int i = 0; i < recordsBeans.size(); i++) {
                                                int count = (int) objects[i];
                                                if (count != 0) {
                                                    data.add(recordsBeans.get(i));
                                                }
                                            }
                                            return data;
                                        }
                                    });
                        }
                    }
                })//根据每个自定义房间的设备数量，排除掉数量为0的房间。
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<List<RoomManageBean.RecordsBean>>() {
                    @Override
                    public void accept(List<RoomManageBean.RecordsBean> roomManageBean) throws Exception {
                        mView.hotelLoadSuccess(roomManageBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);
    }

    public void transferToHotel(String hotelId, String[] roomIdList) {
        Disposable subscribe = RequestModel.getInstance()
                .transferToHotel(hotelId, roomIdList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<BaseResult>() {
                    @Override
                    public void accept(BaseResult baseResult) throws Exception {
                        mView.doSuccess(roomIdList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.doFailed();
                    }
                });
        addSubscrebe(subscribe);
    }

    public void transferToStruct(String hotelId, String sourceRoomId, String[] roomIdList) {
        Disposable subscribe = RequestModel.getInstance()
                .transferToStruct(hotelId, sourceRoomId, roomIdList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<BaseResult>() {
                    @Override
                    public void accept(BaseResult baseResult) throws Exception {
                        mView.doSuccess(roomIdList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.doFailed();
                    }
                });
        addSubscrebe(subscribe);
    }

    public void transferToRoom(String hotelId, String sourceRoomId, String targetRoomId) {
        Disposable subscribe = RequestModel.getInstance()
                .transferToRoom(hotelId, sourceRoomId, targetRoomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<BaseResult>() {
                    @Override
                    public void accept(BaseResult baseResult) throws Exception {
                        mView.doSuccess(new String[]{targetRoomId});
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.doFailed();
                    }
                });
        addSubscrebe(subscribe);
    }
}
