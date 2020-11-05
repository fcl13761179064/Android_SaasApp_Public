package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.AutoRunView;
import com.ayla.hotelsaas.utils.BeanObtainCompactUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AutoRunFragmentPresenter extends BasePresenter<AutoRunView> {

    public void changeSceneStatus(BaseSceneBean ruleEngineBean, boolean isChecked) {
        ruleEngineBean.setEnable(isChecked);
        Disposable subscribe = RequestModel.getInstance().updateRuleEngine(BeanObtainCompactUtil.obtainRuleEngineBean(ruleEngineBean))
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
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean booleanBaseResult) throws Exception {
                        mView.changeSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ruleEngineBean.setEnable(!isChecked);
                        mView.changeFailed(ruleEngineBean);
                    }
                });
        addSubscrebe(subscribe);
    }
}
