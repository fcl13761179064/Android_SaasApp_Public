package com.ayla.hotelsaas.mvp.view;


import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.RoomManageBean;


/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public interface RoomMoreView extends BaseView {

    //错误提示
    void operateError(String msg);

    //操作成功
    void operateSuccess(String is_rename);

    //移除成功
    void operateRemoveSuccess(String is_rename);

    //失败成功
    void operateMoveFailSuccess(String code,String msg);

}
