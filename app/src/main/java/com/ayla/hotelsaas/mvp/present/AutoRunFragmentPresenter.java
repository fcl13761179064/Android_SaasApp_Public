package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.AutoRunView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AutoRunFragmentPresenter extends BasePresenter<AutoRunView> {

    public void changeSceneStatus(RuleEngineBean ruleEngineBean, boolean isChecked) {
        ruleEngineBean.setStatus(isChecked ? 1 : 0);
        Disposable subscribe = RequestModel.getInstance().updateRuleEngine(ruleEngineBean)
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
                .subscribe(new Consumer<BaseResult>() {
                    @Override
                    public void accept(BaseResult booleanBaseResult) throws Exception {
                        mView.changeSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ruleEngineBean.setStatus(isChecked ? 0 : 1);
                        mView.changeFailed(ruleEngineBean);
                    }
                });
        addSubscrebe(subscribe);
    }
}
