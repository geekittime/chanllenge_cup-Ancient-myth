package com.example.tomatomall.exception;

public class TomatoMallException extends RuntimeException {
    public TomatoMallException(String message) {
        super(message);
    }

    public static TomatoMallException usernameAlreadyExists() {return  new TomatoMallException("用户名已存在");}

    public static TomatoMallException usernameOrPasswordError() {return  new TomatoMallException("用户密码错误");}

    public static TomatoMallException notLogin() {return  new TomatoMallException("not login!");}

    public static TomatoMallException userNotFound() {return  new TomatoMallException("用户不存在");}
}
