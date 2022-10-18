package com.clarus12.clarusscanner.dto;

public class BasicResponseDto {
    /*
        "timestamp": "2022-08-27T13:14:26.3808362",
    "status": 200,
    "error": "OK",
    "code": "SUCCESS",
    "message": "성공적으로 수행하였습니다"
     */

    String timestamp;
    int status;
    String error;
    String code;
    String message;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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
}
