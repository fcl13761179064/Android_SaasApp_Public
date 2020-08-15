package com.ayla.hotelsaas.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.TouchPanelBean;
import com.ayla.hotelsaas.utils.DensityUtils;

import java.util.List;

public class TouchPanelAdapter extends BaseAdapter {

    private final List<TouchPanelBean> mTouchPanelBeans;
    int dp6;

    public TouchPanelAdapter(List<TouchPanelBean> dataList) {
        mTouchPanelBeans =dataList;
        this.dp6 = DensityUtils.dip2px(MyApplication.getContext(), 6);
    }

    @Override
    public int getCount() {
        return mTouchPanelBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(MyApplication.getContext(), R.layout.adapter_touchpaner, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        LinearLayout ll_child_width = (LinearLayout) view.findViewById(R.id.ll_child_width);
        ll_child_width.setLayoutParams(
                new LinearLayout.LayoutParams(400, 400));

        TouchPanelBean bean = mTouchPanelBeans.get(position);
        tv_title.setText(bean.getPropertyValue());

        return view;
    }
}
