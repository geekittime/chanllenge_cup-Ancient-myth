package com.example.myth.exception;


import com.example.myth.vo.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value= MythException.class)
    public Response handleMythException(MythException e) {
        e.printStackTrace();
        return Response.buildFailure(e.getMessage(),"401");
    }
}
