package com.example.myth.po;

import com.example.myth.enums.RoleEnum;
import com.example.myth.vo.AccountVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Account {
    //id,username,password,name,avatar,role,telephone,email,location
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name="username")
    private String username;

    @Basic
    @Column(name="password")
    private String password;

    @Basic
    @Column(name="name")
    private String name;

    @Basic
    @Column(name = "avatar", columnDefinition = "mediumtext")
    private String avatar;

    @Basic
    @Column(name="role")
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Basic
    @Column(name="telephone")
    private String telephone;

    @Basic
    @Column(name="email")
    private String email;

    @Basic
    @Column(name="location")
    private String location;

    public AccountVO toVO(){
        AccountVO accountVO = new AccountVO();
        accountVO.setId(this.id);
        accountVO.setUsername(this.username);
        accountVO.setPassword(this.password);
        accountVO.setName(this.name);
        accountVO.setAvatar(this.avatar);
        accountVO.setRole(this.role);
        accountVO.setTelephone(this.telephone);
        accountVO.setEmail(this.email);
        accountVO.setLocation(this.location);
        return accountVO;
    }
}
