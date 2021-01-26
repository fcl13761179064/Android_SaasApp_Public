package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/29
 */
public class DeviceMorePresenter extends BasePresenter<DeviceMoreView> {

    public void deviceRenameMethod(String deviceId, String nickName, String pointName, long regionId, String regionName) {
        Disposable subscribe = RequestModel.getInstance().deviceRename(deviceId, nickName, pointName, regionId, regionName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("修改中...");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mView.renameSuccess(nickName);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.renameFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    public void deviceRemove(String deviceId, long scopeId, String scopeType) {
        Disposable subscribe = RequestModel.getInstance().deviceRemove(deviceId, scopeId, scopeType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("移除中...");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mView.removeSuccess(aBoolean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.removeFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    public void getRenameAbleFunctions(String pid) {
        Disposable subscribe = RequestModel.getInstance()
                .getDeviceCategoryDetail(pid)//找到了指定的设备，存在可以配置为联动条件、动作的属性。
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
                .subscribe(new Consumer<DeviceCategoryDetailBean>() {
                    @Override
                    public void accept(DeviceCategoryDetailBean deviceCategoryDetailBean) throws Exception {
                        int count = deviceCategoryDetailBean.getConditionProperties().size() + deviceCategoryDetailBean.getActionProperties().size();
                        if (count > 0) {
                            mView.canRenameFunction();
                        } else {
                            mView.cannotRenameFunction();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.cannotRenameFunction();
                    }
                });
        addSubscrebe(subscribe);

    }

    public void getPurposeCategory() {
        Disposable subscribe = RequestModel.getInstance().getPurposeCategory()
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
                .subscribe(new Consumer<List<PurposeCategoryBean>>() {
                    @Override
                    public void accept(List<PurposeCategoryBean> purposeCategoryBeans) throws Exception {
                        mView.showPurposeCategory(purposeCategoryBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);
    }

    public void updatePurpose(String deviceId, int purposeCategory) {
        Disposable subscribe = RequestModel.getInstance().updatePurpose(deviceId, purposeCategory)
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
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.updatePurposeSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.updatePurposeFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
