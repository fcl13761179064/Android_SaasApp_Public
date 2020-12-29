package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.TouchPanelSelectAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.TouchPanelBean;
import com.ayla.hotelsaas.bean.TouchPanelDataBean;
import com.ayla.hotelsaas.fragment.DeviceListContainerFragment;
import com.ayla.hotelsaas.mvp.present.TourchPanelSelectPresenter;
import com.ayla.hotelsaas.mvp.view.TourchPanelSelectView;
import com.ayla.hotelsaas.widget.AppBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 触控面板、六键场景开关 的场景按键选择页面
 * 进入时带入参数"pannel_type" 1：触控面板 2：六键开关
 */
public class TouchPanelSelectActivity extends BaseMvpActivity<TourchPanelSelectView, TourchPanelSelectPresenter> implements TourchPanelSelectView {
    @BindView(R.id.gridView)
    GridView mGridView;

    @BindView(R.id.appBar)
    AppBar appBar;
    private List<TouchPanelBean> dataList;
    private TouchPanelSelectAdapter mAdapter;
    private String deviceId;

    @Override
    protected int getLayoutId() {
        return R.layout.touch_panel_select_layout;
    }


    @Override
    protected void initView() {
        appBar.setCenterText("场景按键设置");

        deviceId = getIntent().getStringExtra("deviceId");
        mPresenter.getTouchPanelData(1, deviceId);
        int pannel_type = getIntent().getIntExtra("pannel_type", 0);
        if (pannel_type == 1) {
            appBar.rightTextView.setVisibility(View.GONE);
            dataList = new ArrayList<>();
            dataList.add(new TouchPanelBean(R.mipmap.go_home, "回家", 1));
            dataList.add(new TouchPanelBean(R.mipmap.back_home, "离店模式", 2));
            dataList.add(new TouchPanelBean(R.mipmap.sleep_model, "睡眠模式", 3));
            dataList.add(new TouchPanelBean(R.mipmap.storm_model, "影音", 4));
            dataList.add(new TouchPanelBean(R.mipmap.all_open, "全开", 5));
            dataList.add(new TouchPanelBean(R.mipmap.all_off, "全关", 6));
            dataList.add(new TouchPanelBean(R.mipmap.opoen_curtain, "开窗帘", 7));
            dataList.add(new TouchPanelBean(R.mipmap.close_curtain, "关窗帘", 8));
            dataList.add(new TouchPanelBean(R.mipmap.pause, "暂停", 9));
            dataList.add(new TouchPanelBean(R.mipmap.read_model, "阅读", 10));
            dataList.add(new TouchPanelBean(R.mipmap.sleep_model, "睡眠", 11));
            dataList.add(new TouchPanelBean(R.mipmap.getup_model, "起床", 12));
        } else {
            appBar.rightTextView.setVisibility(View.VISIBLE);
            dataList = new ArrayList<>();
            dataList.add(new TouchPanelBean(R.mipmap.go_home, "回家", 1));
            dataList.add(new TouchPanelBean(R.mipmap.back_home, "会客", 2));
            dataList.add(new TouchPanelBean(R.mipmap.all_open, "全开", 3));
            dataList.add(new TouchPanelBean(R.mipmap.back_home, "离家", 4));
            dataList.add(new TouchPanelBean(R.mipmap.storm_model, "影音", 5));
            dataList.add(new TouchPanelBean(R.mipmap.all_off, "全关", 6));
        }
    }

    @Override
    protected void initListener() {
        appBar.rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TouchPanelSelectActivity.this, DeviceMoreActivity.class);
                intent.putExtras(getIntent());
                startActivityForResult(intent, 1002);
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyApplication.getContext(), TouchPanelSettingActivity.class);
                intent.putExtra("touchpanel", dataList.get(position));
                intent.putExtras(getIntent());
                startActivityForResult(intent, 1001);
            }
        });
    }


    @Override
    public void operateError(String msg) {

    }

    @Override
    public void operateSuccess(List<TouchPanelDataBean> dataBeans) {
        for (TouchPanelDataBean touchPanelDataBean : dataBeans) {
            final String propertyName = touchPanelDataBean.getPropertyName();
            final String propertyType = touchPanelDataBean.getPropertyType();
            final String propertyValue = touchPanelDataBean.getPropertyValue();
            int id = touchPanelDataBean.getId();

            if (TextUtils.equals(propertyType, "Words")) {
                try {
                    int btn_position = Integer.parseInt(propertyName) - 1;
                    TouchPanelBean touchPanelBean = dataList.get(btn_position);
                    touchPanelBean.setWordsId(id);
                    touchPanelBean.setWords(propertyValue);
                } catch (Exception ignored) {
                }
            }
            if (TextUtils.equals(propertyType, "PictureCode")) {
                try {
                    int btn_position = Integer.parseInt(propertyName) - 1;
                    TouchPanelBean touchPanelBean = dataList.get(btn_position);
                    touchPanelBean.setPictureCodeId(id);
                    touchPanelBean.setPictureCode(propertyValue);
                    touchPanelBean.setIconRes(DeviceListContainerFragment.drawableIcon[Integer.parseInt(propertyValue) - 1]);
                } catch (Exception ignored) {
                }
            }
        }

        mAdapter = new TouchPanelSelectAdapter(dataList);
        mGridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected TourchPanelSelectPresenter initPresenter() {
        return new TourchPanelSelectPresenter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            mPresenter.getTouchPanelData(1, deviceId);
        } else if (requestCode == 1002 && resultCode == RESULT_OK) {
            finish();
        }
    }
}
