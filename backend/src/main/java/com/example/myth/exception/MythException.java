package com.example.myth.exception;

public class MythException extends RuntimeException {
    public MythException(String message) {
        super(message);
    }

    public static MythException usernameAlreadyExists() {return  new MythException("用户名已存在");}

    public static MythException usernameOrPasswordError() {return  new MythException("用户密码错误");}

    public static MythException notLogin() {return  new MythException("not login!");}

    public static MythException userNotFound() {return  new MythException("用户不存在");}
}
