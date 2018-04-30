package com.jpa.demo.controller;

import com.jpa.demo.Exceptions.CustomerNotFound;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.lang.InterruptedException;


@ControllerAdvice
public class Exceptionhandler {
    private static final Logger log = LoggerFactory.getLogger(Exceptionhandler.class);
    @ExceptionHandler({ InterruptedException.class })
    public ResponseEntity<String> handleException(InterruptedException e){
        JSONObject error = new JSONObject();
        try {
            error.put("msg", e.getMessage());
        }catch(JSONException je){
            log.error("error",je);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error.toString());
    }


}
