package com.ayla.hotelsaas.bean;

import java.io.Serializable;
import java.util.List;

public class ConstructionBillListBean {

    /**
     * resultList : [{"id":"1","title":"艾拉智慧酒店","businessId":"1298506027704045655","type":0,"startDate":"2020-08-26 06:39:15","endDate":"2020-09-30 06:39:15","constructionStatus":2,"createId":"1290198260413161513","createName":"syssuperadmin","createTime":"2020-08-26 06:39:41","updateId":"1290198260413161513","updateName":"syssuperadmin","updateTime":"2020-08-26 06:39:41","isDeleted":0,"trade":"酒店"},{"id":"6","title":"酒店版展示箱01","businessId":"1299250249302331418","type":0,"startDate":"2020-08-31 06:29:36","endDate":"2020-09-01 06:29:36","constructionStatus":2,"createId":"1290198260413161513","createName":"syssuperadmin","createTime":"2020-08-31 06:29:55","updateId":"1290198260413161513","updateName":"syssuperadmin","updateTime":"2020-08-31 06:29:55","isDeleted":0,"trade":"酒店"},{"id":"63","title":"展厅项目9","businessId":"1305418026031575122","type":0,"startDate":null,"endDate":null,"constructionStatus":2,"createId":"1298510272788484174","createName":"施工人员","createTime":"2020-11-17 03:36:45","updateId":"1298510272788484174","updateName":"施工人员","updateTime":"2020-11-17 03:36:45","isDeleted":0,"trade":""}]
     * currentPage : 1
     * pageSize : 20
     * totalPages : 1
     * totalCount : 3
     */

    private int currentPage;
    private int pageSize;
    private int totalPages;
    private int totalCount;
    /**
     * id : 1
     * title : 艾拉智慧酒店
     * businessId : 1298506027704045655
     * type : 0
     * startDate : 2020-08-26 06:39:15
     * endDate : 2020-09-30 06:39:15
     * constructionStatus : 2
     * createId : 1290198260413161513
     * createName : syssuperadmin
     * createTime : 2020-08-26 06:39:41
     * updateId : 1290198260413161513
     * updateName : syssuperadmin
     * updateTime : 2020-08-26 06:39:41
     * isDeleted : 0
     * trade : 酒店
     */

    private List<ResultListBean> resultList;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<ResultListBean> getResultList() {
        return resultList;
    }

    public void setResultList(List<ResultListBean> resultList) {
        this.resultList = resultList;
    }

    public static class ResultListBean implements Serializable {
        private String id;
        private String title;
        private String businessId;
        private int type;//0:正式 1:展箱 2:展厅
        private String startDate;
        private String endDate;
        private int constructionStatus;
        private String createId;
        private String createName;
        private String createTime;
        private String updateId;
        private String updateName;
        private String updateTime;
        private int isDeleted;
        /**
         * 1	酒店	酒店行业
         * 2	家装	家装行业
         * 3	地产	地产行业
         * 4	公寓	公寓行业
         */
        private int trade;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBusinessId() {
            return businessId;
        }

        public void setBusinessId(String businessId) {
            this.businessId = businessId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public int getConstructionStatus() {
            return constructionStatus;
        }

        public void setConstructionStatus(int constructionStatus) {
            this.constructionStatus = constructionStatus;
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

        public int getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(int isDeleted) {
            this.isDeleted = isDeleted;
        }

        public int getTrade() {
            return trade;
        }

        public void setTrade(int trade) {
            this.trade = trade;
        }
    }
}
