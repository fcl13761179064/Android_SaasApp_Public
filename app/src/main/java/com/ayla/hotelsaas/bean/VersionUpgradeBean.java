package com.ayla.hotelsaas.bean;

import java.io.Serializable;

public class VersionUpgradeBean implements Serializable {

    /**
     * id : 6
     * name : 艾拉工程
     * version : 1.4.0
     * isForce : 0
     * url : http://aylasmht-test.oss-cn-shanghai.aliyuncs.com/%2Ffilestore%2Fpackage%2Fapk%2Fpackage_1605859096461.apk
     * versionInfo : 这是新版本这是新版本这是新版本这是新版本这是新版本这是新版本
     * size : 10
     */

    private int id;
    private String name;
    private String version;
    private int isForce;
    private String url;
    private String versionInfo;
    private int size;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getIsForce() {
        return isForce;
    }

    public void setIsForce(int isForce) {
        this.isForce = isForce;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
