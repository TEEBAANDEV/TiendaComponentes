package com.example.inv_cliente.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
public class ErrorResponse {
    private int status;
    private String error;
    private LocalDateTime timestamp;
    private HashMap<String,String> errors;

    public ErrorResponse(String error, int status, HashMap<String, String> errors) {
        this.error = error;
        this.status = status;
        this.errors = errors;
    }
}