package com.lucia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("email")
    private String email;

    public EmailResponse() {}

    public EmailResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public EmailResponse(boolean success, String message, String email) {
        this.success = success;
        this.message = message;
        this.email = email;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
