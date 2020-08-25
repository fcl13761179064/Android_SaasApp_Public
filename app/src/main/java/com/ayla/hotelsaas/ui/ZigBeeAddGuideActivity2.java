package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.GatewayAddGuidePresenter;
import com.ayla.hotelsaas.widget.LarkWebView;

import org.json.JSONObject;

import butterknife.BindView;

/**
 * ZigBee添加引导页面，嵌套H5
 * 进入时必须带入网关deviceId 、cuId 、scopeId 、deviceName、deviceCategory
 */
public class ZigBeeAddGuideActivity2 extends BaseMvpActivity {
    private static final String TAG = "wyj";
    @BindView(R.id.web_view)
    LarkWebView mWebView;
    @BindView(R.id.error_page)
    View mErrorPageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int cuId = getIntent().getIntExtra("cuId", 0);
        String deviceName = getIntent().getStringExtra("deviceName");
        mWebView.loadUrl(String.format("https://smarthotel-h5-test.ayla.com.cn/addDevice.html?cuId=%s&deviceName=%s", cuId, deviceName));
    }

    @Override
    protected GatewayAddGuidePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_zigbee_add_guide_2;
    }

    @Override
    protected void initView() {
        mErrorPageView.setVisibility(View.GONE);
    }

    @Override
    protected void initListener() {
        mWebView.registerLoadCallBack(new LarkWebView.LoadCallBack() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "onPageStarted: " + url);
                mErrorPageView.setVisibility(View.GONE);
                showProgress();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "onPageFinished: " + url);
                hideProgress();
            }

            @Override
            public void onPageError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d(TAG, "onPageError: " + failingUrl);
                mErrorPageView.setVisibility(View.VISIBLE);
                Button refreshButton = mErrorPageView.findViewById(R.id.refresh_bt);
                refreshButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWebView.reload();
                    }
                });
            }
        });
        mWebView.registerJsBridgeCallBack(new LarkWebView.JsBridgeCallBack() {
            @Override
            public void onJsCall(String msg) {
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    String name = jsonObject.optString("name");
                    if (TextUtils.equals(name, "larkjs.notify.app.pop.controller")) {
                        handleJump();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void handleJump() {
        Intent mainActivity = new Intent(this, ZigBeeAddActivity.class);
        mainActivity.putExtras(getIntent());
        startActivityForResult(mainActivity, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {//网关绑定页面关闭，告知绑定成功，本引导页面自动关闭。
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        mWebView.unregisterJsBridgeCallBack();
        mWebView.unregisterLoadCallBack();
        super.onDestroy();
    }
}
