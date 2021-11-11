package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.mvp.present.RuleEngineActionTypeGuidePresenter;
import com.ayla.hotelsaas.mvp.view.RuleEngineActionTypeGuideView;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 场景设备，选择动作类型
 * 进入必须带上 {@link long scopeId} 房间ID
 * {@link BaseSceneBean data}
 * <p>
 * 返回：
 * type 0：设备动作 1：延时动作 2：欢迎语动作
 */
public class RuleEngineActionTypeGuideActivity extends BaseMvpActivity<RuleEngineActionTypeGuideView, RuleEngineActionTypeGuidePresenter> implements RuleEngineActionTypeGuideView {


    @BindView(R.id.rl_rule_scene)
    RelativeLayout rl_rule_scene;

    private BaseSceneBean mRuleEngineBean;
    private long scopeId;
    private boolean hasWelcomeAction;

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
        int siteType = getIntent().getIntExtra("siteType", 0);
        mRuleEngineBean = (BaseSceneBean) getIntent().getSerializableExtra("data");
        scopeId = getIntent().getLongExtra("scopeId", 0);
        for (BaseSceneBean.Action action : mRuleEngineBean.getActions()) {
            if (action instanceof BaseSceneBean.WelcomeAction) {
                hasWelcomeAction = true;
                break;
            }
        }

        View welcomeView = findViewById(R.id.rl_type_welcome);
        if (mRuleEngineBean.getSiteType() == BaseSceneBean.SITE_TYPE.REMOTE) {//只有云端场景才可以设置酒店欢迎语动作
            welcomeView.setVisibility(View.VISIBLE);
        } else {
            welcomeView.setVisibility(View.GONE);
        }

        if (siteType==BaseSceneBean.SITE_TYPE.LOCAL || mRuleEngineBean.getRuleType() == BaseSceneBean.RULE_TYPE.ONE_KEY){
            rl_rule_scene.setVisibility(View.GONE);
        }else {
            rl_rule_scene.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.rl_device_changed})
    public void jumpDeviceSelect() {
        setResult(RESULT_OK, new Intent().putExtra("type", 0));
        finish();
    }

    @OnClick({R.id.rl_rule_scene})
    public void jumpOneKeyRulePage() {
     Intent intent = new Intent(this, OnekeylinkageListActivity.class);
        startActivity(intent);

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
        showError(throwable);
    }
}
