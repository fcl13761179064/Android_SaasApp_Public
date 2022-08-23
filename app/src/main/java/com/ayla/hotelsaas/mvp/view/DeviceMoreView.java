package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.FirmwareUpdateData;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;
import com.ayla.hotelsaas.constant.ShowWay;

import java.util.List;

public interface DeviceMoreView extends BaseView {

    //错误提示
    void renameFailed(Throwable throwable);

    //操作成功
    void renameSuccess(String newNickName);

    //移除成功
    void removeSuccess(Boolean is_rename);

    //失败成功
    void removeFailed(Throwable throwable);

    void cannotRenameFunction();

    void canRenameFunction();

    void showPurposeCategory(List<PurposeCategoryBean> purposeCategoryBeans);

    void updatePurposeSuccess();

    void updatePurposeFailed(Throwable throwable);

    void canSetSwitchDefault();

    void cannotSetSwitchDefault();

    void MarshallEntryPidSuccess(List<String> data);

    void MarshallEntryPidFail(Throwable throwable);

    //移交成功
    void transferSuccess(BaseResult is_rename);

    //移交失败
    void transferFailed(Throwable throwable);

    void updatetransferSuccess(BaseResult result);

    void updatetransferFailed(Throwable throwable);

    void getFirmwareNewVersion(BaseResult<FirmwareUpdateData> result);

    void getFirmwareNewVersionFail(Throwable throwable);

    void getCurrentFirmwareVersion(String version);

    void getFirmwareVersionFail(Throwable throwable);

    void onDeviceStatus(Boolean online);

    void onSaveDisplaySuccess(ShowWay showWay);

    void onDisplayFail(Throwable throwable);

    void getDisplaySuccess(ShowWay showWay);

    void initSwitchModeResult(boolean result, Throwable throwable);

}
