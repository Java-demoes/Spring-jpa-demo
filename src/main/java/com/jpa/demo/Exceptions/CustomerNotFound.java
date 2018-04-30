package com.jpa.demo.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Customer not found!")
public class CustomerNotFound extends Exception{
    public CustomerNotFound( String message){
        super(message);
    }

}
