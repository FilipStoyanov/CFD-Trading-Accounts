package com.t212.accounts.positions.api.rest.models;

public class ApiResponse {
    private final int status;
    private final String message;
    private Object result;

    public ApiResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiResponse(int status, String message, Object result) {
        this(status, message);
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "ApiResponse [statusCode=" + status + ", message=" + message + "]";
    }
}
