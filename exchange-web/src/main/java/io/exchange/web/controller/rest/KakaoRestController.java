package io.exchange.web.controller.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.exchange.core.dto.UserDto.ResUser;
import io.exchange.core.service.UserTokenService;
import io.exchange.core.service.api.KakaoApiService;
import io.exchange.core.util.StaticUtils;
import io.exchange.web.util.LoginUser;
import io.exchange.web.util.StaticWebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Slf4j
@RestController
@Validated
@RequestMapping("/api/v1.0/kakaos")
@RequiredArgsConstructor
public class KakaoRestController {

    private final UserTokenService userTokenService;
    private final KakaoApiService kakaoApiService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/send/msg")
    @ResponseStatus(HttpStatus.OK)
    public void sendKakaoMsg(String code, LoginUser loginUser) {

        this.userTokenService.checkKakaoApiCount(loginUser.getId());

        this.kakaoApiService.sendKakaoMsg(code, loginUser.getId(), loginUser.getUserLevel());

    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(String token) {

        ResUser resUser = this.kakaoApiService.kakaoVerify(token);
        if(resUser != null) {
            LoginUser loginUser = StaticWebUtils.findLoginUserByUserId(resUser.getId());
            if(loginUser != null) {
                loginUser.changeUserLevel(resUser.getUserLevel());
            }
        }

        String content = "<script>";
        content += "alert(\'카카오톡 인증에 성공하였습니다.\\n마이페이지에서 새로고침 후 확인해주세요.\');";
        content += "location.href = '" + StaticUtils.getBaseUrl() + "';";
        content += "</script>";
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);

        return new ResponseEntity<String>(content, responseHeaders, HttpStatus.OK);
    }

}