package com.example.inv_cliente.exception;


import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        HashMap<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            // Esto limpia el camino del campo (ej: "agregarItems.items[0].idUsuario" -> "idUsuario")
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);

            errors.put(fieldName, violation.getMessage());
        });

        ErrorResponse errorResponse = new ErrorResponse("Error de validacion en lote", 400, errors);
        errorResponse.setTimestamp(LocalDateTime.now());
        return errorResponse;
    }

    // 2. Captura cuando la validación falla en objetos individuales directo en el @RequestBody
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBindException(WebExchangeBindException ex){
        HashMap<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        ErrorResponse errorResponse = new ErrorResponse("Error de validacion", 400, errors);
        errorResponse.setTimestamp(LocalDateTime.now());
        return errorResponse;
    }
}
