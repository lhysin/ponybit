package io.exchange.web.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.exchange.core.dto.PreLoadDto;
import io.exchange.core.dto.UserDto;
import io.exchange.core.service.CoinService;
import io.exchange.core.service.UserService;
import io.exchange.core.util.ModelUtils;
import io.exchange.web.util.LoginUser;
import io.exchange.web.util.StaticWebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/preloads")
@Validated
@RequiredArgsConstructor
public class PreLoadRestController {

	private final UserService userService;
    private final CoinService coinService;

//    @GetMapping("/home")
//    @ResponseBody
//    public ResponseEntity<Object> home(LoginUser loginUser) {
//
//        PreLoadDto.Home home = new PreLoadDto.Home();
//
//        if(loginUser != null) {
//            UserDto.ResUser  resUser = ModelUtils.map(loginUser, UserDto.ResUser.class);
//            home.setUser(resUser);
//            home.setMyRefCnt(userService.getRefCnt(resUser.getMyRefCd()));
//        }
//        log.trace("PreLoad home={}", home);
//
//        return StaticWebUtils.defaultSuccessResponseEntity(home);
//    }
//
//    @GetMapping("/dashboard")
//    @ResponseBody
//    public ResponseEntity<Object> dashboard(LoginUser loginUser){
//
//        PreLoadDto.Dashboard dashboard = new PreLoadDto.Dashboard();
//
//        if(loginUser != null) {
//            UserDto.ResUser  resUser = ModelUtils.map(loginUser, UserDto.ResUser.class);
//            dashboard.setUser(resUser);
//            dashboard.setCoinWallets(this.coinService.getAllRegisteredCoin(resUser.getId()));
//            log.trace("PreLoad dashboard={}", dashboard);
//        }
//
//        return StaticWebUtils.defaultSuccessResponseEntity(dashboard);
//    }
}
