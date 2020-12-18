package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.mvp.present.RuleEngineActionTypeGuidePresenter;
import com.ayla.hotelsaas.mvp.view.RuleEngineActionTypeGuideView;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;

import butterknife.OnClick;

/**
 * 场景设备，选择动作类型
 * 进入必须带上 {@link long scopeId} 房间ID
 * {@link BaseSceneBean data}
 * {@link Boolean hasWelcomeAction} 是否本身就包含酒店欢迎语动作
 * <p>
 * 返回：
 * type 0：设备动作 1：延时动作 2：欢迎语动作
 */
public class RuleEngineActionTypeGuideActivity extends BaseMvpActivity<RuleEngineActionTypeGuideView, RuleEngineActionTypeGuidePresenter> implements RuleEngineActionTypeGuideView {
    @Override
    protected RuleEngineActionTypeGuidePresenter initPresenter() {
        return new RuleEngineActionTypeGuidePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ruleengine_action_type_guide;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {

    }

    private BaseSceneBean mRuleEngineBean;
    private long scopeId;
    private boolean hasWelcomeAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRuleEngineBean = (BaseSceneBean) getIntent().getSerializableExtra("data");
        scopeId = getIntent().getLongExtra("scopeId", 0);
        hasWelcomeAction = getIntent().getBooleanExtra("hasWelcomeAction", false);
    }

    @OnClick(R.id.rl_device_changed)
    public void jumpDeviceSelect() {
        setResult(RESULT_OK, new Intent().putExtra("type", 0));
        finish();
    }

    @OnClick(R.id.rl_type_delay)
    public void handleDelaySelect() {
        if (mRuleEngineBean.getActions().size() != 0) {
            BaseSceneBean.Action lastAction = mRuleEngineBean.getActions().get(mRuleEngineBean.getActions().size() - 1);
            if (lastAction instanceof BaseSceneBean.DelayAction) {
                CustomAlarmDialog.newInstance()
                        .setDoneCallback(new CustomAlarmDialog.Callback() {
                            @Override
                            public void onDone(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                            }

                            @Override
                            public void onCancel(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                            }
                        }).setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON).setTitle("提示")
                        .setContent("不能添加延时动作").show(getSupportFragmentManager(), "tip");
                return;
            }
        }

        setResult(RESULT_OK, new Intent().putExtra("type", 1));
        finish();
    }

    @OnClick(R.id.rl_type_welcome)
    public void handleWelcomeSelect() {
        mPresenter.check(scopeId);
    }


    @Override
    public void checkResult(int result) {
        //如果是编辑一个包含欢迎语动作的联动，就需要忽略这个错误
        boolean currentExitWelcomeAction = false;
        for (BaseSceneBean.Action action : mRuleEngineBean.getActions()) {
            if (action instanceof BaseSceneBean.WelcomeAction) {
                currentExitWelcomeAction = true;
                break;
            }
        }

        if (result == 0) {
            if (!currentExitWelcomeAction) {
                setResult(RESULT_OK, new Intent().putExtra("type", 2));
                finish();
                return;
            }
        } else if (result == -1) {//没有音响
            CustomAlarmDialog.newInstance()
                    .setDoneCallback(new CustomAlarmDialog.Callback() {
                        @Override
                        public void onDone(CustomAlarmDialog dialog) {
                            dialog.dismissAllowingStateLoss();
                        }

                        @Override
                        public void onCancel(CustomAlarmDialog dialog) {
                            dialog.dismissAllowingStateLoss();
                        }
                    }).setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON).setTitle("提示")
                    .setContent("未检测到该房间下有可设置的音箱，请确认是否已完成关联").show(getSupportFragmentManager(), "tip");
            return;
        } else if (result == -2) {//已经设置了欢迎语的联动
            if (hasWelcomeAction && !currentExitWelcomeAction) {
                setResult(RESULT_OK, new Intent().putExtra("type", 2));
                finish();
                return;
            }
        }
        CustomAlarmDialog.newInstance()
                .setDoneCallback(new CustomAlarmDialog.Callback() {
                    @Override
                    public void onDone(CustomAlarmDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                    }

                    @Override
                    public void onCancel(CustomAlarmDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                    }
                }).setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON).setTitle("提示")
                .setContent("当前房间已设置过酒店欢迎语，不可重复设置").show(getSupportFragmentManager(), "tip");
    }

    @Override
    public void checkFailed(Throwable throwable) {
    }
}
