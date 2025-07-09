package com.example.myth.util;

import com.example.myth.po.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class SecurityUtil {

    @Autowired
    HttpServletRequest httpServletRequest;

    public Account getCurrentAccount() { return (Account) httpServletRequest.getSession().getAttribute("currentAccount");}
}
