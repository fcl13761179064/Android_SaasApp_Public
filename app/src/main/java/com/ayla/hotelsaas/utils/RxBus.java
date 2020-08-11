package com.ayla.hotelsaas.utils;

import org.reactivestreams.Subscriber;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * @描述
 * @作者 fanchunlei
 */
public class RxBus {

    private static final String TAG = RxBus.class.getSimpleName();

    private static volatile RxBus mInstance;

    /**
     * 默认 bus ;
     */

    private Subject<Object> _mBus;

    /**
     * 背压
     */
    private FlowableProcessor<Object> _mBackPressureBus;

    private Map<Object, CompositeDisposable> mSubscription;

    private RxBus() {
        _mBus = PublishSubject.create().toSerialized();
        _mBackPressureBus = PublishProcessor.create().toSerialized();
    }

    public static RxBus getInstance() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    /**
     * 发送普通事件
     */
    public void send(Object event) {
        _mBus.onNext(event);
    }

    /**
     * 发送背压事件
     */
    public void sendByBackPressure(Object event) {
        _mBackPressureBus.onNext(event);

    }

    /**
     * 接收普通事件
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {

        return _mBus.ofType(eventType);
    }

    /**
     * 接受背压事件
     */
    public <T> Flowable<T> toFlowable(Class<T> eventType) {

        return _mBackPressureBus.ofType(eventType);
    }

    /**
     * 普通事件的处理
     */
    public <T> Disposable doSubscribe(Class<T> eventType, Consumer<T> next, Consumer<Throwable> error) {

        return toObservable(eventType)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }

    /**
     * 背压事件处理
     */
    public <T> Flowable doFlowable(Class<T> eventType, Subscriber<T> tSubscriber) {
        toFlowable(eventType)
                .onBackpressureLatest() //背压策略
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(tSubscriber);
        return toFlowable(eventType);
    }

    /**
     * 是否有订阅者
     */
    public  boolean hasSubscribers(boolean isBackPressure) {

        if (!isBackPressure){
            return _mBus.hasObservers();
        } else{
            return _mBackPressureBus.hasSubscribers();
        }
    }


    /**
     * 背压解除订阅
     */
    public void unSubscription(){

        _mBackPressureBus.onComplete();

    }


    /**
     * 添加订阅到集合(一般事件)
     */
    public void addSubscriptions(Object o, Disposable disposable) {

        if (mSubscription == null) {
            mSubscription = new HashMap<>();
        }

        String key = o.getClass().getName();

        if (mSubscription.get(key) != null) {
            mSubscription.get(key).add(disposable);

        } else {
            CompositeDisposable compositeDisposable = new CompositeDisposable();

            compositeDisposable.add(disposable);
            mSubscription.put(key, compositeDisposable);
        }


    }


    /**
     * 解除订阅
     * 一般事件的解除订阅
     *
     * @param o
     */
    public void clearSubscriptions(Object o) {
        if (mSubscription == null) {
            return;
        }
        String key = o.getClass().getName();
        if (!mSubscription.containsKey(key)) {
            return;
        }

        if (mSubscription.get(key) != null) {
            mSubscription.get(key).dispose();
        }
        mSubscription.remove(key);
    }
}
