package com.example.inv_componentes.exception;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
public class ErrorResponse {

    private int status;
    private String error;
    private LocalDateTime date;
    private HashMap<String,String> errors;

    public ErrorResponse(int status, String error, HashMap<String, String> errors) {
        this.status = status;
        this.error = error;
        this.errors = errors;
        date = LocalDateTime.now();
    }
}
