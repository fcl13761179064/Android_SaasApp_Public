package com.ayla.hotelsaas.data.net;

import java.io.IOException;

/**
 * @描述 自定义异常类, 当flatmap转换时上次请求数据时, 上次请求数据返回status不等于200时候使用
 * @作者 fanchunlei
 * @时间 2020/6/3
 */
public class RxjavaFlatmapThrowable extends IOException {

    private String code;
    private String msg;

    public RxjavaFlatmapThrowable(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
