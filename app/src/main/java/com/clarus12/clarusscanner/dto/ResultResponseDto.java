package com.clarus12.clarusscanner.dto;

import com.google.gson.annotations.SerializedName;

public class ResultResponseDto<T> {

    int status;
    String code;
    String message;
    T result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T  getResult() {
        return result;
    }

    public void setResult(T  result) {
        this.result = result;
    }

}
