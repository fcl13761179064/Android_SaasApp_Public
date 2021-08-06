package com.ayla.hotelsaas.ui;

import android.view.View;
import android.webkit.JavascriptInterface;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.widget.AppBar;
import com.tencent.smtt.sdk.WebView;

import butterknife.BindView;
import butterknife.OnClick;
import wendu.dsbridge.DWebView;

/**
 * H5页面
 * pageTitle 标题
 * url 页面地址
 */
public class H5BaseActivity extends BaseWebViewActivity {

    @BindView(R.id.web_view)
    DWebView mWebView;

    @BindView(R.id.empty_layout)
    View emptyView;
    @BindView(R.id.appBar)
    AppBar mAppBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_help_center;
    }

    @Override
    protected void initView() {
        WebView.setWebContentsDebuggingEnabled(true);
        mAppBar.setCenterText(getIntent().getStringExtra("pageTitle"));
        mWebView.loadUrl(getIntent().getStringExtra("url"));

    }

    @Override
    protected void initListener() {



    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected View getEmptyView() {
        return emptyView;
    }

    @Override
    protected DWebView getWebView() {
        return mWebView;
    }

    @OnClick(R.id.bt_refresh)
    @Override
    public void handleRefreshClick() {
        super.handleRefreshClick();
    }


}


