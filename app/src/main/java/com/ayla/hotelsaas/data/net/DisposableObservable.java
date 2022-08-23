package com.ayla.hotelsaas.data.net;

import com.ayla.ng.lib.bootstrap.common.AylaDisposable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.operators.observable.ObservableCreate;
import io.reactivex.plugins.RxJavaPlugins;


public  abstract class DisposableObservable<T> extends Observable<T>{
    public static <T> Observable<T>  create(DisposableObservableOnSubscribe<T> source) {
        ObjectHelper.requireNonNull(source, "source is null");
        return RxJavaPlugins.onAssembly(new ObservableCreate(source).doFinally(new Action() {

            /**
             * Runs the action and optionally throws a checked exception.
             *
             * @throws Exception if the implementation wishes to throw a checked exception
             */
            @Override
            public void run() throws Exception {
                for (AylaDisposable  disposable : source.aylaDisposables) {
                    disposable.dispose();
                }
            }
        }));
    }
}
