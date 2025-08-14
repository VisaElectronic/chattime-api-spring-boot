package com.chattime.chattime_api.exception;

import com.chattime.chattime_api.dto.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<String>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        BaseResponse<String> response = new BaseResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<BaseResponse<String>> handleIOException(IOException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Add more exception handlers as needed

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<String>> handleGlobalException(Exception ex, WebRequest request) {
        BaseResponse<String> response = new BaseResponse<>(false, "An unexpected error occurred: " + ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
