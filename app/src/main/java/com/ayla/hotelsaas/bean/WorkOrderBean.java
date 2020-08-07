package com.ayla.hotelsaas.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @描述 工作订单的bean
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class WorkOrderBean implements Serializable {

    /**
     * resultList : [{"id":1,"title":"施工单1","businessId":2,"startDate":"2020-07-20T14:10:56","endDate":"2020-07-24T14:11:00","constructionStatus":1,"createId":"","createName":"json","createTime":"2020-07-20T06:11:38","updateId":"","updateName":"","updateTime":"2020-07-20T06:11:48","isDeleted":0}]
     * currentPage : 10
     * pageSize : 1
     * totalPages : 20
     * totalCount : 10
     */

    private int currentPage;
    private int pageSize;
    private int totalPages;
    private int totalCount;
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
        /**
         * id : 1
         * title : 施工单1
         * businessId : 2
         * startDate : 2020-07-20T14:10:56
         * endDate : 2020-07-24T14:11:00
         * constructionStatus : 1
         * createId :
         * createName : json
         * createTime : 2020-07-20T06:11:38
         * updateId :
         * updateName :
         * updateTime : 2020-07-20T06:11:48
         * isDeleted : 0
         */

        private long id;
        private String title;
        private long businessId;
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


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }


        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

         public long getBusinessId() {
            return businessId;
        }

        public void setBusinessId(long businessId) {
            this.businessId = businessId;
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
    }
}
