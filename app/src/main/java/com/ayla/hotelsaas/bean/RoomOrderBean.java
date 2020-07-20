package com.ayla.hotelsaas.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @描述 工作订单的bean
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class RoomOrderBean implements Serializable {
    /**
     * programName : saas酒店
     * voucherId : 348749
     * palyTime : 2019-12-06 17:54:00
     * status : 待施工
     */


    private String currentPage;
    private String pageSize;
    private List<RoomOrderBean.RoomOrder> roomOrderContent;

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public List<RoomOrder> getRoomOrderContent() {
        return roomOrderContent;
    }

    public void setRoomOrderContent(List<RoomOrder> roomOrderContent) {
        this.roomOrderContent = roomOrderContent;
    }

    public static class RoomOrder implements Serializable {
        private String resourceRoomNum;
        private String resourceRoomId;

        public String getResourceRoomNum() {
            return resourceRoomNum;
        }

        public void setResourceRoomNum(String resourceRoomNum) {
            this.resourceRoomNum = resourceRoomNum;
        }

        public String getResourceRoomId() {
            return resourceRoomId;
        }

        public void setResourceRoomId(String resourceRoomId) {
            this.resourceRoomId = resourceRoomId;
        }
    }
}
