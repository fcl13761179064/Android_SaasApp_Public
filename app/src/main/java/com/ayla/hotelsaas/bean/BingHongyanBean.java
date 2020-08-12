package com.ayla.hotelsaas.bean;

import java.io.Serializable;

public class BingHongyanBean implements Serializable {


    /**
     * id : 67eae32e-8b85-4040-932a-66a5050b597b
     * code : 200
     * message : null
     * localizedMsg : null
     * data : {"iotId":"VU6b9DNFxVHQ****dldF000101","categoryKey":"C****a","pageRouterUrl":"page/lvcamera/devicepanel"}
     */

    private String id;
    private int code;
    private Object message;
    private Object localizedMsg;
    private DataBean data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Object getLocalizedMsg() {
        return localizedMsg;
    }

    public void setLocalizedMsg(Object localizedMsg) {
        this.localizedMsg = localizedMsg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * iotId : VU6b9DNFxVHQ****dldF000101
         * categoryKey : C****a
         * pageRouterUrl : page/lvcamera/devicepanel
         */

        private String iotId;
        private String categoryKey;
        private String pageRouterUrl;

        public String getIotId() {
            return iotId;
        }

        public void setIotId(String iotId) {
            this.iotId = iotId;
        }

        public String getCategoryKey() {
            return categoryKey;
        }

        public void setCategoryKey(String categoryKey) {
            this.categoryKey = categoryKey;
        }

        public String getPageRouterUrl() {
            return pageRouterUrl;
        }

        public void setPageRouterUrl(String pageRouterUrl) {
            this.pageRouterUrl = pageRouterUrl;
        }
    }
}
