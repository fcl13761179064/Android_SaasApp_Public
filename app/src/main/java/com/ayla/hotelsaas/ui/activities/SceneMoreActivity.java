package com.ayla.hotelsaas.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean;
import com.ayla.hotelsaas.constant.KEYS;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.common_dialog.SceneIconSelectDialog;
import com.ayla.hotelsaas.widget.common_dialog.SceneRuleMoreRenameDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class SceneMoreActivity extends BasicActivity {
    @BindView(R.id.save_location)
    TextView save_location;
    @BindView(R.id.ll_rename)
    LinearLayout ll_rename;
    @BindView(R.id.tv_scene_name)
    TextView tv_scene_name;

    @BindView(R.id.ll_icon_area)
    LinearLayout ll_icon_area;
    @BindView(R.id.mIconImageView)
    ImageView mIconImageView;
    @BindView(R.id.scene_more_appbar)
    AppBar sceneMoreAppbar;

    private BaseSceneBean mRuleEngineBean;
    private SceneIconSelectDialog sceneIconSelectDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_more;
    }

    @Override
    protected View getLayoutView() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRuleEngineBean = (BaseSceneBean) getIntent().getSerializableExtra(KEYS.SCENE_BASERESULT);
        //通过icon的path获取下标数字
        int iconImg = getIconResByIndex(getIconIndexByPath(mRuleEngineBean.getIconPath()));
        mIconImageView.setImageResource(iconImg);
        tv_scene_name.setText(mRuleEngineBean.getRuleName());
        int location = getIntent().getIntExtra(KEYS.SCENELOCATION, 0);
        if (location == 1)
            save_location.setText("本地");
        else
            save_location.setText("云端");
        sceneIconSelectDialog = new SceneIconSelectDialog(this);
        sceneIconSelectDialog.setOnSelectSceneIconListener(index -> {
            mRuleEngineBean.setIconPath(getIconPathByIndex(index));
            mIconImageView.setImageResource(getIconResByIndex(index));
        });
        if (sceneMoreAppbar != null) {
            sceneMoreAppbar.rightTextView.setOnClickListener(v -> {
                Intent intents = new Intent();
                intents.putExtra("iconPath", mRuleEngineBean.getIconPath());
                intents.putExtra("sceneName", mRuleEngineBean.getRuleName());
                setResult(10012, intents);
                finish();
            });
        }
    }

    @Override
    protected void initView() {

    }


    @OnClick(R.id.ll_rename)
    public void sceneNameClicked() {
        String currentSceneName = tv_scene_name.getText().toString();
        SceneRuleMoreRenameDialog.newInstance(new SceneRuleMoreRenameDialog.DoneCallback() {
            @Override
            public void onDone(DialogFragment dialog, String txt) {
                if (TextUtils.isEmpty(txt) || txt.trim().isEmpty()) {
                    CustomToast.makeText(getBaseContext(), "名称不能为空", R.drawable.ic_toast_warning);
                    return;
                }
                tv_scene_name.setText(txt);
                mRuleEngineBean.setRuleName(txt);
                dialog.dismissAllowingStateLoss();
            }

            @Override
            public void onCancel(DialogFragment dialog) {

            }
        })
                .setTitle("修改名称")
                .setEditHint("请输入场景名称")
                .setEditValue(currentSceneName)
                .setMaxLength(20)
                .show(getSupportFragmentManager(), "scene_name");
    }

    @OnClick(R.id.ll_icon_area)
    public void jumpIconSelect() {
        //通过icon的path获取下标数字
        int iconImg = getIconIndexByPath(mRuleEngineBean.getIconPath());

        sceneIconSelectDialog.show();
        sceneIconSelectDialog.initSelect(iconImg - 1);
    }


    /**
     * 通过图片url获取icon下标
     *
     * @param path
     * @return
     */
    private int getIconIndexByPath(String path) {
        int i = 1;
        if (null == path)
            return i;
        String indexString = path.replace("http://cdn-smht.ayla.com.cn/minip/assets/public/scene/", "").replace(".png", "");
        try {
            i = Integer.parseInt(indexString);
        } catch (Exception ignore) {
        }
        return i;
    }

    /**
     * 通过icon下标后去图片
     *
     * @param index
     * @return
     */
    private int getIconResByIndex(int index) {
        return getResources().getIdentifier(String.format("ic_scene_%s", index), "drawable", getBaseContext().getPackageName());
    }


    @Override
    protected void initListener() {

    }


    /**
     * 通过icon下标获得图片url
     *
     * @param i
     * @return
     */
    private String getIconPathByIndex(int i) {
        return String.format("http://cdn-smht.ayla.com.cn/minip/assets/public/scene/%s.png", i);
    }


}
