package com.ra.util.advice;

import com.ra.util.exception.*;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class AppExceptionHandle extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> exceptionHandler(CustomException appException) {
        return new ResponseEntity<>(appException.getMessage(), HttpStatusCode.valueOf(appException.getStatus()));
    }
}
