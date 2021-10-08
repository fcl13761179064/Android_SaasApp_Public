package com.ayla.hotelsaas.bean;

import java.util.List;

public class MoveWallBean {

        private int id;
        private String title;
        private Object tradeId;
        private Object tradeName;
        private long businessId;
        private int constructionStatus;
        private String startDate;
        private String endDate;
        private String updateTime;
        private String updateName;
        private long updateId;
        private int isDeleted;
        private String type;
        private List<UserListDTO> userList;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Object getTradeId() {
            return tradeId;
        }

        public void setTradeId(Object tradeId) {
            this.tradeId = tradeId;
        }

        public Object getTradeName() {
            return tradeName;
        }

        public void setTradeName(Object tradeName) {
            this.tradeName = tradeName;
        }

        public long getBusinessId() {
            return businessId;
        }

        public void setBusinessId(long businessId) {
            this.businessId = businessId;
        }

        public int getConstructionStatus() {
            return constructionStatus;
        }

        public void setConstructionStatus(int constructionStatus) {
            this.constructionStatus = constructionStatus;
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

        public long getUpdateId() {
            return updateId;
        }

        public void setUpdateId(long updateId) {
            this.updateId = updateId;
        }

        public int getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(int isDeleted) {
            this.isDeleted = isDeleted;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<UserListDTO> getUserList() {
            return userList;
        }

        public void setUserList(List<UserListDTO> userList) {
            this.userList = userList;
        }

        public static class ResourceListDTO {
            private long resourceId;
            private String resourceName;

            public long getResourceId() {
                return resourceId;
            }

            public void setResourceId(long resourceId) {
                this.resourceId = resourceId;
            }

            public String getResourceName() {
                return resourceName;
            }

            public void setResourceName(String resourceName) {
                this.resourceName = resourceName;
            }
        }

        public static class UserListDTO {
            private String userId;
            private String userName;
            private String phoneNumber;

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getPhoneNumber() {
                return phoneNumber;
            }

            public void setPhoneNumber(String phoneNumber) {
                this.phoneNumber = phoneNumber;
            }
    }
}
