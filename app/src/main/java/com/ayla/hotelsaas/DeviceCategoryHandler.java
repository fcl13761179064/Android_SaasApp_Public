package com.ayla.hotelsaas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.ui.A2GatewayAddGuideActivity;
import com.ayla.hotelsaas.ui.A2GatewaySelectActivity;
import com.ayla.hotelsaas.ui.AylaGatewayAddGuideActivity;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.ui.DeviceAddGuideActivity;
import com.ayla.hotelsaas.ui.GatewaySelectActivity;
import com.ayla.hotelsaas.ui.HongyanGatewayAddGuideActivity;
import com.ayla.hotelsaas.utils.TempUtils;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 添加、替换 设备的 操作业务处理
 * 实现者必须在{@link Activity#(int, int, Intent)} 中调用本类的{@link #onActivityResult(int, int, Intent)}
 */
public class DeviceCategoryHandler {
    public static final int REQUEST_CODE_ADD_DEVICE = 0X1071;
    private final int REQUEST_CODE_SELECT_GATEWAY = 0X1072;

    private final long scopeId;
    private final Context fromContext;
    private Activity activity;
    private Fragment fragment;
    private String targetGatewayDeviceId;//替换节点设备时，节点设备所属的网关
    private Bundle addForWaitBundle, replaceInfoBundle;

    public DeviceCategoryHandler(Activity fromContext, long scopeId) {
        this.activity = fromContext;
        this.fromContext = fromContext;
        this.scopeId = scopeId;
    }

    public DeviceCategoryHandler(Fragment fromContext, long scopeId) {
        this.fragment = fromContext;
        this.fromContext = fromContext.getContext();
        this.scopeId = scopeId;
    }

    /**
     * 添加待绑定设备 或者 替换设备
     *
     * @param deviceCategoryBeans
     * @param intent              添加待绑定设备时，包含{@link Bundle addForWait}
     *                            替换设备时，包含{@link Bundle replaceInfo}
     */
    public void bindOrReplace(List<DeviceCategoryBean> deviceCategoryBeans, Intent intent, DeviceCategoryBean.SubBean.NodeBean deviceNodeBean) {
        addForWaitBundle = intent.getBundleExtra("addForWait");
        if (deviceNodeBean != null && addForWaitBundle != null) {//绑定待添加设备逻辑
            handleAddJump(new DeviceCategoryBean.SubBean.NodeBean[]{deviceNodeBean});
            return;
        }

        replaceInfoBundle = intent.getBundleExtra("replaceInfo");
        if (replaceInfoBundle != null) {//替换设备逻辑
            String replaceDeviceId = replaceInfoBundle.getString("replaceDeviceId");
            targetGatewayDeviceId = replaceInfoBundle.getString("targetGatewayDeviceId");
            DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(replaceDeviceId);
            for (DeviceCategoryBean deviceCategoryBean : deviceCategoryBeans) {
                for (DeviceCategoryBean.SubBean subBean : deviceCategoryBean.getSub()) {
                    for (DeviceCategoryBean.SubBean.NodeBean nodeBean : subBean.getNode()) {
                        if (TextUtils.equals(nodeBean.getPid(), devicesBean.getPid())) {
                            handleAddJump(new DeviceCategoryBean.SubBean.NodeBean[]{nodeBean});
                            return;
                        }
                    }
                }
            }
//            handleShouldExit();
        }

    }

    /**
     * 添加指定品类的设备
     *
     * @param subBeans
     */
    public void handleAddJump(DeviceCategoryBean.SubBean.NodeBean[] subBeans) {
        List<DeviceListBean.DevicesBean> aylaGateways = new ArrayList<>();//当前已有的艾拉网关
        List<DeviceListBean.DevicesBean> hyGateways = new ArrayList<>();//当前已有的鸿雁网关

        for (DeviceListBean.DevicesBean device : MyApplication.getInstance().getDevicesBean()) {
            if (TempUtils.isDeviceGateway(device) && (TextUtils.isEmpty(targetGatewayDeviceId) || TextUtils.equals(device.getDeviceId(), targetGatewayDeviceId))) {
                if (device.getCuId() == 0) {
                    aylaGateways.add(device);
                } else if (device.getCuId() == 1) {
                    hyGateways.add(device);
                }
            }
        }

        if (subBeans.length == 1) {
            DeviceCategoryBean.SubBean.NodeBean nodeBean = subBeans[0];
            boolean is_has_A2 = loadGateway(aylaGateways);
            if (is_has_A2 && nodeBean.getOemModel().size() > 1) {//A2网关
                Bundle addInfo = generateA2AddInfoBundle(nodeBean);
                Intent mainActivity = new Intent(fromContext, A2GatewaySelectActivity.class);
                mainActivity.putExtra("addInfo", addInfo);
                mainActivity.putExtra("nodeBean", nodeBean);
                startActivityForResult(mainActivity, REQUEST_CODE_SELECT_GATEWAY);
            } else {
                int networkType = calculateNetworkType(nodeBean);
                Bundle addInfo = generateAddInfoBundle(nodeBean);
                if (networkType == 2) {//艾拉网关
                    if (nodeBean.getPid().equalsIgnoreCase("ZBGW0-A000002")) {
                        Intent mainActivity = new Intent(fromContext, A2GatewayAddGuideActivity.class);
                        mainActivity.putExtra("addInfo", addInfo);
                        startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
                    } else {
                        Intent mainActivity = new Intent(fromContext, AylaGatewayAddGuideActivity.class);
                        mainActivity.putExtra("addInfo", addInfo);
                        startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
                    }

                } else if (networkType == 3) {//跳转艾拉节点
                    if (aylaGateways.size() == 0) {//没有艾拉网关
                        CustomToast.makeText(fromContext, "请先绑定网关", R.drawable.ic_toast_warming);
                    } else if (aylaGateways.size() == 1) {//一个艾拉网关
                        DeviceListBean.DevicesBean gateway = aylaGateways.get(0);
                        if (TempUtils.isDeviceOnline(gateway)) {//网关在线
                            Intent mainActivity = new Intent(fromContext, DeviceAddGuideActivity.class);
                            addInfo.putString("deviceId", gateway.getDeviceId());
                            mainActivity.putExtra("addInfo", addInfo);
                            startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
                        } else {
                            CustomToast.makeText(fromContext, "当前网关离线", R.drawable.ic_toast_warming);
                        }
                    } else {//多个网关
                        Intent mainActivity = new Intent(fromContext, GatewaySelectActivity.class);
                        mainActivity.putExtra("addInfo", addInfo);
                        mainActivity.putExtra("sourceId", nodeBean.getSource());
                        startActivityForResult(mainActivity, REQUEST_CODE_SELECT_GATEWAY);
                    }
                } else if (networkType == 5) {//跳转艾拉wifi
                    Intent mainActivity = new Intent(fromContext, DeviceAddGuideActivity.class);
                    mainActivity.putExtra("addInfo", addInfo);
                    startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
                } else if (networkType == 1) {//跳转鸿雁网关
                    Intent mainActivity = new Intent(fromContext, HongyanGatewayAddGuideActivity.class);
                    mainActivity.putExtra("addInfo", addInfo);
                    startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
                } else if (networkType == 4) {//跳转鸿雁节点
                    if (hyGateways.size() == 0) {//没有鸿雁网关
                        CustomToast.makeText(fromContext, "请先绑定网关", R.drawable.ic_toast_warming);
                    } else if (hyGateways.size() == 1) {//一个网关
                        DeviceListBean.DevicesBean gateway = hyGateways.get(0);
                        if (TempUtils.isDeviceOnline(gateway)) {//网关在线
                            Intent mainActivity = new Intent(fromContext, DeviceAddGuideActivity.class);
                            addInfo.putString("deviceId", gateway.getDeviceId());
                            mainActivity.putExtra("addInfo", addInfo);
                            startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
                        } else {
                            CustomToast.makeText(fromContext, "当前网关离线", R.drawable.ic_toast_warming);
                        }
                    } else {//多个网关
                        Intent mainActivity = new Intent(fromContext, GatewaySelectActivity.class);
                        mainActivity.putExtra("addInfo", addInfo);
                        mainActivity.putExtra("sourceId", nodeBean.getSource());
                        startActivityForResult(mainActivity, REQUEST_CODE_SELECT_GATEWAY);
                    }
                }
            }
        } else if (subBeans.length == 2) {
            DeviceCategoryBean.SubBean.NodeBean nodeBean1 = subBeans[0];
            DeviceCategoryBean.SubBean.NodeBean nodeBean2 = subBeans[1];
            if (nodeBean1.getIsNeedGateway() == 1 && nodeBean2.getIsNeedGateway() == 1 && nodeBean1.getSource() != nodeBean2.getSource()) {
                DeviceCategoryBean.SubBean.NodeBean aylaNodeBean = null;//待绑定的艾拉节点描述
                DeviceCategoryBean.SubBean.NodeBean hyNodeBean = null;//待绑定的鸿雁节点描述
                if (nodeBean1.getSource() == 0) {
                    aylaNodeBean = nodeBean1;
                } else if (nodeBean1.getSource() == 1) {
                    hyNodeBean = nodeBean1;
                }
                if (nodeBean2.getSource() == 0) {
                    aylaNodeBean = nodeBean2;
                } else if (nodeBean2.getSource() == 1) {
                    hyNodeBean = nodeBean2;
                }
                if (aylaNodeBean != null && hyNodeBean != null) {//一个icon 表示 两种 节点设备，一个鸿雁、一个艾拉。
                    if (aylaGateways.size() == 0 && hyGateways.size() == 0) {//没有绑定过任何网关
                        CustomToast.makeText(fromContext, "请先绑定网关", R.drawable.ic_toast_warming);
                    } else if (aylaGateways.size() + hyGateways.size() > 1) {//当前存在多个网关 ,跳转到网关选择页面
                        Intent mainActivity = new Intent(fromContext, GatewaySelectActivity.class);
                        ArrayList<DeviceCategoryBean.SubBean.NodeBean> nodeBeans = new ArrayList<>();
                        nodeBeans.add(aylaNodeBean);
                        nodeBeans.add(hyNodeBean);
                        mainActivity.putExtra("waitCategoryNodes", nodeBeans);
                        if (aylaGateways.size() == 0) {
                            mainActivity.putExtra("sourceId", hyGateways.get(0).getCuId());
                        } else if (hyGateways.size() == 0) {
                            mainActivity.putExtra("sourceId", aylaGateways.get(0).getCuId());
                        }
                        startActivityForResult(mainActivity, REQUEST_CODE_SELECT_GATEWAY);
                    } else {//只有一个网关
                        DeviceCategoryBean.SubBean.NodeBean forAddNode;
                        if (aylaGateways.size() == 0) {
                            forAddNode = hyNodeBean;
                        } else {
                            forAddNode = aylaNodeBean;
                        }
                        handleAddJump(new DeviceCategoryBean.SubBean.NodeBean[]{forAddNode});
                    }
                }
            }
        }
    }

    public boolean loadGateway(List<DeviceListBean.DevicesBean> aylaGateways) {
        if (aylaGateways != null && aylaGateways.size() > 0) {
            for (DeviceListBean.DevicesBean device : aylaGateways) {
                if (TempUtils.isDeviceGateway(device) && device.getPid().equalsIgnoreCase("ZBGW0-A000002")) {
                    return true;
                }
            }
        }
        return false;
    }

    private Bundle generateAddInfoBundle(DeviceCategoryBean.SubBean.NodeBean subBean) {
        Bundle addInfo = new Bundle();
        addInfo.putInt("networkType", calculateNetworkType(subBean));
        addInfo.putInt("cuId", subBean.getSource());
        addInfo.putLong("scopeId", scopeId);
        addInfo.putString("pid", subBean.getPid());
        addInfo.putString("productName", subBean.getProductName());
        addInfo.putString("deviceUrl", subBean.getActualIcon());
        if (subBean.getSource() == 0) {
            addInfo.putString("deviceCategory", subBean.getOemModel().get("0"));
        } else {
            addInfo.putString("deviceCategory", subBean.getOemModel().get("1"));
        }
        addInfo.putString("productName", subBean.getProductName());
        if (addForWaitBundle != null) {//带绑定设备
            addInfo.putString("waitBindDeviceId", addForWaitBundle.getString("waitBindDeviceId"));
            addInfo.putString("nickname", addForWaitBundle.getString("nickname"));
        }
        if (replaceInfoBundle != null) {//替换设备
            addInfo.putString("replaceDeviceId", replaceInfoBundle.getString("replaceDeviceId"));
            addInfo.putString("nickname", replaceInfoBundle.getString("replaceDeviceNickname"));
            addInfo.putString("targetGatewayDeviceId", replaceInfoBundle.getString("targetGatewayDeviceId"));
        }
        return addInfo;
    }

    private Bundle generateA2AddInfoBundle(DeviceCategoryBean.SubBean.NodeBean subBean) {
        Bundle addInfo = new Bundle();
        addInfo.putLong("scopeId", scopeId);
        addInfo.putString("pid", subBean.getPid());
        addInfo.putString("productName", subBean.getProductName());
        addInfo.putString("deviceUrl", subBean.getActualIcon());
        if (addForWaitBundle != null) {
            addInfo.putString("waitBindDeviceId", addForWaitBundle.getString("waitBindDeviceId"));
            addInfo.putString("nickname", addForWaitBundle.getString("nickname"));
        }
        if (replaceInfoBundle != null) {
            addInfo.putString("replaceDeviceId", replaceInfoBundle.getString("replaceDeviceId"));
            addInfo.putString("nickname", replaceInfoBundle.getString("replaceDeviceNickname"));
            addInfo.putString("targetGatewayDeviceId", replaceInfoBundle.getString("targetGatewayDeviceId"));
        }
        return addInfo;
    }

    /**
     * @param subBean
     * @return 2：艾拉网关  3：艾拉节点 5：艾拉WiFi   1：鸿雁网关 4：鸿雁节点
     */
    private int calculateNetworkType(DeviceCategoryBean.SubBean.NodeBean subBean) {
        int networkType = -1;//2：艾拉网关  3：艾拉节点 5：艾拉WiFi   1：鸿雁网关 4：鸿雁节点
        if (subBean.getSource() == 0) {//艾拉云
            if (subBean.getProductType() == 1) {//网关设备
                networkType = 2;//艾拉网关
            } else {//其他设备
                if (subBean.getIsNeedGateway() == 1) {//节点设备
                    networkType = 3;//艾拉节点
                } else {
                    networkType = 5;//艾拉WiFi
                }
            }
        } else if (subBean.getSource() == 1) {//阿里云
            if (subBean.getProductType() == 1) {//网关设备
                networkType = 1;//鸿雁网关
            } else {//其他设备
                if (subBean.getIsNeedGateway() == 1) {//节点设备
                    networkType = 4;//鸿雁节点
                }
            }
        }
        return networkType;
    }


    private void startActivityForResult(@SuppressLint("UnknownNullness") Intent intent,
                                        int requestCode) {
        if (activity != null) {
            activity.startActivityForResult(intent, requestCode);
        } else if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 必须在实现者的{@link Activity#(int, int, Intent)} 中调用
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_DEVICE) {
            if (resultCode == RESULT_OK) {
//                finish();
            } else {
//                handleShouldExit();
            }
        } else if (requestCode == REQUEST_CODE_SELECT_GATEWAY) {
            if (resultCode == RESULT_OK) {
                String deviceId = data.getStringExtra("deviceId");
                Bundle addInfo = null;
                Intent mainActivity = new Intent(fromContext, DeviceAddGuideActivity.class);

                if (data.hasExtra("addInfo")) {
                    addInfo = data.getBundleExtra("addInfo");
                } else if (data.hasExtra("waitCategoryNodes")) {
                    List<DeviceCategoryBean.SubBean.NodeBean> nodes = (List<DeviceCategoryBean.SubBean.NodeBean>) data.getSerializableExtra("waitCategoryNodes");

                    DeviceListBean.DevicesBean gatewayDevice = MyApplication.getInstance().getDevicesBean(deviceId);
                    for (DeviceCategoryBean.SubBean.NodeBean subBean : nodes) {
                        if (subBean.getSource() == gatewayDevice.getCuId()) {
                            addInfo = generateAddInfoBundle(subBean);
                            break;
                        }
                    }
                }
                if (addInfo != null) {
                    addInfo.putString("deviceId", deviceId);
                    mainActivity.putExtra("addInfo", addInfo);
                    startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
                }
            } else {
//                handleShouldExit();
            }
        }
    }
}
