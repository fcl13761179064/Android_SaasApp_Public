package com.ayla.hotelsaas.bean;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 所有的model必须继承此类
 */
public class BaseResult<T> implements Serializable {
    @SerializedName("code")
    public String code;
    @SerializedName("error")
    public String error;
    @SerializedName("data")
    public T data;

    public boolean isSuccess() {
        if (code.equals("0000")) {
            return true;
        } else{
            return false;
        }
    }

    public boolean needLogin() {
        return code.equals("5001");
    }
}
