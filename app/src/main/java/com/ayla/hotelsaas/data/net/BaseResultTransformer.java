package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.bean.BaseResult;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public abstract class BaseResultTransformer<Upstream extends BaseResult<Downstream>, Downstream> implements ObservableTransformer<Upstream, Downstream> {
    @NonNull
    @Override
    public final ObservableSource<Downstream> apply(@NonNull Observable<Upstream> upstream) {
        return upstream.flatMap(new Function<Upstream, ObservableSource<Downstream>>() {
            @Override
            public ObservableSource<Downstream> apply(@NonNull Upstream upstream) throws Exception {
                if (upstream.isSuccess()) {
                    return Observable.just(upstream.data);
                }
                return Observable.error(new RxjavaFlatmapThrowable(upstream.code, upstream.msg));
            }
        });
    }
}
