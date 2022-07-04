package com.finx.web.exceptions;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HandlerOfException {

    @ExceptionHandler(value = {
            NoSuchProgramException.class,
            NoTierAvailableException.class,
            NoSuchProductException.class,
            NoSuchProductVariationException.class,
            InsufficientCreditScoreException.class,
            TermSettingException.class
    })
    public ResponseEntity<Object> handleException(RuntimeException exception) {
        return new ResponseEntity<>(new JSONObject().put("message", exception.getMessage()).toString(), HttpStatus.NOT_FOUND);
    }
}
