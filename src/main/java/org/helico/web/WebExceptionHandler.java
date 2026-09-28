package org.helico.web;

import org.helico.service.DictNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(DictNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String dictNotFound() {
        return "error/404";
    }

}
