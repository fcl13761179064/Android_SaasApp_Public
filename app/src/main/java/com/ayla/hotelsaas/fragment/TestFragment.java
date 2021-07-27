package com.ayla.hotelsaas.fragment;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.utils.WifiUtil;
import com.ayla.hotelsaas.widget.MaskProgress;
import com.ayla.hotelsaas.widget.WifiUtils;

import butterknife.BindView;


public class TestFragment extends BaseMvpFragment {
    @BindView(R.id.maskView)
    MaskProgress maskView;
    @BindView(R.id.start)
    Button start;
    @BindView(R.id.relation_status)
    ImageView relation_status;

    @BindView(R.id.tv_net_num)
    TextView tv_net_num;

    @BindView(R.id.iv_star)
    ImageView iv_star;

    @BindView(R.id.tv_net_text)
    TextView tv_net_text;

    @Override
    protected int getLayoutId() {
        return R.layout.test__wifi_fragment;
    }

    @Override
    protected void initView(View view) {
        if (!WifiUtils.getInstance(getContext()).mIsopenWifi()) {//如果没有打开wifi
            //progress背景图
            tv_net_text.setText("网络错误");
            tv_net_text.setTextColor(getResources().getColor(R.color.login_error_show));
            maskView.setProgress(0);
            maskView.updateProgress();
        }
    }

    private void initialProgress(MaskProgress maskProgress) {
        tv_net_text.setText("测试中");
        tv_net_text.setTextColor(getResources().getColor(R.color.common_green));
        //设置最大值
        maskProgress.setMax(360);
        //初始化填充progress时的填充动画时间,越大越慢
        maskProgress.setTotaltime(10);
        //Progress开始的填充的位置360和0为圆最右、90圆最下、180为圆最右、270为圆最上(顺时针方向为正)
        maskProgress.setStartAngle(110);
        maskProgress.setAnimateListener(animateListener);
        //初始化时必须在setMax设置之后再设置setProgress
        int level = WifiUtils.getInstance(getContext()).getCurrentWifiInfoLevel();
        if (level < -70) {
            tv_net_text.setText("网络极差");
            maskProgress.setProgress(level);
        } else if (level < -50 && level > -70) {
            tv_net_text.setText("网络极差");
            maskProgress.setProgress(level);
        } else {
            tv_net_text.setText("网络极好");
            maskProgress.setProgress(360);
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (maskView == null) {
                return;
            }
            float newProgress =  maskView.getProgress();
            maskView.setProgress(newProgress);
            maskView.updateProgress();
        }
    };

    MaskProgress.AnimateListener animateListener = new MaskProgress.AnimateListener() {

        @Override
        public void onAnimateFinish() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            start.setEnabled(true);
                        }
                    });
                }
            });
            handler.sendEmptyMessageDelayed(0, 5000);
        }

        @Override
        public void NetError() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    maskView.setProgress(0);
                    tv_net_text.setText("网络错误");
                    tv_net_text.setTextColor(getResources().getColor(R.color.login_error_show));
                    maskView.setProgress(0);
                    maskView.updateProgress();
                }
            });

        }
    };


    @Override
    protected void initListener() {
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!WifiUtils.getInstance(getContext()).mIsopenWifi()) {//如果没有打开wifi
                    tv_net_text.setText("网络错误");
                    tv_net_text.setTextColor(getResources().getColor(R.color.login_error_show));
                    maskView.setProgress(0);
                    maskView.updateProgress();
                } else {
                    start.setEnabled(false);
                    initialProgress(maskView);
                    maskView.initial();
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }


}
