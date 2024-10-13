package com.example.pilates_studio.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionController {
    @RequestMapping("/error")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundError(){
        return "error/404";
    }

    @RequestMapping("/error/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnauthorizedError() {
        return "error/401";
    }

    @RequestMapping("/error/500")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalServerError() {
        return "error/500";
    }
}
