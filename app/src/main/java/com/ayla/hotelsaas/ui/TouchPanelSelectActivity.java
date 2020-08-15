package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.TouchPanelSelectAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.TouchPanelBean;
import com.ayla.hotelsaas.bean.TouchPanelDataBean;
import com.ayla.hotelsaas.mvp.present.TourchPanelSelectPresenter;
import com.ayla.hotelsaas.mvp.view.TourchPanelSelectView;
import com.ayla.hotelsaas.utils.ToastUtils;
import com.ayla.hotelsaas.widget.AppBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TouchPanelSelectActivity extends BaseMvpActivity<TourchPanelSelectView, TourchPanelSelectPresenter> implements TourchPanelSelectView {
    @BindView(R.id.gridView)
    GridView mGridView;

    @BindView(R.id.appBar)
    AppBar appBar;
    private List<TouchPanelBean> dataList;
    private TouchPanelSelectAdapter mAdapter;
    private DeviceListBean.DevicesBean mDevicesBean;
    public static int[] drawableIcon = new int[]{R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five, R.drawable.six, R.drawable.seven, R.drawable.eight, R.drawable.nine, R.drawable.ten, R.drawable.eleven, R.drawable.tween};
    private String mPannel_types;

    @Override
    public void refreshUI() {
        appBar.setCenterText("场景按键设置");
        super.refreshUI();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.touch_panel_select_layout;
    }


    @Override
    protected void initView() {

        mDevicesBean = (DeviceListBean.DevicesBean) getIntent().getSerializableExtra("devicesBean");
        mPresenter.getTouchPanelData(1, mDevicesBean.getDeviceId());
        mPannel_types = getIntent().getStringExtra("pannel_type");
        if ("1".equals(mPannel_types)) {
            dataList = new ArrayList<>();
            dataList.add(new TouchPanelBean(R.mipmap.go_home, "回家", 0, 1));
            dataList.add(new TouchPanelBean(R.mipmap.back_home, "离店模式", 0, 2));
            dataList.add(new TouchPanelBean(R.mipmap.sleep_model, "睡眠模式", 0, 3));
            dataList.add(new TouchPanelBean(R.mipmap.storm_model, "影音", 0, 4));
            dataList.add(new TouchPanelBean(R.mipmap.all_open, "全开", 0, 5));
            dataList.add(new TouchPanelBean(R.mipmap.all_off, "全关", 0, 6));
            dataList.add(new TouchPanelBean(R.mipmap.opoen_curtain, "开窗帘", 0, 7));
            dataList.add(new TouchPanelBean(R.mipmap.close_curtain, "关窗帘", 0, 8));
            dataList.add(new TouchPanelBean(R.mipmap.pause, "暂停", 0, 9));
            dataList.add(new TouchPanelBean(R.mipmap.read_model, "阅读", 0, 10));
            dataList.add(new TouchPanelBean(R.mipmap.sleep_model, "睡眠", 0, 11));
            dataList.add(new TouchPanelBean(R.mipmap.getup_model, "起床", 0, 12));
        } else {

            dataList = new ArrayList<>();
            dataList.add(new TouchPanelBean(R.mipmap.go_home, "回家", 0, 1));
            dataList.add(new TouchPanelBean(R.mipmap.back_home, "会客", 0, 2));
            dataList.add(new TouchPanelBean(R.mipmap.all_open, "全开", 0, 3));
            dataList.add(new TouchPanelBean(R.mipmap.back_home, "离家", 0, 4));
            dataList.add(new TouchPanelBean(R.mipmap.storm_model, "影音", 0, 5));
            dataList.add(new TouchPanelBean(R.mipmap.all_off, "全关", 0, 6));
        }


    }

    @Override
    protected void initListener() {
        appBar.rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TouchPanelSelectActivity.this, DeviceMoreActivity.class);
                intent.putExtras(getIntent());
                startActivity(intent);
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ("2".equals(mPannel_types)) {
                    ToastUtils.showShortToast("该功能暂时不可用");
                    return;
                }
                Intent intent = new Intent(MyApplication.getContext(), TouchPanelSettingActivity.class);
                final int touchpanel_num = position + 1;
                intent.putExtra("position", position);
                intent.putExtra("btn_position", touchpanel_num + "");
                intent.putExtra("touchpanel_data", (Serializable) dataList);
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
            final String deviceId = touchPanelDataBean.getDeviceId();
            final int id = touchPanelDataBean.getId();
            if ("J9WX4aPBnZlxtipuQqwC000000".equals(deviceId)) {
                for (int x = 0; x < dataList.size(); x++) {
                    int btn_position = dataList.get(x).getBtn_position();
                    final String String_btn_position = String.valueOf(btn_position);
                    if (String_btn_position.equals(propertyName)) {
                        if ("Words".equals(propertyType)) {
                            dataList.get(x).setPropertyValue(propertyValue);
                            dataList.get(x).setId(id);
                        } else {
                            //  dataList.get(x).setIconRes(dataBeans[x]);
                            dataList.get(x).setId(id);

                        }
                    }
                }
            } else {
                for (int x = 0; x < dataList.size(); x++) {
                    int btn_position = dataList.get(x).getBtn_position();
                    final String String_btn_position = String.valueOf(btn_position);
                    if (String_btn_position.equals(propertyName)) {
                        if ("Words".equals(propertyType)) {
                            dataList.get(x).setPropertyValue(propertyValue);
                            dataList.get(x).setId(id);
                        } else {
                            //  dataList.get(x).setIconRes(dataBeans[x]);
                            dataList.get(x).setId(id);

                        }
                    }
                }
            }

        }
        mAdapter = new TouchPanelSelectAdapter(dataList);
        mGridView.setAdapter(mAdapter);

    }

    @Override
    protected TourchPanelSelectPresenter initPresenter() {
        return new TourchPanelSelectPresenter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            mPresenter.getTouchPanelData(1, mDevicesBean.getDeviceId());
        }
    }
}
