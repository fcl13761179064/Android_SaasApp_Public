package com.ayla.hotelsaas.base;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by haoxu on 16/9/20.
 * Email:877647044@qq.com
 * Introduce:
 */
public class BaseDialog extends AlertDialog implements View.OnClickListener{
    protected Context context;
    public BaseDialog(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(getLayoutId(), new RelativeLayout(getContext()));
        setContentView(view);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        initViews(view);
    }

    protected int getLayoutId(){
        return 0;
    }

    protected void initViews(View view){

    }

    @Override
    public void onClick(View v) {

    }
}
