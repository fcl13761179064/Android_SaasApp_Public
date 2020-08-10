package com.ayla.hotelsaas.feiyansdk;

import java.util.List;

public interface OnDeviceAddListener {


    void showToast(String message);


    void onSupportDeviceSuccess(List<SupportDeviceListItem> mSupportDeviceListItems);


    void onFilterComplete(List<FoundDeviceListItem> foundDeviceListItems);

}
