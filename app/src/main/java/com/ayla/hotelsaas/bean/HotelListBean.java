package com.ayla.hotelsaas.bean;

import java.util.List;

public class HotelListBean {
    /**
     * current : 0
     * hitCount : true
     * optimizeCountSql : true
     * orders : [{"asc":true,"column":""}]
     * pages : 0
     * records : [{"busLinkId":0,"contactsEmail":"","contactsName":"","contactsPhone":"","createId":"","createName":"","createTime":"","hotelAddress":"","hotelName":"酒店名称1","hotelStatus":0,"id":"","isDeleted":0,"updateId":"","updateName":"","updateTime":""}]
     * searchCount : true
     * size : 0
     * total : 0
     */

    private int current;
    private boolean hitCount;
    private boolean optimizeCountSql;
    private int pages;
    private boolean searchCount;
    private int size;
    private int total;
    private List<OrdersBean> orders;
    private List<RecordsBean> records;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public boolean isHitCount() {
        return hitCount;
    }

    public void setHitCount(boolean hitCount) {
        this.hitCount = hitCount;
    }

    public boolean isOptimizeCountSql() {
        return optimizeCountSql;
    }

    public void setOptimizeCountSql(boolean optimizeCountSql) {
        this.optimizeCountSql = optimizeCountSql;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public boolean isSearchCount() {
        return searchCount;
    }

    public void setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<OrdersBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OrdersBean> orders) {
        this.orders = orders;
    }

    public List<RecordsBean> getRecords() {
        return records;
    }

    public void setRecords(List<RecordsBean> records) {
        this.records = records;
    }

    public static class OrdersBean {
        /**
         * asc : true
         * column :
         */

        private boolean asc;
        private String column;

        public boolean isAsc() {
            return asc;
        }

        public void setAsc(boolean asc) {
            this.asc = asc;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }
    }

    public static class RecordsBean {
        /**
         * busLinkId : 0
         * contactsEmail :
         * contactsName :
         * contactsPhone :
         * createId :
         * createName :
         * createTime :
         * hotelAddress :
         * hotelName : 酒店名称1
         * hotelStatus : 0
         * id :
         * isDeleted : 0
         * updateId :
         * updateName :
         * updateTime :
         */

        private long busLinkId;
        private String contactsEmail;
        private String contactsName;
        private String contactsPhone;
        private String createId;
        private String createName;
        private String createTime;
        private String hotelAddress;
        private String hotelName;
        private int hotelStatus;
        private String id;
        private int isDeleted;
        private String updateId;
        private String updateName;
        private String updateTime;

        public long getBusLinkId() {
            return busLinkId;
        }

        public void setBusLinkId(long busLinkId) {
            this.busLinkId = busLinkId;
        }

        public String getContactsEmail() {
            return contactsEmail;
        }

        public void setContactsEmail(String contactsEmail) {
            this.contactsEmail = contactsEmail;
        }

        public String getContactsName() {
            return contactsName;
        }

        public void setContactsName(String contactsName) {
            this.contactsName = contactsName;
        }

        public String getContactsPhone() {
            return contactsPhone;
        }

        public void setContactsPhone(String contactsPhone) {
            this.contactsPhone = contactsPhone;
        }

        public String getCreateId() {
            return createId;
        }

        public void setCreateId(String createId) {
            this.createId = createId;
        }

        public String getCreateName() {
            return createName;
        }

        public void setCreateName(String createName) {
            this.createName = createName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getHotelAddress() {
            return hotelAddress;
        }

        public void setHotelAddress(String hotelAddress) {
            this.hotelAddress = hotelAddress;
        }

        public String getHotelName() {
            return hotelName;
        }

        public void setHotelName(String hotelName) {
            this.hotelName = hotelName;
        }

        public int getHotelStatus() {
            return hotelStatus;
        }

        public void setHotelStatus(int hotelStatus) {
            this.hotelStatus = hotelStatus;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(int isDeleted) {
            this.isDeleted = isDeleted;
        }

        public String getUpdateId() {
            return updateId;
        }

        public void setUpdateId(String updateId) {
            this.updateId = updateId;
        }

        public String getUpdateName() {
            return updateName;
        }

        public void setUpdateName(String updateName) {
            this.updateName = updateName;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
