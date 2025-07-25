package com.example.myth.vo;

import com.example.myth.enums.RoleEnum;
import com.example.myth.po.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountVO {
    private int id;
    private String username;
    private String password;
    private String name;
    private String avatar;
    private RoleEnum role;
    private String telephone;
    private String email;
    private String location;

    public Account toPO(){
        Account account = new Account();
        account.setId(this.id);
        account.setUsername(this.username);
        account.setPassword(this.password);
        account.setName(this.name);
        account.setAvatar(this.avatar);
        account.setTelephone(this.telephone);
        account.setRole(this.role);
        account.setEmail(this.email);
        account.setLocation(this.location);
        return account;
    }
}
