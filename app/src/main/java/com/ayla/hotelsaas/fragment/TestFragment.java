package com.ayla.hotelsaas.fragment;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
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
    float progressFromCode = 150;
    float progressFromXml = 150;

    private boolean isAnimateFinish = true;

    @Override
    protected int getLayoutId() {
        return R.layout.test__wifi_fragment;
    }

    @Override
    protected void initView(View view) {
        if (!WifiUtils.getInstance(getContext()).mIsopenWifi()) {//如果没有打开wifi
            //progress背景图
            maskView.setBackgroundResId(R.mipmap.wifi_network_error);
        }
    }

    private void initialProgress(MaskProgress maskProgress) {
        //设置最大值
        maskProgress.setMax(360);
        //初始填充量为一半
        //初始化填充progress时的填充动画时间,越大越慢
        maskProgress.setTotaltime(10);
        //Progress开始的填充的位置360和0为圆最右、90圆最下、180为圆最右、270为圆最上(顺时针方向为正)
        maskProgress.setStartAngle(130);
        maskProgress.setAnimateListener(animateListener);
        //初始化时必须在setMax设置之后再设置setProgress
        maskProgress.setProgress(130);
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (maskView == null) {
                return;
            }
            if (!WifiUtils.getInstance(getContext()).mIsopenWifi()) {//如果没有打开wifi
                //progress背景图
                maskView.setBackgroundResId(R.mipmap.wifi_network_error);
                return;
            }
            float newProgress =  maskView.getProgress();
            if (newProgress <= 0) {//随机绘制效果

                float max = (float) (Math.random() * 900 + 1000);
                float progress = (float) (max * Math.random());

                maskView.setMax(max);
                maskView.setProgress(progress);
                maskView.setTotaltime((float) (Math.random() * 10));
                maskView.setStartAngle((float) (Math.random() * 360));
                maskView.initial();
                return;
            }
            maskView.setProgress(newProgress);
            maskView.updateProgress();
        }
    };

    MaskProgress.AnimateListener animateListener = new MaskProgress.AnimateListener() {

        @Override
        public void onAnimateFinish() {
            handler.sendEmptyMessageDelayed(0, 5000);
        }
    };


    @Override
    protected void initListener() {
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!WifiUtils.getInstance(getContext()).mIsopenWifi()) {//如果没有打开wifi
                    //progress背景图
                    maskView.setBackgroundResId(R.mipmap.wifi_network_error);
                } else {
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
