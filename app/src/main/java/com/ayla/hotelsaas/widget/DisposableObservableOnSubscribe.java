package com.ayla.hotelsaas.widget;


import com.ayla.ng.lib.bootstrap.common.AylaDisposable;

import java.util.HashSet;

import io.reactivex.ObservableOnSubscribe;

public abstract class DisposableObservableOnSubscribe<T> implements ObservableOnSubscribe<T> {

     HashSet<AylaDisposable>  aylaDisposables= new HashSet();

    public void addDisposable(AylaDisposable aylaDisposable) {
        aylaDisposables.add(aylaDisposable);
    }
}
