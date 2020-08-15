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
    private int id;
    private int number;
    private int btn_position;

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

    public TouchPanelBean(int iconRes, String propertyValue, int id, int btn_position) {
        this.iconRes = iconRes;
        this.propertyValue = propertyValue;
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
