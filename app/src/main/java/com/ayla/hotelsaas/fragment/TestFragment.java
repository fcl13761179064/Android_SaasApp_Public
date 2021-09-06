package com.ayla.hotelsaas.fragment;

import android.content.Context;
import android.opengl.Visibility;
import android.os.Handler;
import android.os.Message;
import android.view.VerifiedInputEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.ui.MainActivity;
import com.ayla.hotelsaas.widget.MaskProgress;
import com.ayla.hotelsaas.widget.WifiUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.Random;

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
    @BindView(R.id.tv_dbm)
    TextView tv_dbm;
    /*  @BindView(R.id.wifi_arrow)
      ImageView wifi_arrow;*/
    @BindView(R.id.tv_wifi_test_status)
    TextView tv_wifi_test_status;
    @BindView(R.id.tv_wifi_status_clear)
    TextView tv_wifi_status_clear;
    @BindView(R.id.ll_bottom_page)
    LinearLayout ll_bottom_page;
    @BindView(R.id.tv_error_btn)
    Button tv_error_btn;
    @BindView(R.id.tv_wifi_status_img)
    RelativeLayout tv_wifi_status_img;
    @BindView(R.id.view)
    View view;
    /*@BindView(R.id.point_weizhi)
        ImageView point_weizhi;
        @BindView(R.id.point_weizhi_two)
        ImageView point_weizhi_two;
        @BindView(R.id.point_weizhi_three)
        ImageView point_weizhi_three;
     */
    private String type;
    private Animation anim;

    @Override
    protected int getLayoutId() {
        return R.layout.test_wifi_fragment;
    }


    @Override
    protected void initView(View view) {
        if (WifiUtils.getInstance(getActivity()).mIsopenWifi() && NetworkUtils.isWifiConnected()) {//如果没有打开wifi
            relation_status.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_yes_relation_test));
        } else {
            relation_status.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_no_relation_text));
        }
    }

    private void initialProgress(MaskProgress maskProgress) {
        try {
            //   wifi_arrow.setVisibility(View.GONE);
            tv_dbm.setVisibility(View.VISIBLE);
            tv_wifi_status_img.setVisibility(View.GONE);
            tv_wifi_test_status.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            tv_wifi_status_clear.setVisibility(View.GONE);
            relation_status.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_yes_relation_test));
            start.setText("取消检测");
            if (tv_error_btn.getVisibility() == View.VISIBLE) {
                tv_error_btn.setText("取消检查");
            } else {
                tv_error_btn.setText("开始检查");
            }
            tv_net_num.setTextColor(getResources().getColor(R.color.color_333333));
            tv_net_text.setText("测试中");
            tv_net_text.setTextColor(getResources().getColor(R.color.common_green));
            //设置最大值
            maskProgress.setMax(360);
            //初始化填充progress时的填充动画时间,越大越慢
            maskProgress.setTotaltime(8);
            //Progress开始的填充的位置360和0为圆最右、90圆最下、180为圆最右、270为圆最上(顺时针方向为正)
            maskProgress.setStartAngle(137);
            maskProgress.setAnimateListener(animateListener);
            //初始化时必须在setMax设置之后再设置setProgress
            int level = WifiUtils.getInstance(getContext()).getCurrentWifiInfoLevel();
            if (level < -70) {
                type = "很差";
                int x = 263; // 下界。
                int y = 265; // 上界
                float rn = new Random().nextInt(y - x + 1) + x;
                tv_net_text.setText("网络极差");
                tv_net_text.setTextColor(getResources().getColor(R.color.login_error_show));
                maskProgress.setProgress(rn);
                rotateAnim(320 - 115, 7500l);
            } else if (level <= -50 && level > -70) {
                type = "一般";
                tv_net_text.setText("网络一般");
                int x = 175; // 下界。
                int y = 180; // 上界
                float rn = new Random().nextInt(y - x + 1) + x;
                maskProgress.setProgress(rn);
                tv_net_text.setTextColor(getResources().getColor(R.color.color_yellow));
                rotateAnim(rn - 58, 5700l);
            } else {
                type =  "极好";
                tv_net_text.setText("网络极好");
                int x = 102; // 下界。
                int y = 106; // 上界
                float rn = new Random().nextInt(y - x + 1) + x;
                maskProgress.setProgress(rn);
                rotateAnim(rn - 58, 3000l);
            }
        } catch (Exception e) {
            e.printStackTrace();
            NetErrorMethod();
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (maskView == null) {
                return;
            }
            float newProgress = maskView.getProgress();
            maskView.setProgress(newProgress);
            maskView.updateProgress();
            tv_wifi_status_clear.setVisibility(View.VISIBLE);
        }
    };

    MaskProgress.AnimateListener animateListener = new MaskProgress.AnimateListener() {

        @Override
        public void onAnimateFinish() {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("取消检测".equals(start.getText()) || "取消检查".equals(tv_error_btn.getText())) {
                            tv_error_btn.setText("重新检查");
                            start.setText("重新检测");
                            if ("很差".equals(type)) {
                                tv_wifi_test_status.setText("当前网络环境极差\n" +
                                        "不适合安装设备，请调整安装位置");
                            } else if ("一般".equals(type)) {
                                tv_wifi_test_status.setText("当前网络环境一般\n" +
                                        "不适合安装过多设备避免信道干扰");
                            } else {
                                tv_wifi_test_status.setText("当前网络环境良好\n" +
                                        "能顺畅执行各种复杂的场景");
                            }
                            tv_wifi_status_img.setVisibility(View.VISIBLE);
                            tv_wifi_test_status.setVisibility(View.VISIBLE);
                            view.setVisibility(View.GONE);
                            Message msg = new Message();
                            msg.what = 0;
                            handler.sendMessage(msg);
                            tv_error_btn.setText("开始检查");
                            ll_bottom_page.setVisibility(View.VISIBLE);
                            tv_error_btn.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }

        @Override
        public void NetError() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NetErrorMethod();
                    /*wifi_arrow.clearAnimation();
                    wifi_arrow.setVisibility(View.INVISIBLE);*/
                }
            });

        }

        @Override
        public void progressNum(float currentProgress) {

            try {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tv_net_num != null && type != null) {
                                if ("很差".equals(type)) {
                                    int current_Progress = (int) currentProgress;
                                    tv_net_num.setText("-" + current_Progress / 2);
                                    if (current_Progress == 80) {
                                        iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_info_weak));
                                    }
                                } else if ("一般".equals(type)) {
                                    int current_Progress = (int) currentProgress;
                                    tv_net_num.setText("-" + current_Progress / 4);
                                    if (current_Progress == 20) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {

                                            @Override

                                            public void run() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_info_weak));
                                                    }
                                                });

                                            }

                                        }, 50);//3秒后执行Runnable中的run方法

                                    } else if (current_Progress == 40) {
                                        Handler handlers = new Handler();
                                        handlers.postDelayed(new Runnable() {

                                            @Override

                                            public void run() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_info_middle_weak));
                                                    }
                                                });

                                            }

                                        }, 200);//3秒后执行Runnable中的run方法
                                    }
                                } else {
                                    int current_Progress = (int) currentProgress;
                                    tv_net_num.setText("-" + current_Progress / 4);
                                    if (current_Progress == 8) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {

                                            @Override

                                            public void run() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_info_weak));
                                                    }
                                                });

                                            }

                                        }, 100);//3秒后执行Runnable中的run方法

                                    } else if (current_Progress == 13) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {

                                            @Override

                                            public void run() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_info_middle_weak));
                                                    }
                                                });

                                            }

                                        }, 200);//3秒后执行Runnable中的run方法

                                    } else if (current_Progress > 15) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {

                                            @Override

                                            public void run() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_info_grood));
                                                    }
                                                });

                                            }

                                        }, 300);//3秒后执行Runnable中的run方法

                                    }
                                }
                            }
                        }
                    });
                }
            } catch (
                    Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void rotateAnim(float toDegress, long duation) {
        anim = new RotateAnimation(-27f, toDegress, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true); // 设置保持动画最后的状态
        anim.setDuration(duation); // 设置动画时间
        anim.setInterpolator(new LinearInterpolator()); // 设置插入器
        // wifi_arrow.startAnimation(anim);
    }

    @Override
    protected void initListener() {
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPermiss();
            }
        });
        tv_error_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("取消检查".equals(tv_error_btn.getText())) {
                    tv_error_btn.setText("重新检查");
                    clearData();
                } else {
                    setPermiss();
                }
            }
        });
        tv_wifi_status_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
                tv_wifi_status_clear.setVisibility(View.GONE);
            }
        });
    }


    public void setPermiss() {
        if (!WifiUtils.getInstance(getActivity()).mIsopenWifi() || !NetworkUtils.isWifiConnected()) {//如果没有打开wifi
            CustomToast.makeText(getActivity(), "请连接 Wi-Fi 后才能开始检测", R.drawable.ic_toast_warming);
            NetErrorMethod();
            return;
        } else {
            if (start.getText().equals("取消检测")) {
                clearData();
            } else {
                initialProgress(maskView);
                maskView.initial();
            }

        }
    }

    public void setShut() {
        if (maskView != null)
            maskView.initialing = false;
    }

    public void clearData() {
        // wifi_arrow.setVisibility(View.INVISIBLE);
        // wifi_arrow.clearAnimation();
        tv_net_text.setText("未检测网络");
        start.setText("开始检测");
        relation_status.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_yes_relation_test));
        tv_net_text.setTextColor(getResources().getColor(R.color.color_gray));
        maskView.setProgress(0);
        tv_dbm.setVisibility(View.GONE);
        tv_net_num.setTextColor(getResources().getColor(R.color.color_gray));
        maskView.updateProgress();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override

            public void run() {
                tv_net_num.setText(0 + "");
                tv_wifi_test_status.setText("当前网络未进行检测");
                iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_progress_gray_five_star));
            }

        }, 300);//3秒后执行Runnable中的run方法
    }

    public void NetErrorMethod() {
        ll_bottom_page.setVisibility(View.GONE);
        tv_error_btn.setVisibility(View.VISIBLE);
        tv_net_text.setText("网络错误");
        start.setText("重新检测");
        tv_error_btn.setText("重新检查");
        relation_status.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_no_relation_text));
        tv_net_text.setTextColor(getResources().getColor(R.color.login_error_show));
        maskView.setProgress(0);
        tv_net_num.setText(0 + "");
        tv_dbm.setVisibility(View.GONE);
        tv_net_num.setTextColor(getResources().getColor(R.color.color_gray));
        maskView.updateProgress();
        /*wifi_arrow.setVisibility(View.INVISIBLE);
        wifi_arrow.clearAnimation();*/
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override

            public void run() {
                iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_progress_gray_five_star));
            }

        }, 300);//3秒后执行Runnable中的run方法
    }

    @Override
    protected void initData() {
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
