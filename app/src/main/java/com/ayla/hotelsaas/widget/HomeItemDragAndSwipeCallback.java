package com.ayla.hotelsaas.widget;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;

public class HomeItemDragAndSwipeCallback extends ItemDragAndSwipeCallback {

    public HomeItemDragAndSwipeCallback(BaseItemDraggableAdapter adapter) {
        super(adapter);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        return true;
    }
}
