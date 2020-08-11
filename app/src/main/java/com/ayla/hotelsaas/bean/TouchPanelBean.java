package com.ayla.hotelsaas.bean;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/8/12
 */
public class TouchPanelBean {

    private int iconRes;
    private String title;
    private String msgCount;

    public TouchPanelBean(int iconRes, String title) {
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
