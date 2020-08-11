package com.weying.business.bean;

/**
 * @描述
 * @作者 丁军伟
 * @时间 2017/9/14
 */
public class MainSettingBean {

    private int iconRes;
    private String title;
    private String msgCount;

    public MainSettingBean(int iconRes, String title) {
        this.iconRes = iconRes;
        this.title = title;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(String msgCount) {
        this.msgCount = msgCount;
    }
}
