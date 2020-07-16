package com.ayla.hotelsaas.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @描述  工作订单的bean
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class WorkOrderBean implements Serializable {
    /**
     * programName : saas酒店
     * voucherId : 348749
     * palyTime : 2019-12-06 17:54:00
     * status : 待施工
     */
    private String currentPage;
    private String pageSize;
    private List<WorkOrder> workOrderContent;

    public static class WorkOrder  implements  Serializable{

        private String businessId;
        private String projectName;
        private String startDate;
        private String endDate;
        private String progressStatus;
        private String resourceId;
        private String resourceNum;

        public String getBusinessId() {
            return businessId;
        }

        public void setBusinessId(String businessId) {
            this.businessId = businessId;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
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

        public String getProgressStatus() {
            return progressStatus;
        }

        public void setProgressStatus(String progressStatus) {
            this.progressStatus = progressStatus;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public String getResourceNum() {
            return resourceNum;
        }

        public void setResourceNum(String resourceNum) {
            this.resourceNum = resourceNum;
        }
    }


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

    public List<WorkOrder> getWorkOrderContent() {
        return workOrderContent;
    }

    public void setWorkOrderContent(List<WorkOrder> workOrderContent) {
        this.workOrderContent = workOrderContent;
    }
}
