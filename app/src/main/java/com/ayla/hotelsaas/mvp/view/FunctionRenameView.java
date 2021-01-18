package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

import java.util.List;
import java.util.Map;

public interface FunctionRenameView extends BaseView {
    /**
     *
     * @param attributesBeans propertyName propertyNickname nickNameId
     */
    void showFunctions(List<Map<String, String>> attributesBeans);

    void renameSuccess();

    void renameFailed(Throwable throwable);
}
