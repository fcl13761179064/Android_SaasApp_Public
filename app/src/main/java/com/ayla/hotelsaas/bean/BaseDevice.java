package com.ayla.hotelsaas.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class BaseDevice implements MultiItemEntity {
    public long id;
//    public Long createTime;
    private int bindType = -1;//设备绑定绑定类型，【0：默认，1：待绑定】 -1 表示是编组

    public int getBindType() {
        return bindType;
    }

    public void setBindType(int bindType) {
        this.bindType = bindType;
    }

    public static final int item_normal = 0;
    public static final int item_wait_add = 1;
    public static final int item_group=2;

    @Override
    public int getItemType() {
        if (bindType == 0) {
            return item_normal;
        } else if (bindType == 1) {
            return item_wait_add;
        } else return item_group;
    }
}
