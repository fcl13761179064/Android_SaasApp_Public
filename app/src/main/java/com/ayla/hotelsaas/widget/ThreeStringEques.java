package com.ayla.hotelsaas.widget;

import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean;

public class ThreeStringEques {


    public static boolean mIsEques(BaseSceneBean.Action action) {
        if (action.getTargetDeviceId().equals(action.getLeftValue()) &&action.getTargetDeviceId().equals(action.getRightValue())&&action.getLeftValue().equals(action.getRightValue())) {
            return true;
        }
        return false;
    }
}
