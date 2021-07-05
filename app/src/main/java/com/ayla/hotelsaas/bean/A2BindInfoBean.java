package com.ayla.hotelsaas.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class A2BindInfoBean {
    private Boolean bindStatus;
    private String scopeId;

    public Boolean getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(Boolean bindStatus) {
        this.bindStatus = bindStatus;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }
}
