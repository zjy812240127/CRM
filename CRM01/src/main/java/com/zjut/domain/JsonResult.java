package com.zjut.domain;

public class JsonResult {
    private String message;
    private boolean success = true;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public JsonResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public JsonResult() {
    }

    @Override
    public String toString() {
        return "JsonResult{" +
                "message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}
