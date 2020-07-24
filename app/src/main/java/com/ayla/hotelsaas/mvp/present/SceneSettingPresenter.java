package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SceneSettingView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SceneSettingPresenter extends BasePresenter<SceneSettingView> {
    public void saveOrUpdateRuleEngine(RuleEngineBean mRuleEngineBean) {
        Observable<BaseResult<Boolean>> observable;
        if (mRuleEngineBean.getRuleId() == null) {
            observable = RequestModel.getInstance().saveRuleEngine(mRuleEngineBean);
        } else {
            observable = RequestModel.getInstance().updateRuleEngine(mRuleEngineBean);
        }
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("加载中");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new RxjavaObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void _onNext(Boolean data) {
                        mView.saveSuccess();
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.saveFailed();
                    }
                });
    }

    public void deleteScene(int ruleId) {
        RequestModel.getInstance()
                .deleteRuleEngine(ruleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("加载中");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new RxjavaObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void _onNext(Boolean data) {
                        mView.deleteSuccess();
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.deleteFailed();
                    }
                });
    }
}
