package com.ayla.hotelsaas.bean;

import java.io.Serializable;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/8/12
 */
public class TouchPanelBean implements Serializable {

    private int iconRes;
    private String propertyValue;
    private int number;
    private int name_id;
    private int icon_id;
    private int btn_position;

    public int getName_id() {
        return name_id;
    }

    public void setName_id(int name_id) {
        this.name_id = name_id;
    }

    public int getIcon_id() {
        return icon_id;
    }

    public void setIcon_id(int icon_id) {
        this.icon_id = icon_id;
    }

    public int getBtn_position() {
        return btn_position;
    }

    public void setBtn_position(int btn_position) {
        this.btn_position = btn_position;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public TouchPanelBean(int iconRes, String propertyValue, int name_id,int icon_id, int btn_position) {
        this.iconRes = iconRes;
        this.propertyValue = propertyValue;
        this.name_id = name_id;
        this.icon_id = icon_id;
        this.btn_position = btn_position;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

}
