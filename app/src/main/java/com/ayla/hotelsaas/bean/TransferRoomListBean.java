package com.ayla.hotelsaas.bean;

import java.util.List;

public class TransferRoomListBean {

    /**
     * records : [{"id":"1302801706798669862","hotelId":"1302800899034439760","hotelName":"TEST_酒店","structId":"7","structName":"楼层","contentName":"6L房间","updateTime":"2020-09-09 10:38:13","updateName":"","pathNames":"成都分店 / 麻瓜大厦","status":null},{"id":"1302801612745596988","hotelId":"1302800899034439760","hotelName":"TEST_酒店","structId":"7","structName":"楼层","contentName":"7L房间","updateTime":"2020-09-09 10:38:05","updateName":"","pathNames":"成都分店 / 麻瓜大厦","status":null},{"id":"1302801433778839601","hotelId":"1302800899034439760","hotelName":"TEST_酒店","structId":"5","structName":"分店","contentName":"成都分店房间01","updateTime":"2020-09-09 10:37:48","updateName":"","pathNames":"无","status":null},{"id":"1302801538808406093","hotelId":"1302800899034439760","hotelName":"TEST_酒店","structId":"6","structName":"大楼","contentName":"麻瓜大厦房间01","updateTime":"2020-09-09 10:37:39","updateName":"","pathNames":"成都分店","status":null},{"id":"1302801490792013892","hotelId":"1302800899034439760","hotelName":"TEST_酒店","structId":"6","structName":"大楼","contentName":"天狗大厦房间01","updateTime":"2020-09-09 10:37:25","updateName":"","pathNames":"成都分店","status":null},{"id":"1302848748094873642","hotelId":"1302800899034439760","hotelName":"TEST_酒店","structId":"8","structName":"房间号","contentName":"0703商务房","updateTime":"2020-09-07 05:58:41","updateName":"","pathNames":"成都分店 / 麻瓜大厦 / 7L","status":null},{"id":"1302848635435868189","hotelId":"1302800899034439760","hotelName":"TEST_酒店","structId":"8","structName":"房间号","contentName":"0702主题房","updateTime":"2020-09-07 05:58:15","updateName":"","pathNames":"成都分店 / 麻瓜大厦 / 7L","status":null}]
     * total : 0
     * size : 10
     * current : 1
     * orders : []
     * optimizeCountSql : true
     * hitCount : false
     * searchCount : true
     * pages : 0
     */

    private List<RecordsBean> records;

    public List<RecordsBean> getRecords() {
        return records;
    }

    public void setRecords(List<RecordsBean> records) {
        this.records = records;
    }

    public static class RecordsBean {
        /**
         * id : 1302801706798669862
         * hotelId : 1302800899034439760
         * hotelName : TEST_酒店
         * structId : 7
         * structName : 楼层
         * contentName : 6L房间
         * updateTime : 2020-09-09 10:38:13
         * updateName :
         * pathNames : 成都分店 / 麻瓜大厦
         * status : null
         */

        private String id;
        private String hotelId;
        private String hotelName;
        private String structId;
        private String structName;
        private String contentName;
        private String updateTime;
        private String updateName;
        private String pathNames;
        private Object status;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getHotelId() {
            return hotelId;
        }

        public void setHotelId(String hotelId) {
            this.hotelId = hotelId;
        }

        public String getHotelName() {
            return hotelName;
        }

        public void setHotelName(String hotelName) {
            this.hotelName = hotelName;
        }

        public String getStructId() {
            return structId;
        }

        public void setStructId(String structId) {
            this.structId = structId;
        }

        public String getStructName() {
            return structName;
        }

        public void setStructName(String structName) {
            this.structName = structName;
        }

        public String getContentName() {
            return contentName;
        }

        public void setContentName(String contentName) {
            this.contentName = contentName;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUpdateName() {
            return updateName;
        }

        public void setUpdateName(String updateName) {
            this.updateName = updateName;
        }

        public String getPathNames() {
            return pathNames;
        }

        public void setPathNames(String pathNames) {
            this.pathNames = pathNames;
        }

        public Object getStatus() {
            return status;
        }

        public void setStatus(Object status) {
            this.status = status;
        }
    }
}
