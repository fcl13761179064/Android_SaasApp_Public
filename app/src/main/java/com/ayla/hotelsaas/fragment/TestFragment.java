package com.ayla.hotelsaas.fragment;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.widget.MaskProgress;
import com.ayla.hotelsaas.widget.WifiUtils;

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
    @BindView(R.id.wifi_arrow)
    ImageView wifi_arrow;
    /*    @BindView(R.id.point_weizhi)
        ImageView point_weizhi;
        @BindView(R.id.point_weizhi_two)
        ImageView point_weizhi_two;
        @BindView(R.id.point_weizhi_three)
        ImageView point_weizhi_three;*/
    private String type;

    @Override
    protected int getLayoutId() {
        return R.layout.test__wifi_fragment;
    }

    @Override
    protected void initView(View view) {
        if (!WifiUtils.getInstance(getContext()).mIsopenWifi()) {//如果没有打开wifi
            //progress背景图
            tv_net_text.setText("网络错误");
            start.setText("重新检测");
            relation_status.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_no_relation_text));
            tv_net_text.setTextColor(getResources().getColor(R.color.login_error_show));
            maskView.setProgress(0);
            tv_net_num.setText(0 + "");
            tv_dbm.setVisibility(View.GONE);
            tv_net_num.setTextColor(getResources().getColor(R.color.color_gray));
            maskView.updateProgress();
        }
    }

    private void initialProgress(MaskProgress maskProgress) {
        try {
            wifi_arrow.setVisibility(View.GONE);
            tv_dbm.setVisibility(View.VISIBLE);
            relation_status.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_yes_relation_test));
            start.setText("取消检测");
            tv_net_num.setTextColor(getResources().getColor(R.color.color_333333));
            tv_net_text.setText("测试中");
            tv_net_text.setTextColor(getResources().getColor(R.color.common_green));
            //设置最大值
            maskProgress.setMax(360);
            //初始化填充progress时的填充动画时间,越大越慢
            maskProgress.setTotaltime(10);
            //Progress开始的填充的位置360和0为圆最右、90圆最下、180为圆最右、270为圆最上(顺时针方向为正)
            maskProgress.setStartAngle(125);
            maskProgress.setAnimateListener(animateListener);
            //初始化时必须在setMax设置之后再设置setProgress
            int level = WifiUtils.getInstance(getContext()).getCurrentWifiInfoLevel();
            if (level < -70) {
                type = "很差";
                int x = 310; // 下界。
                int y = 324; // 上界
                float rn = new Random().nextInt(y - x + 1) + x;
                tv_net_text.setText("网络极差");
                tv_net_text.setTextColor(getResources().getColor(R.color.login_error_show));
                maskProgress.setProgress(rn);
                rotateAnim(320-110,7000l);
            } else if (level <= -50 && level > -70) {
                type = "一般";
                tv_net_text.setText("网络一般");
                int x = 160; // 下界。
                int y = 220; // 上界
                float rn = new Random().nextInt(y - x + 1) + x;
                maskProgress.setProgress(rn);
                tv_net_text.setTextColor(getResources().getColor(R.color.color_yellow));
                rotateAnim(rn - 60, 5000l);
            } else {
                type = "极好";
                tv_net_text.setText("网络极好");
                int x = 60; // 下界。
                int y = 140; // 上界
                float rn = new Random().nextInt(y - x + 1) + x;
                maskProgress.setProgress(rn);
                rotateAnim(rn - 60, 3000l);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tv_net_text.setText("网络错误");
            start.setText("重新检测");
            tv_dbm.setVisibility(View.GONE);
            relation_status.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_no_relation_text));
            tv_net_text.setTextColor(getResources().getColor(R.color.login_error_show));
            maskView.setProgress(0);
            tv_net_num.setText(0 + "");
            tv_dbm.setVisibility(View.GONE);

            tv_net_num.setTextColor(getResources().getColor(R.color.color_gray));
            maskView.updateProgress();
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
        }
    };

    MaskProgress.AnimateListener animateListener = new MaskProgress.AnimateListener() {

        @Override
        public void onAnimateFinish() {
            start.setText("重新检测");
            handler.sendEmptyMessageDelayed(0, 100);
        }

        @Override
        public void NetError() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_net_text.setText("网络错误");
                    start.setText("重新检测");
                    relation_status.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_no_relation_text));
                    tv_net_text.setTextColor(getResources().getColor(R.color.login_error_show));
                    maskView.setProgress(0);
                    tv_net_num.setText(0 + "");
                    tv_dbm.setVisibility(View.GONE);
                    wifi_arrow.setVisibility(View.GONE);
                    tv_net_num.setTextColor(getResources().getColor(R.color.color_gray));
                    maskView.updateProgress();
                    handler.postDelayed(new Runnable() {

                        @Override

                        public void run() {
                            iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_progress_gray_five_star));
                        }

                    }, 200);//3秒后执行Runnable中的run方法
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
                                    tv_net_num.setText("-" + (int) (current_Progress / 2.5));
                                    if (current_Progress == 80) {
                                        iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_info_weak));
                                        wifi_arrow.setVisibility(View.VISIBLE);
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
                                                        wifi_arrow.setVisibility(View.VISIBLE);
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
                                                        wifi_arrow.setVisibility(View.VISIBLE);

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
        Animation anim = new RotateAnimation(-27f, toDegress, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true); // 设置保持动画最后的状态
        anim.setDuration(duation); // 设置动画时间
        anim.setInterpolator(new AccelerateInterpolator()); // 设置插入器
        wifi_arrow.startAnimation(anim);
    }

    @Override
    protected void initListener() {
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!WifiUtils.getInstance(getContext()).mIsopenWifi()) {//如果没有打开wifi
                    tv_net_text.setText("网络错误");
                    start.setText("重新检测");
                    relation_status.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_no_relation_text));
                    tv_net_text.setTextColor(getResources().getColor(R.color.login_error_show));
                    maskView.setProgress(0);
                    tv_net_num.setText(0 + "");
                    tv_dbm.setVisibility(View.GONE);
                    tv_net_num.setTextColor(getResources().getColor(R.color.color_gray));
                    maskView.updateProgress();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override

                        public void run() {
                            wifi_arrow.setVisibility(View.GONE);
                            iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_progress_gray_five_star));
                        }

                    }, 80);//3秒后执行Runnable中的run方法

                } else {
                    if (start.getText().equals("取消检测")) {
                        tv_net_text.setText("未检测网络");
                        start.setText("重新检测");
                        relation_status.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_no_relation_text));
                        tv_net_text.setTextColor(getResources().getColor(R.color.color_gray));
                        tv_dbm.setVisibility(View.GONE);
                        tv_net_num.setTextColor(getResources().getColor(R.color.color_gray));
                        maskView.setProgress(0);
                        maskView.updateProgress();
                        try {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {

                                @Override

                                public void run() {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            wifi_arrow.setVisibility(View.GONE);
                                            iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_progress_gray_five_star));
                                            tv_net_num.setText("0");
                                        }
                                    });

                                }

                            }, 350);//3秒后执行Runnable中的run方法

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        initialProgress(maskView);
                        maskView.initial();
                    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
