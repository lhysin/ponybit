package io.exchange.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.exchange.core.service.UserService;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.Role;
import io.exchange.domain.enums.UserLevel;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.user.User;
import io.exchange.web.util.LoginUser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.trace("Authenticating user with loginId={}", email);

        User user = userService.get(email).orElseThrow(() -> new BusinessException(Code.USER_NOT_EXISTS, "email:" + email));;

        if(user.getUserLevel().getLevel() < UserLevel.LEVEL1.getLevel()) {
            throw new BusinessException(Code.USER_NOT_EMAIL_VERIFICATION);
        }

        return new LoginUser(user, this.getGrantedAuthorities(user.getRole()));
    }

    /**
     * login active role
     */
    private List<GrantedAuthority> getGrantedAuthorities(Role roleEnum) {
        final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(roleEnum.name()));
        return authorities;
    }
}
