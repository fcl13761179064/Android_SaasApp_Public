package com.ayla.hotelsaas.data.net;

import android.text.TextUtils;


/**
 * @描述 自定义异常类, 当flatmap转换时上次请求数据时, 上次请求数据返回status不等于200时候使用
 * @作者 fanchunlei
 * @时间 2020/6/3
 */
public class RxjavaFlatmapThrowable  extends Throwable{

    public static final String THROW_CODE = "4096";
    private String code;
    private String msg;
    private Throwable throwable;

    public RxjavaFlatmapThrowable(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public RxjavaFlatmapThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getMsg() {
        if (!TextUtils.isEmpty(msg)) {
            return msg;
        }
        if (throwable != null) {
            return throwable.toString();
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public Throwable getThrowable() {
        return throwable;
    }
    /**
     * 是否是成功
     *
     * @return
     */
    public boolean isSuccess() {
        if (!TextUtils.isEmpty(code) && code.equals("0000")) {
            return true;
        }
        return false;
    }
}
