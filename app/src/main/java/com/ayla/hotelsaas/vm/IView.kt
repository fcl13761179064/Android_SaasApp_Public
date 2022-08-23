package com.ayla.hotelsaas.vm

import android.view.View
import com.scwang.smart.refresh.layout.api.RefreshLayout

interface IView :ICommonEvent {
    fun onRefreshForError(refreshLayout: RefreshLayout) {

    }

    fun onRetryForError() {

    }

    fun onRefreshForEmpty(refreshLayout: RefreshLayout) {

    }

    fun onRetryForEmpty() {

    }

    fun onShowViewForPage(view: View, state: State) {

    }

    fun onHideViewForPage(view: View, state: State) {

    }

}