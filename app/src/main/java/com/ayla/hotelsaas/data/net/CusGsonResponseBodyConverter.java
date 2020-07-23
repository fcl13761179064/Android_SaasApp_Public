package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.bean.BaseResult;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class CusGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    CusGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        try {
            T read = adapter.read(jsonReader);
            if (read instanceof BaseResult) {
                if (!((BaseResult) read).isSuccess()) {
                    throw new RxjavaFlatmapThrowable(((BaseResult) read).code, ((BaseResult) read).msg);
                }
            }
            return read;
        } finally {
            value.close();
        }
    }
}