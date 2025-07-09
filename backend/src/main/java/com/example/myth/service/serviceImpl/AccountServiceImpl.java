package com.example.myth.service.serviceImpl;

import com.example.myth.exception.MythException;
import com.example.myth.po.Account;
import com.example.myth.repository.AccountRepository;
import com.example.myth.service.AccountService;
import com.example.myth.util.TokenUtil;
import com.example.myth.vo.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.myth.util.SecurityUtil;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public Boolean createAccount(AccountVO accountVO) {
        Account account =accountRepository.findByUsername(accountVO.getUsername());
        if(account != null){
            throw MythException.usernameAlreadyExists();
        }
        Account newAccount = accountVO.toPO();
        String rawPassword = accountVO.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        newAccount.setPassword(encodedPassword);



        accountRepository.save(newAccount);
        return true;
    }

    @Override
    public Boolean updateAccount(AccountVO accountVO) {
        Account account = securityUtil.getCurrentAccount();
        if(account==null){
            System.out.println("LLLLLLL");
            return false;
        }
        if(!accountVO.getPassword().isEmpty()){
            String rawPassword = accountVO.getPassword();
            //System.out.println(rawPassword+"sdfsdafsaf");
            String encodedPassword = passwordEncoder.encode(rawPassword);
            account.setPassword(encodedPassword);
        }
        if(accountVO.getEmail()!=null){
            account.setEmail(accountVO.getEmail());
        }
        if(accountVO.getAvatar()!=null){
            account.setAvatar(accountVO.getAvatar());
        }
        if(accountVO.getName()!=null){
            account.setName(accountVO.getName());
        }
        if(accountVO.getTelephone()!=null){
            account.setTelephone(accountVO.getTelephone());
        }
        if(accountVO.getLocation()!=null){
            account.setLocation(accountVO.getLocation());
        }
        if(accountVO.getRole()!=null){
            account.setRole(accountVO.getRole());
        }
        accountRepository.save(account);
        return true;
    }

    @Override
    public Integer getCurrentUserId() {
        return securityUtil.getCurrentAccount().getId();
    }

    @Override
    public String login(String username, String rawPassword) {
        Account account = accountRepository.findByUsername(username);
        if(account == null){
            throw MythException.userNotFound();
        }
        if(!passwordEncoder.matches(rawPassword, account.getPassword())){  //passwordEncoder.matches(rawPassword, account.getPassword())
            throw MythException.usernameOrPasswordError();
        }
        return tokenUtil.getToken(account);
    }

    @Override
    public AccountVO getAccount(String userName) {
        System.out.println("获取用户信息成功！");
        if(userName.isEmpty())
        {
            Account account=securityUtil.getCurrentAccount();
            return account.toVO();
        }
        else
        {
            Account account = accountRepository.findByUsername(userName);
            return account.toVO();
        }
    }
}
