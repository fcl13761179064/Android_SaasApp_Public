package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.bean.BaseResult;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
                    if (upstream.data != null) {
                        return Observable.just(upstream.data);
                    } else {
                        Type DownstreamType = ((ParameterizedType) BaseResultTransformer.this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
                        if (Object.class.equals(DownstreamType)) {
                            return Observable.just(((Downstream) new Object()));
                        } else if (Boolean.class.equals(DownstreamType)) {
                            return Observable.just(((Downstream) new Boolean(true)));
                        }
                    }
                }
                return Observable.error(new ServerBadException(upstream));
            }
        });
    }
}
