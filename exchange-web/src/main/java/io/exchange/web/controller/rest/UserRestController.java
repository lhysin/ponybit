package io.exchange.web.controller.rest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.exchange.core.annotation.RequestQuery;
import io.exchange.core.dto.CommonDto;
import io.exchange.core.dto.CommonDto.ResPage;
import io.exchange.core.dto.UserDto;
import io.exchange.core.dto.UserDto.ResUser;
import io.exchange.core.service.UserService;
import io.exchange.domain.hibernate.user.User;
import io.exchange.web.service.CustomValidationService;
import io.exchange.web.util.LoginUser;
import io.exchange.web.util.StaticWebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/v1.0/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final CustomValidationService customValidationService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    @ResponseBody
    public ResponseEntity<Object> getUsers(@RequestQuery CommonDto.ReqPage req) {

        customValidationService.validationObject(req);

        LocalDateTime start1 = LocalDateTime.now();
        ResPage<UserDto.UserWithWallets> usersWithWallet = userService.getAllUserWithWallets(req);
        LocalDateTime end1 = LocalDateTime.now();

        LocalDateTime start2 = LocalDateTime.now();
        usersWithWallet = userService.getAllUserWithWallets(req);
        LocalDateTime end2 = LocalDateTime.now();

        log.debug("======== num1:{}nanoSec", ChronoUnit.NANOS.between(start1, end1));
        log.debug("======== num2:{}nanoSec", ChronoUnit.NANOS.between(start2, end2));

        return StaticWebUtils.defaultSuccessResponseEntity(usersWithWallet);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> getUser(@PathVariable Long id) {

        UserDto.ResUser user = userService.getUser(id);

        return StaticWebUtils.defaultSuccessResponseEntity(user);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Object> registationUser(@RequestBody UserDto.ReqRegist req) {
//pwd + confirmPwd validation required.

        customValidationService.validationObject(req);

        User user = this.userService.createUserAndToken(req.getEmail(), req.getPwd(), req.getRefCd());
        log.trace("success put user={}", user);

        return StaticWebUtils.defaultSuccessResponseEntity();
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Object> chnageUserPassword(@RequestBody UserDto.ReqChangePassword req) {

        customValidationService.validationObject(req);

        User user = this.userService.changeUserPassword(req);
        log.trace("success put user={}", user);

        // clear current session
        StaticWebUtils.clearCurrentSessionByRequest();

        return StaticWebUtils.defaultSuccessResponseEntity();
    }

    @PutMapping("/{token}/active")
    @ResponseBody
    public ResponseEntity<Object> userActive(@PathVariable("token") String token) {

        User user = this.userService.userEmailVerificationByToken(token);
        log.trace("success active:{}", user);

        return StaticWebUtils.defaultSuccessResponseEntity();
    }

    @PutMapping("/{email}/resend")
    @ResponseBody
    public ResponseEntity<Object> resendEmail(@PathVariable("email") String email) {

        User user = this.userService.resendTokenByEmail(email);
        log.trace("success resendEmail:{}", user);

        return StaticWebUtils.defaultSuccessResponseEntity();
    }

    @PutMapping("/{email}/reset")
    @ResponseBody
    public ResponseEntity<Object> resetPwd(@PathVariable("email") String email) {

        User user = this.userService.userResetPwdByEmail(email);
        log.trace("success resetPwd:{}", user);

        return StaticWebUtils.defaultSuccessResponseEntity();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{refCd}/refCd")
    @ResponseStatus(HttpStatus.OK)
    public void refCd(@PathVariable("refCd") String refCd, LoginUser loginUser) {
        this.userService.userOtherRefCd(loginUser.getId(), refCd);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/refCntTop10Users")
    @ResponseBody
    public ResponseEntity<Object> getRefCntTop10Users() {
        List<ResUser> refCntTop10Users = this.userService.getRefCntTop10Users();
        return StaticWebUtils.defaultSuccessResponseEntity(refCntTop10Users);
    }
}

