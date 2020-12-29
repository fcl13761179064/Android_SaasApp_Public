package com.ayla.hotelsaas.data.net;

import java.io.IOException;

/**
 * 服务端错误
 */
public class ServerBadException extends IOException {

    private String code;
    private String msg;

    public ServerBadException(String code, String msg) {
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
