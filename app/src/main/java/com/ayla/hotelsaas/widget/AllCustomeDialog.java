package com.ayla.hotelsaas.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseDialog;


/**
 * Created by Administrator on 2016/10/27.
 */
public class AllCustomeDialog extends BaseDialog {
    private String title;
    private Context mContext;
    private TextView tv_ensure,tv_cancel;
    private OnSureClick onSureClick;
    private OnCancelClick onCancelClick;
    private TextView tv_message;
    private String message;
    private String mConfirm;
    private String cancleText;
    private TextView tv_custome_title;

    public AllCustomeDialog(Context context, String message, String title) {
        super(context);
        this.mContext=context;
       this.message= message;
        this.title=title;
    }

    public void setOnSureClick(String Confirm , OnSureClick onSureClick) {
       this.mConfirm= Confirm;
        this.onSureClick = onSureClick;
    }

    public void setOnCancelClick(String CancleText, OnCancelClick onCancelClick) {
        this.cancleText=CancleText;
        this.onCancelClick = onCancelClick;
    }




    @Override
    protected int getLayoutId() {
        return R.layout.all_custome_dialog;
    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);
        tv_cancel = (TextView) view.findViewById(R.id.tv_custome_cancel);
        tv_ensure = (TextView) view.findViewById(R.id.tv_custome_ensure);
        tv_message = (TextView) view.findViewById(R.id.tv_custome_message);
        tv_custome_title = (TextView) view.findViewById(R.id.tv_Custome_Title);
        tv_ensure.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_custome_title.setText(title==null?"":title);
        tv_message.setText(message == null ? "" : message);
        tv_ensure.setText(mConfirm == null ? "" : mConfirm);
        tv_cancel.setText(cancleText == null ? "" : cancleText);

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        dismiss();
        switch (v.getId()){
            case R.id.tv_custome_cancel:
                if (onCancelClick != null) {
                    onCancelClick.click(this);
                }

                break;
            case R.id.tv_custome_ensure:

                if (onSureClick != null) {
                    onSureClick.click(this);
                }
                break;
            default:
                break;
        }
    }
    public interface OnSureClick {
        void click(Dialog dialog);
    }

    public interface OnCancelClick {
        void click(Dialog dialog);
    }

}
