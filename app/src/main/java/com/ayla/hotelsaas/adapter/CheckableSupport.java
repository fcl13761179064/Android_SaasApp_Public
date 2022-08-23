package com.ayla.hotelsaas.adapter;

public class CheckableSupport<T> {

    private boolean checked;
    private T data;

    public CheckableSupport(T data) {
        this.data = data;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
