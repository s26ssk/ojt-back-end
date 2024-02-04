package com.ra.util.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Getter
@Setter
public class CustomException extends Exception {


    private int status;

    public CustomException(String msg, int status) {
        super(msg);
        this.status = status;
    }

    public CustomException(String message, HttpStatus status) {
        super(message);
    }


}
