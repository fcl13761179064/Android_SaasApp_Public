package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.RuleEngineActionTypeGuideView;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RuleEngineActionTypeGuidePresenter extends BasePresenter<RuleEngineActionTypeGuideView> {
    public void check(long scopeId) {
        Disposable subscribe = RequestModel.getInstance().checkRadioExists(scopeId)//首先检查是否关联了音响
                .flatMap(new Function<Boolean, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            return RequestModel.getInstance().checkVoiceRule(scopeId)//再检查是否已经创建了包含酒店欢迎语动作的联动
                                    .map(new Function<Boolean, Integer>() {
                                        @Override
                                        public Integer apply(@NonNull Boolean aBoolean) throws Exception {
                                            if (aBoolean) {
                                                return -2;
                                            } else {
                                                return 0;
                                            }
                                        }
                                    });
                        } else {
                            return Observable.just(-1);
                        }
                    }
                })// -1:没有关联音响  -2:已创建了酒店欢迎语的联动  0:关联了音响，没有创建过酒店欢迎语动作。
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
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mView.checkResult(integer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.checkFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
