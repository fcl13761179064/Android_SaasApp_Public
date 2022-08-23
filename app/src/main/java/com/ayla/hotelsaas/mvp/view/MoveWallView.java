package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.MoveWallBean;
import com.ayla.hotelsaas.bean.ZxingMoveWallBean;
import com.ayla.hotelsaas.data.net.ServerBadException;

public interface MoveWallView extends BaseView {
    void getMoveWallDataSuccess(MoveWallBean moveWallBean, ZxingMoveWallBean move_wall_data);
    void getMoveWallDataFail(String o, ServerBadException throwable);
}
