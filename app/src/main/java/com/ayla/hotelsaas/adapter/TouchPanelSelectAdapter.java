package com.ayla.hotelsaas.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.TouchPanelBean;
import com.ayla.hotelsaas.utils.DensityUtils;

import java.util.List;

public class TouchPanelSelectAdapter extends BaseAdapter {

    private final List<TouchPanelBean> mTouchPanelBeans;
    int dp6;

    public TouchPanelSelectAdapter(List<TouchPanelBean> dataList) {
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
        View view = View.inflate(MyApplication.getContext(), R.layout.adapter_select_touchpaner, null);
        ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TouchPanelBean bean = mTouchPanelBeans.get(position);

        iv_icon.setImageResource(bean.getIconRes());
        tv_title.setText(bean.getPropertyValue());

        return view;
    }
}
