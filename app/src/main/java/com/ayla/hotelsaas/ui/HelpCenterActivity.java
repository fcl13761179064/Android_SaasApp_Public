package com.ayla.hotelsaas.ui;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.webview.LarkWebView;
import com.ayla.hotelsaas.widget.AppBar;

import butterknife.BindView;

public class HelpCenterActivity extends BasicActivity implements LarkWebView.LoadCallBack {

    @BindView(R.id.web_view)
    LarkWebView mWebView;

    @BindView(R.id.appBar)
    AppBar mAppBar;

    @Override
    public void refreshUI() {
        super.refreshUI();
        mAppBar.setCenterText("帮助中心");
        mWebView.registerLoadCallBack(this);
        mWebView.loadUrl("https://www.baidu.com");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_help_center;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        showProgress();

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        hideProgress();
    }

    @Override
    public void onPageError(WebView view, int errorCode, String description, String failingUrl) {
        final View emptyLayout = LayoutInflater.from(this).inflate(R.layout.widget_empty_view, null);
        TextView empty_back_btn = (TextView) emptyLayout.findViewById(R.id.empty_back_btn);
        setContentView(emptyLayout);

        empty_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}


