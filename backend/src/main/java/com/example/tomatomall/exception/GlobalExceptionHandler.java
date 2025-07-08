package com.example.tomatomall.exception;


import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value=TomatoMallException.class)
    public Response handleTomatoMallException(TomatoMallException e) {
        e.printStackTrace();
        return Response.buildFailure(e.getMessage(),"401");
    }
}
