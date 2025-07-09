package com.example.myth.service;

import com.example.myth.vo.AccountVO;

public interface AccountService {
    //创建新用户
    Boolean createAccount(AccountVO accountVO);

    AccountVO getAccount(String userName);

    String login(String username, String password);

    Boolean updateAccount(AccountVO accountVO);

    Integer getCurrentUserId();

}
