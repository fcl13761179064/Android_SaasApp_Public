package com.ayla.hotelsaas.ui.fragment.wifi_test;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.widget.MaskProgress;
import com.ayla.hotelsaas.utils.WifiUtils;
import com.blankj.utilcode.util.NetworkUtils;

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
        if (WifiUtils.getInstance(getActivity()).mIsopenWifi() && NetworkUtils.isWifiConnected()) {//??????????????????wifi
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
            start.setText("????????????");
            if (tv_error_btn.getVisibility() == View.VISIBLE) {
                tv_error_btn.setText("????????????");
            } else {
                tv_error_btn.setText("????????????");
            }
            tv_net_num.setTextColor(getResources().getColor(R.color.color_333333));
            tv_net_text.setText("?????????");
            tv_net_text.setTextColor(getResources().getColor(R.color.common_green));
            //???????????????
            maskProgress.setMax(360);
            //???????????????progress????????????????????????,????????????
            maskProgress.setTotaltime(8);
            //Progress????????????????????????360???0???????????????90????????????180???????????????270????????????(?????????????????????)
            maskProgress.setStartAngle(137);
            maskProgress.setAnimateListener(animateListener);
            //?????????????????????setMax?????????????????????setProgress
            int level = WifiUtils.getInstance(getContext()).getCurrentWifiInfoLevel();
            if (level < -70) {
                type = "??????";
                int x = 263; // ?????????
                int y = 265; // ??????
                float rn = new Random().nextInt(y - x + 1) + x;
                tv_net_text.setText("????????????");
                tv_net_text.setTextColor(getResources().getColor(R.color.login_error_show));
                maskProgress.setProgress(rn);
                rotateAnim(320 - 115, 7500l);
            } else if (level <= -50 && level > -70) {
                type = "??????";
                tv_net_text.setText("????????????");
                int x = 175; // ?????????
                int y = 180; // ??????
                float rn = new Random().nextInt(y - x + 1) + x;
                maskProgress.setProgress(rn);
                tv_net_text.setTextColor(getResources().getColor(R.color.color_yellow));
                rotateAnim(rn - 58, 5700l);
            } else {
                type = "??????";
                tv_net_text.setText("????????????");
                int x = 102; // ?????????
                int y = 106; // ??????
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
                        if ("????????????".equals(start.getText()) || "????????????".equals(tv_error_btn.getText())) {
                            tv_error_btn.setText("????????????");
                            start.setText("????????????");
                            if ("??????".equals(type)) {
                                tv_wifi_test_status.setText("????????????????????????\n" +
                                        "?????????????????????????????????????????????");
                            } else if ("??????".equals(type)) {
                                tv_wifi_test_status.setText("????????????????????????\n" +
                                        "?????????????????????????????????????????????");
                            } else {
                                tv_wifi_test_status.setText("????????????????????????\n" +
                                        "????????????????????????????????????");
                            }
                            tv_wifi_status_img.setVisibility(View.VISIBLE);
                            tv_wifi_test_status.setVisibility(View.VISIBLE);
                            view.setVisibility(View.GONE);
                            Message msg = new Message();
                            msg.what = 0;
                            handler.sendMessage(msg);
                            tv_error_btn.setText("????????????");
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
                                if ("??????".equals(type)) {
                                    int current_Progress = (int) currentProgress;
                                    tv_net_num.setText("-" + current_Progress / 2);
                                    if (current_Progress == 80) {
                                        iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_info_weak));
                                    }
                                } else if ("??????".equals(type)) {
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

                                        }, 50);//3????????????Runnable??????run??????

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

                                        }, 200);//3????????????Runnable??????run??????
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

                                        }, 100);//3????????????Runnable??????run??????

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

                                        }, 200);//3????????????Runnable??????run??????

                                    } else if (current_Progress > 15) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                if (getActivity() == null) {
                                                    return;
                                                }
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_info_grood));
                                                    }
                                                });

                                            }

                                        }, 300);//3????????????Runnable??????run??????

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
        anim.setFillAfter(true); // ?????????????????????????????????
        anim.setDuration(duation); // ??????????????????
        anim.setInterpolator(new LinearInterpolator()); // ???????????????
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
                if ("????????????".equals(tv_error_btn.getText())) {
                    tv_error_btn.setText("????????????");
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
        if (!WifiUtils.getInstance(getActivity()).mIsopenWifi() || !NetworkUtils.isWifiConnected()) {//??????????????????wifi
            CustomToast.makeText(getActivity(), "????????? Wi-Fi ?????????????????????", R.drawable.ic_toast_warning);
            NetErrorMethod();
            return;
        } else {
            if (start.getText().equals("????????????")) {
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
        tv_net_text.setText("???????????????");
        start.setText("????????????");
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
                tv_wifi_test_status.setText("???????????????????????????");
                iv_star.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_progress_gray_five_star));
            }

        }, 300);//3????????????Runnable??????run??????
    }

    public void NetErrorMethod() {
        ll_bottom_page.setVisibility(View.GONE);
        tv_error_btn.setVisibility(View.VISIBLE);
        tv_net_text.setText("????????????");
        start.setText("????????????");
        tv_error_btn.setText("????????????");
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

        }, 300);//3????????????Runnable??????run??????
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
