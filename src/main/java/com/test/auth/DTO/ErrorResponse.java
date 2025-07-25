package com.test.auth.DTO;

import java.time.Instant;
import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private int status;
    private Instant timestamp;

    public ErrorResponse(String message, int status,Instant time) {
        this.message = message;
        this.status = status;
        this.timestamp = time;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public ErrorResponse() {

    }
}
