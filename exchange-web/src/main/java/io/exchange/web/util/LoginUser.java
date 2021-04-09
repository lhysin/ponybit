package io.exchange.web.util;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import io.exchange.domain.enums.UserLevel;
import io.exchange.domain.hibernate.user.User;

@SuppressWarnings("serial")
public class LoginUser extends org.springframework.security.core.userdetails.User {

    private Long id;
    private String name;
    private UserLevel userLevel;
    private String myRefCd;

    public LoginUser(User user, List<GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPwd(), authorities);
        this.id = user.getId();
        this.name = user.getName();
        this.userLevel = user.getUserLevel();
        this.myRefCd = user.getMyRefCd();
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public UserLevel getUserLevel() {
        return this.userLevel;
    }

    public String getEmail() {
        return this.getUsername();
    }

    public String getMyRefCd() {
        return this.myRefCd;
    }

    public void changeUserLevel(UserLevel userLevel){
        this.userLevel = userLevel;
    }
}
