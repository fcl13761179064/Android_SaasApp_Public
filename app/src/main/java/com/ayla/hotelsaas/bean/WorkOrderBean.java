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
     * programName : 20161206选座套票
     * voucherId : 348749
     * palyTime : 2019-12-06 17:54:00
     * status : 待施工
     */
    private String programName;
    private String applyTime;
    private String status;

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
