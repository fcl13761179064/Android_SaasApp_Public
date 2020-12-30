package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.bean.BaseResult;

import java.io.IOException;

/**
 * 服务端错误
 */
public class ServerBadException extends IOException {

    private BaseResult baseResult;

    public ServerBadException(BaseResult baseResult) {
        super(baseResult.msg);
        this.baseResult = baseResult;
    }

    public String getCode() {
        return baseResult.code;
    }

    public String getMsg() {
        return baseResult.msg;
    }

    public boolean isSuccess() {
        return baseResult.isSuccess();
    }
}
