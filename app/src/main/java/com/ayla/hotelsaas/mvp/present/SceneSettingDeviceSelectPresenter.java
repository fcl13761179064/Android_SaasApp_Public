package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.GatewayNodeBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SceneSettingDeviceSelectView;
import com.ayla.hotelsaas.utils.TempUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SceneSettingDeviceSelectPresenter extends BasePresenter<SceneSettingDeviceSelectView> {

    /**
     * @param gateway   网关id，如果为空 不用网关节点过滤
     * @param condition
     */
    public void loadDevice(long scopeId, String gateway, boolean condition) {
        Observable<List<DeviceListBean.DevicesBean>> observable;
        if (gateway != null) {//如果是网关的联动，需要首先过滤出网关的子设备
            observable = RequestModel.getInstance()
                    .getGatewayNodes(gateway, scopeId)
                    .map(new Function<BaseResult<List<GatewayNodeBean>>, List<GatewayNodeBean>>() {
                        @Override
                        public List<GatewayNodeBean> apply(BaseResult<List<GatewayNodeBean>> listBaseResult) throws Exception {
                            return listBaseResult.data;
                        }
                    })
                    .map(new Function<List<GatewayNodeBean>, List<DeviceListBean.DevicesBean>>() {
                        @Override
                        public List<DeviceListBean.DevicesBean> apply(List<GatewayNodeBean> gatewayNodeBeans) throws Exception {
                            List<DeviceListBean.DevicesBean> devicesBeans = new ArrayList<>();
                            for (DeviceListBean.DevicesBean devicesBean : MyApplication.getInstance().getDevicesBean()) {
                                for (GatewayNodeBean gatewayNodeBean : gatewayNodeBeans) {
                                    if (TextUtils.equals(devicesBean.getDeviceId(), gatewayNodeBean.getDeviceId())) {
                                        devicesBeans.add(devicesBean);
                                    }
                                }
                            }
                            return devicesBeans;
                        }
                    });
        } else {
            observable = Observable.just(MyApplication.getInstance().getDevicesBean());
        }
        observable = observable.doOnNext(new Consumer<List<DeviceListBean.DevicesBean>>() {
            @Override
            public void accept(List<DeviceListBean.DevicesBean> devicesBeans) throws Exception {
                List<DeviceListBean.DevicesBean> result = new ArrayList<>(devicesBeans);
                for (DeviceListBean.DevicesBean devicesBean : devicesBeans) {
                    if (devicesBean.getBindType() == 1) {
                        result.remove(devicesBean);
                    }
                }
                devicesBeans.clear();
                devicesBeans.addAll(result);
            }
        });//创建场景，选择设备，查询设备条件还是动作支持情况
        Disposable subscribe = RequestModel.getInstance()
                .getDeviceCategoryDetail(scopeId)//查询出设备对条件、动作的支持情况
                .zipWith(observable, new BiFunction<List<DeviceCategoryDetailBean>, List<DeviceListBean.DevicesBean>, List<DeviceListBean.DevicesBean>>() {
                    @Override
                    public List<DeviceListBean.DevicesBean> apply(List<DeviceCategoryDetailBean> deviceCategoryDetailBeans, List<DeviceListBean.DevicesBean> devicesBeans) throws Exception {
                        List<DeviceListBean.DevicesBean> enableDevices = new ArrayList<>();//可以显示在列表里面的设备
                        for (DeviceListBean.DevicesBean devicesBean : devicesBeans) {
                            if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(devicesBean) && !condition) {//如果是用途设备(红外遥控家电)，就直接套用物模型作为联动动作，不走品类中心过滤
                                enableDevices.add(devicesBean);
                                continue;
                            }
                            for (DeviceCategoryDetailBean categoryDetailBean : deviceCategoryDetailBeans) {
                                if (TextUtils.equals(categoryDetailBean.getDeviceId(), devicesBean.getDeviceId())) {//找到已绑定的设备的条件、动作描述信息
                                    if (condition) {
                                        List<String> conditionProperties = categoryDetailBean.getConditionProperties();
                                        if (conditionProperties != null && conditionProperties.size() != 0) {
                                            enableDevices.add(devicesBean);
                                        }
                                    } else {
                                        List<String> actionProperties = categoryDetailBean.getActionProperties();
                                        if (actionProperties != null && actionProperties.size() != 0) {
                                            enableDevices.add(devicesBean);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        return enableDevices;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("加载中...");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<List<DeviceListBean.DevicesBean>>() {
                    @Override
                    public void accept(List<DeviceListBean.DevicesBean> data) throws Exception {
                        mView.showDevices(data);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mView.showDevices(null);
                    }
                });
        addSubscrebe(subscribe);

    }
}
