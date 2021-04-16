package com.ayla.hotelsaas.bean;

import java.io.Serializable;
import java.util.List;

public class TreeListBean implements Serializable {

    /**
     * id : 1302801433778839601
     * parentId : 0
     * contentName : 成都分店
     * children : [{"id":"1302801490792013892","parentId":"1302801433778839601","contentName":"天狗大厦","children":[]},{"id":"1302801538808406093","parentId":"1302801433778839601","contentName":"麻瓜大厦","children":[{"id":"1302801612745596988","parentId":"1302801538808406093","contentName":"7L","children":[{"id":"1302848635435868189","parentId":"1302801612745596988","contentName":"0702主题房","children":[]},{"id":"1302848748094873642","parentId":"1302801612745596988","contentName":"0703商务房","children":[]}]},{"id":"1302801706798669862","parentId":"1302801538808406093","contentName":"6L","children":[]}]}]
     */

    private String id;
    private String parentId;
    private String contentName;
    private long roomTypeId;//房型
    private List<TreeListBean> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public List<TreeListBean> getChildren() {
        return children;
    }

    public void setChildren(List<TreeListBean> children) {
        this.children = children;
    }

    public long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
}
