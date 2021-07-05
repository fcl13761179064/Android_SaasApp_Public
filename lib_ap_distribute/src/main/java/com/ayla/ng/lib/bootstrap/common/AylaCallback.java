package com.ayla.ng.lib.bootstrap.common;

import androidx.annotation.NonNull;

public interface AylaCallback<E> {
    void onSuccess(@NonNull E result);

    void onFailed(@NonNull Throwable throwable);
}
