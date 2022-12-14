package com.ayla.hotelsaas.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.WorkOrderBean;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;

/**
 * 酒店信息页面
 */
public class ProjectDetailFragment extends BaseMvpFragment {
    @BindView(R.id.tv_01)
    TextView textView01;
    @BindView(R.id.tv_02)
    TextView textView02;
    @BindView(R.id.tv_03)
    TextView textView03;
    @BindView(R.id.tv_04)
    TextView textView04;
    private WorkOrderBean.ResultListBean bean;
    String type = null;
    String trade = null;
    String status = null;


    public ProjectDetailFragment() {
    }

    public static ProjectDetailFragment newInstance(WorkOrderBean.ResultListBean bean) {
        Bundle args = new Bundle();
        args.putSerializable("bean", bean);
        ProjectDetailFragment fragment = new ProjectDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_project_detail;
    }

    @Override
    protected void initView(View view) {
        if (getArguments() != null) {
            bean = (WorkOrderBean.ResultListBean) getArguments().getSerializable("bean");
            if (bean != null) {
                switch (bean.getTrade()) {
                    case 1:
                        trade = "酒店";
                        break;
                    case 2:
                        trade = "家装";
                        break;
                    case 3:
                        trade = "地产";
                        break;
                    case 4:
                        trade = "公寓";
                        break;
                }

                switch (bean.getType()) {
                    case 0:
                        type = "正式";
                        break;
                    case 1:
                        type = "展箱";
                        break;
                    case 2:
                        type = "展厅";
                        break;
                }
                switch (bean.getConstructionStatus()) {
                    case 1:
                        status = "待施工";
                        break;
                    case 2:
                        status = "施工中";
                        break;
                    case 3:
                        status = "已完成";
                        break;
                }
            }
            textView01.setText(String.format("行业：%s", trade));
            textView02.setText(String.format("类型：%s", type));
            textView03.setText(String.format("施工阶段：%s", status));

            SimpleDateFormat sf_source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat sf_target = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

            try {
                textView04.setText(String.format("施工日期：%s-%s", sf_target.format(sf_source.parse(bean.getStartDate())), sf_target.format(sf_source.parse(bean.getEndDate()))));
            } catch (Exception e) {
                textView04.setText("施工日期：");
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }
}
