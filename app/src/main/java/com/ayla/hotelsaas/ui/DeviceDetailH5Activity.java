package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.events.DeviceAddEvent;
import com.ayla.hotelsaas.events.DeviceChangedEvent;
import com.ayla.hotelsaas.events.DeviceRemovedEvent;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SizeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.DWebView;

/**
 * 设备单控页面
 * 参数
 * String deviceId
 * long scopeId
 */
public class DeviceDetailH5Activity extends BaseWebViewActivity {

    private static final String TAG = DeviceDetailH5Activity.class.getSimpleName();

    private CompositeDisposable mCompositeDisposable;

    @BindView(R.id.web_view)
    DWebView mWebView;

    @BindView(R.id.empty_layout)
    View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeDisposable = new CompositeDisposable();
        EventBus.getDefault().register(this);
        mWebView.loadUrl(Constance.getDeviceControlBaseUrl());
    }

    @Override
    protected View getEmptyView() {
        return emptyView;
    }

    @Override
    protected DWebView getWebView() {
        return mWebView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_detail;
    }

    private long scopeId;
    private String deviceId;

    @Override
    protected void initView() {
        deviceId = getIntent().getStringExtra("deviceId");
        scopeId = getIntent().getLongExtra("scopeId", 0);
    }

    @Override
    protected void initListener() {
        mWebView.addJavascriptObject(new Object() {
            @JavascriptInterface
            public void back(Object msg) {
                Log.d(TAG, "back: " + msg);
                finish();
            }

            @JavascriptInterface
            public void navigationTo(Object msg) throws Exception {
                Log.d(TAG, "navigationTo: " + msg);
                String code = new JSONObject(msg.toString()).getString("state");
                switch (code) {
                    case "more": {
                        Intent intent = new Intent(DeviceDetailH5Activity.this, DeviceMoreActivity.class);
                        intent.putExtra("deviceId", deviceId);
                        intent.putExtra("scopeId", scopeId);
                        startActivity(intent);
                    }
                    break;
                    case "login": {
                        final Intent intent = new Intent(DeviceDetailH5Activity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    break;
                }
            }

            @JavascriptInterface
            public void isLightMode(Object msg) throws JSONException {
                Log.d(TAG, "isLightMode: " + msg);
                boolean isLightMode = new JSONObject(msg.toString()).getBoolean("state");
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        BarUtils.setStatusBarLightMode(DeviceDetailH5Activity.this, isLightMode);
                    }
                });
            }
        }, "miya.h5.webview");
        mWebView.addJavascriptObject(new Object() {
            @JavascriptInterface
            public void ready(Object msg) {
                Log.d(TAG, "ready: " + msg);
                miya_native_dataShare_init();
            }

            @JavascriptInterface
            public void token(Object msg) throws JSONException {
                Log.d(TAG, "token: " + msg);
                String token = new JSONObject(msg.toString()).getString("state");
                SharePreferenceUtils.saveString(getApplicationContext(), Constance.SP_Login_Token, token);
            }
        }, "miya.h5.dataShare");
        mWebView.addJavascriptObject(new Object() {
            @JavascriptInterface
            public void refreshDeviceList(Object msg) {
                Log.d(TAG, "refreshDeviceList: " + msg);
                EventBus.getDefault().post(new DeviceAddEvent());
            }
        }, "miya.h5.event");
        mWebView.addJavascriptObject(new Object() {
            @JavascriptInterface
            public void getSceneList(Object msg, CompletionHandler<JSONObject> handler) {
                miya_h5_scene_getSceneList(handler);
            }

            @JavascriptInterface
            public void getDsnCodeValueToSceneList(Object msg, CompletionHandler<JSONObject> handler) throws JSONException {
                Log.d(TAG, "getDsnCodeValueToSceneList: " + msg);
                JSONArray state = new JSONObject(msg.toString()).getJSONArray("state");
                Disposable subscribe = RequestModel.getInstance().getRuleListByUniqListFunction(scopeId, state.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                Log.d(TAG, "accept: " + o);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: ", throwable);
                            }
                        });

            }

            @JavascriptInterface
            public void createDsnCodeValueToScene(Object msg, CompletionHandler<JSONObject> handler) {
                Log.d(TAG, "createDsnCodeValueToScene: " + msg);
            }

            @JavascriptInterface
            public void deleteDsnCodeValueToScene(Object msg, CompletionHandler<JSONObject> handler) {
                Log.d(TAG, "deleteDsnCodeValueToScene: " + msg);
            }

            @JavascriptInterface
            public void updateDsnCodeValueToScene(Object msg, CompletionHandler<JSONObject> handler) {
                Log.d(TAG, "updateDsnCodeValueToScene: " + msg);
            }

            @JavascriptInterface
            public void excuteScene(Object msg, CompletionHandler<JSONObject> handler) {
                Log.d(TAG, "excuteScene: " + msg);
            }
        }, "miya.h5.scene");
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @OnClick(R.id.bt_refresh)
    @Override
    public void handleRefreshClick() {
        super.handleRefreshClick();
    }

    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void miya_native_dataShare_init() {
        try {
            DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
            if (devicesBean == null) {
                return;
            }
            JSONObject jsonObject = new JSONObject();
            final String token = SharePreferenceUtils.getString(MyApplication.getInstance(), Constance.SP_Login_Token, null);
            final String refreshToken = SharePreferenceUtils.getString(MyApplication.getInstance(), Constance.SP_Refresh_Token, null);
            jsonObject.put("api", "construction");
            jsonObject.put("scopeId", String.valueOf(scopeId));
            jsonObject.put("token", token);
            jsonObject.put("refreshToken", refreshToken);
            jsonObject.put("marginTop", SizeUtils.px2dp(BarUtils.getStatusBarHeight()));
            JSONObject deviceJsonObject = new JSONObject();
            deviceJsonObject.put("nickName", devicesBean.getNickname());
            deviceJsonObject.put("deviceName", devicesBean.getDeviceName());
            deviceJsonObject.put("deviceStatus", devicesBean.getDeviceStatus());
            deviceJsonObject.put("cuId", devicesBean.getCuId());
            deviceJsonObject.put("pid", devicesBean.getPid());
            deviceJsonObject.put("deviceId", devicesBean.getDeviceId());
            jsonObject.put("device", deviceJsonObject);
            JSONObject state = new JSONObject().put("state", jsonObject);
            mWebView.callHandler("miya.native.dataShare.init", new Object[]{state});
            Log.d(TAG, "miya_native_dataShare_init: " + state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void miya_h5_scene_getSceneList(CompletionHandler<JSONObject> handler) {
        Disposable subscribe = RequestModel.getInstance().fetchRuleEngines(scopeId)
                .map(new Function<List<RuleEngineBean>, JSONObject>() {
                    @Override
                    public JSONObject apply(@NonNull List<RuleEngineBean> ruleEngineBeans) throws Exception {
                        JSONObject result = new JSONObject();
                        JSONArray dataArray = new JSONArray();
                        for (RuleEngineBean ruleEngineBean : ruleEngineBeans) {
                            if (ruleEngineBean.getRuleType() == BaseSceneBean.RULE_TYPE.ONE_KEY) {
                                JSONObject jsonBeanObject = new JSONObject();
                                jsonBeanObject.put("displayName", ruleEngineBean.getRuleName());
                                jsonBeanObject.put("sceneId", ruleEngineBean.getRuleId());
                                jsonBeanObject.put("imgUrl", ruleEngineBean.getIconPath());
                                dataArray.put(jsonBeanObject);
                            }
                        }
                        result.put("data", dataArray);
                        result.put("code", 0);
                        result.put("msg", "success");
                        return result;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject s) throws Exception {
                        handler.complete(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handler.complete();
                    }
                });
        mCompositeDisposable.add(subscribe);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceRemoved(DeviceRemovedEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceChangedEvent(DeviceChangedEvent event) {
        miya_native_dataShare_init();
    }
}
