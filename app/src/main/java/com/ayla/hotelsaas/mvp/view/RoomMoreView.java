package com.ayla.hotelsaas.mvp.view;


import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.RoomManageBean;


/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public interface RoomMoreView extends BaseView {

    //错误提示
    void renameFailed(String msg);

    //操作成功
    void renameSuccess(String newName);

    //移除成功
    void removeSuccess(String is_rename);

    //失败成功
    void removeFailed(String code, String msg);

    void planCheckResult(boolean s);

    void relationIdSuccess(BaseResult s);

    void relationIdFail(String s);


}
