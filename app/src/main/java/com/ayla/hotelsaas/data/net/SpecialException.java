package com.ayla.hotelsaas.data.net;

public final class SpecialException extends Exception {
    private int code;

    public SpecialException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
