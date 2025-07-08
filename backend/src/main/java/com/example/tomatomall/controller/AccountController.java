package com.example.tomatomall.controller;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.vo.AccountVO;
import com.example.tomatomall.vo.Response;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Resource
    AccountService accountService;

    /**
     * 获取用户详情
     */
    @GetMapping("/{username}")
    public Response<AccountVO> getUser(@PathVariable String username) {
        return Response.buildSuccess(accountService.getAccount(username));
    }

    /**
     * 创建新的用户
     */
    @PostMapping()
    public Response<String> createUser(@RequestBody AccountVO userVO) {
        if(accountService.createAccount(userVO))
            return Response.buildSuccess("注册成功");
        return Response.buildSuccess("注册失败");
    }

    /**
     * 更新用户信息
     */
    @PutMapping()
    public Response<Boolean> updateUser(@RequestBody AccountVO userVO) {
        return Response.buildSuccess(accountService.updateAccount(userVO));
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public ResponseEntity<Response<Map<String, String>>> login(@RequestBody Map<String,String> loginInfo) {
        String token = accountService.login(loginInfo.get("username"),loginInfo.get("password"));
        Map<String, String> login_result = new HashMap<>();
        Account account=accountService.getAccount(loginInfo.get("username")).toPO();
        login_result.put("token", token);
        login_result.put("username", loginInfo.get("username"));
        login_result.put("role", String.valueOf(account.getRole()));
        return ResponseEntity.ok(Response.buildSuccess(login_result));
    }

    //获取用户id
    @GetMapping("/userId")
    public Response<Integer> getCurrentUserId()
    {
        return Response.buildSuccess(accountService.getCurrentUserId());
    }
}
