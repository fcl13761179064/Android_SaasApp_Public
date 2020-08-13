package com.ayla.hotelsaas.bean;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/8/12
 */
public class TouchPanelBean {

    private int iconRes;
    private String title;
    private int num;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public TouchPanelBean(int iconRes, String title,int num) {
        this.iconRes = iconRes;
        this.title = title;
        this.num = num;
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

}
