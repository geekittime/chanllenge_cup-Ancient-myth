package com.example.tomatomall.service;

import com.example.tomatomall.vo.AccountVO;

public interface AccountService {
    //创建新用户
    Boolean createAccount(AccountVO accountVO);

    AccountVO getAccount(String userName);

    String login(String username, String password);

    Boolean updateAccount(AccountVO accountVO);

    Integer getCurrentUserId();

}
