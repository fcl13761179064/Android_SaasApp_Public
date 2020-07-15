package com.ayla.hotelsaas.data.event;

/**
 * @描述
 * @作者 fanchunlei
 */
public class NotityBean {
    private String id;
    private int position;

    public NotityBean() {
    }

    public NotityBean(String id, int position) {
        this.id = id;
        this.position = position;
    }

    public NotityBean(String id) {
        this.id = id;
    }

    public NotityBean(int position) {
        this.position = position;
    }


    public String getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }
}
