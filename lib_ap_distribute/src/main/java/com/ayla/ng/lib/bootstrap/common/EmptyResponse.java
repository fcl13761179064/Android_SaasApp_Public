package com.ayla.ng.lib.bootstrap.common;

public class EmptyResponse implements AylaCallback<Void> {
    AylaCallback<Object> aylaCallback;

    public EmptyResponse(AylaCallback<Object> aylaCallback) {
        this.aylaCallback = aylaCallback;
    }

    @Override
    public void onSuccess(Void result) {
        aylaCallback.onSuccess(true);
    }

    @Override
    public void onFailed(Throwable throwable) {
        aylaCallback.onFailed(throwable);
    }
}
