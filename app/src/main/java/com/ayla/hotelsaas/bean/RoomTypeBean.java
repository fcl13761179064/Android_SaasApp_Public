package com.ayla.hotelsaas.bean;

public class RoomTypeBean {

        private int id;
        private String typeName;
        private long scopeId;
        private int tradeId;
        private String description;
        private int typeStatus;
        private String updateId;
        private String updateName;
        private String updateTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public long getScopeId() {
            return scopeId;
        }

        public void setScopeId(long scopeId) {
            this.scopeId = scopeId;
        }

        public int getTradeId() {
            return tradeId;
        }

        public void setTradeId(int tradeId) {
            this.tradeId = tradeId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getTypeStatus() {
            return typeStatus;
        }

        public void setTypeStatus(int typeStatus) {
            this.typeStatus = typeStatus;
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
