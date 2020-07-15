package com.ayla.hotelsaas.bean;

import java.util.List;

/**
 * 设备添加列表页面
 */
public class DeviceCategoryBean {
    public String name;
    public List<SubBean> subBeans;

    public class SubBean {
        public String name;
        public String icon;
        public int mode;
    }
}
