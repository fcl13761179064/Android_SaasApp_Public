package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseDevice;
import com.ayla.hotelsaas.bean.DeviceGroupData;
import com.ayla.hotelsaas.bean.DeviceItem;
import com.ayla.hotelsaas.bean.GroupItem;
import com.ayla.hotelsaas.bean.GroupRequestBean;
import com.ayla.hotelsaas.bean.MarshallEntryBean;
import com.ayla.hotelsaas.data.net.SpecialException;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceMarshallEntryView;
import com.blankj.utilcode.util.GsonUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DeviceMarshallEntryPresenter extends BasePresenter<DeviceMarshallEntryView> {

    private String mGatewayId;

    public void getGatewayId(String deviceId, Long ScopeId) {
        Disposable subscribe = RequestModel.getInstance().getNodeGateway(deviceId)
                .flatMap(new Function<String, ObservableSource<List<MarshallEntryBean>>>() {

                    @Override
                    public ObservableSource<List<MarshallEntryBean>> apply(String GatewayId) throws Exception {
                        mGatewayId = GatewayId;
                        return RequestModel.getInstance()
                                .getGatewayDeviceId(GatewayId, ScopeId)
                                .map(new Function<List<MarshallEntryBean>, List<MarshallEntryBean>>() {
                                    @Override
                                    public List<MarshallEntryBean> apply(List<MarshallEntryBean> marshallEntryBean) throws Exception {
                                        return marshallEntryBean;
                                    }
                                });
                    }
                })
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
                .subscribe(new Consumer<List<MarshallEntryBean>>() {
                    @Override
                    public void accept(List<MarshallEntryBean> marshallEntryBean) throws Exception {
                        mView.DeviceMarshallEntrySuccess(marshallEntryBean, mGatewayId);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.DeviceMarshallEntryFail(throwable.getMessage());
                    }
                });
        addSubscrebe(subscribe);
    }


    public void getSupportCombineGroupDevice(String deviceId, String productLabel, long scopeId) {
        Disposable subscribe = RequestModel.getInstance().getNodeGateway(deviceId)
                .flatMap((Function<String, ObservableSource<List<DeviceGroupData>>>) gatewayId -> {
                    mGatewayId = gatewayId;
                    return RequestModel.getInstance()
                            .getSupportCombineGroupAndDevice(gatewayId, productLabel, String.valueOf(scopeId));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mView.showProgress())
                .doFinally(() -> mView.hideProgress())
                .subscribe(deviceGroupData -> {
                    List<BaseDevice> allData = new ArrayList<>();
                    for (int i = 0; i < deviceGroupData.size(); i++) {
                        DeviceGroupData item = deviceGroupData.get(i);
                        if (item.deviceGroupFlag == 0) {
                            //设备
                            DeviceItem deviceItem = GsonUtils.fromJson(item.deviceGroupJson, DeviceItem.class);
                            allData.add(deviceItem);
                        } else if (item.deviceGroupFlag == 1) {
                            //群组
                            GroupItem groupItem = GsonUtils.fromJson(item.deviceGroupJson, GroupItem.class);
                            allData.add(groupItem);
                        }
                    }
                    mView.getCombineDeviceGroupSuccess(mGatewayId, allData);
                }, throwable -> mView.onCombineDeviceGroupFail(throwable));
        addSubscrebe(subscribe);
    }


    public void createGroup(GroupRequestBean groupRequestBean, String groupName) {
        Disposable subscribe = RequestModel.getInstance().createGroup(groupRequestBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mView.showProgress())
                .subscribe(baseResult -> {
                    if (baseResult.isSuccess())
                        mView.saveGroupSuccess((String) baseResult.data, groupName);
                    else {
                        int code = -1;
                        try {
                            code = Integer.parseInt(baseResult.code);
                        } catch (Exception e) {

                        }
                        mView.saveGroupFail(new SpecialException(code, baseResult.msg, null));
                    }
                }, throwable -> {
                    mView.saveGroupFail(throwable);
                });
        addSubscrebe(subscribe);
    }

    public void updateGroup(String data) {
        Disposable subscribe = RequestModel.getInstance().updateGroup(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mView.showProgress())
                .subscribe(result -> {
                    mView.updateGroupResult(true, null);
                }, throwable -> {
                    mView.updateGroupResult(false, throwable);
                });

        addSubscrebe(subscribe);
    }

    public void updateSwitchProperty(String deviceId, String currentIndex, int switchMode) {
        Disposable subscribe = RequestModel.getInstance().updateSwitchProperty(deviceId, currentIndex, switchMode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    mView.updateSwitchPropertySuccess();
                }, throwable -> {
                    mView.updateSwitchPropertyFail(throwable);
                });

        addSubscrebe(subscribe);
    }

    public void removeUseDevice(String deviceId, long scopeId) {
        addSubscrebe(
                RequestModel.getInstance().unBindPurposeDevice(deviceId, scopeId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result.isSuccess())
                                mView.removeUseDeviceResult(true, null);
                            else
                                mView.removeUseDeviceResult(false, null);

                        }, throwable -> {
                            mView.removeUseDeviceResult(false, throwable);
                        })
        );
    }

}
