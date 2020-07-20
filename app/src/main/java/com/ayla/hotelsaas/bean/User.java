package com.ayla.hotelsaas.bean;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/6/3
 */
public class User {
    private String authToken;
    private String refreshToken;
    private String expireTime;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }
}
