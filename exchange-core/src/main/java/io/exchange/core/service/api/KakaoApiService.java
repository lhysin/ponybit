package io.exchange.core.service.api;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import feign.Feign.Builder;
import io.exchange.core.config.CoreConfig;
import io.exchange.core.dto.UserDto.ResUser;
import io.exchange.core.hibernate.repository.KakaoUserRepository;
import io.exchange.core.hibernate.repository.UserTokenRepository;
import io.exchange.core.provider.feign.KakaoProvider;
import io.exchange.core.provider.feign.KakaoProvider.KakaoMeResult;
import io.exchange.core.provider.feign.KakaoProvider.OauthResult;
import io.exchange.core.provider.model.FeignBody;
import io.exchange.core.service.ManualTransactionService;
import io.exchange.core.service.UserService;
import io.exchange.core.service.WalletService;
import io.exchange.core.util.KeyGenUtils;
import io.exchange.core.util.ModelUtils;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.Active;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.UserLevel;
import io.exchange.domain.enums.UserTokenType;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.user.KakaoUser;
import io.exchange.domain.hibernate.user.User;
import io.exchange.domain.hibernate.user.UserToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoApiService {

    private final static String KOREA_CONTRY_ISO = "KR";

    private final Builder feignBuilder;

    private final UserService userService;
    private final WalletService walletService;
    private final ManualTransactionService manualTransactionService;
    private final KakaoUserRepository kakaoUserRepository;
    private final UserTokenRepository userTokenRepository;
    private final SessionRegistry sessionRegistry;
    private final Gson gson;

    @Transactional
    public void sendKakaoMsg(String code, Long userId, UserLevel userLevel) {

        if(userLevel.getLevel() > UserLevel.LEVEL1.getLevel()){
            throw new BusinessException(Code.USER_ALREADY_KAKAO_VERIFICATION);
        }

        KakaoProvider kakaoAuthProvider = feignBuilder.target(KakaoProvider.class, "https://kauth.kakao.com");
        KakaoProvider.OauthResult oauthResult = null;

        try {
            oauthResult = kakaoAuthProvider.getKakaoOuthToken(StaticUtils.getBaseUrl(), code);
        } catch (Exception e) {
            log.error("kakaoProvider.getKakaoOuthToken() error : {}", e);
            throw new BusinessException(Code.FEIGN_API_ERROR);
        }

        KakaoProvider kakaoAPIProvider = feignBuilder.target(KakaoProvider.class, "https://kapi.kakao.com");
        KakaoProvider.KakaoMeResult meResult = null;
        try {
            meResult = kakaoAPIProvider.getKakaoAppUserInfo(oauthResult.getAccess_token());
        } catch (Exception e) {
            log.error("kakaoProvider.getKakaoAppUserInfo() error : {}", e);
            throw new BusinessException(Code.FEIGN_API_ERROR);
        }

        KakaoUser kakaoUser = this.kakaoUserRepository.findByKakaoUserId(meResult.getId()).orElse(null);
        if(kakaoUser != null) {
            throw new BusinessException(Code.KAKAO_USER_ALREADY_EXISTS);
        }

        LocalDateTime now = LocalDateTime.now();
        String token = KeyGenUtils.generateEmailConfirmNumericKey();
        UserToken userToken = UserToken.builder()
                .userId(userId)
                .type(UserTokenType.KAKAO_ACTIVE)
                .regIp(StaticUtils.getCurrentIp())
                .regDtm(now)
                .sendDtm(now)
                .token(token)
                .build();

        Map<String, Object> headerMap = Maps.newHashMap();
        headerMap.put("Authorization", "Bearer " + oauthResult.getAccess_token());
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");

        String msg = "[Ponybit] 카카오톡 인증";
        msg += "\n\n아래의 버튼 클릭시 카카오톡 인증이 완료됩니다.";
//        msg += "\n[주의] 현재의 메세지를 타인에게 노출시";
//        msg += "\n민형사상의 책임이 발생할 수 있습니다.";
//        msg += "\n인증번호 관리에 주의바랍니다.";

        Map<String, Object> bodyMap = Maps.newHashMap();
        bodyMap.put("object_type", "text");
        bodyMap.put("text", msg);
        bodyMap.put("link",
                ImmutableMap.of("web_url", StaticUtils.getBaseUrl() + "/api/v1.0/kakaos/verify?token=" + userToken.getToken(), 
                                "mobile_web_url", StaticUtils.getBaseUrl() + "/api/v1.0/kakaos/verify?token=" + userToken.getToken())
                );
        bodyMap.put("button_title", "카카오톡 인증하기");

        String templateObjectJsonStr = ModelUtils.toJsonString(bodyMap);

        try {
            kakaoAPIProvider.sendKakaoMsg(headerMap, templateObjectJsonStr);
        } catch (Exception e) {
            if(e instanceof BusinessException) {
                Object data = BusinessException.class.cast(e).data;
                if(data instanceof Map) {
                    FeignBody body = ModelUtils.map(data, FeignBody.class);
                    // /v1/api/talk/profile -> error code -501
                    // given account is not connected to any talk user.
                    if(body.getCode() == -501) {
                        log.debug(body.getMsg());
                        throw new BusinessException(Code.USER_NOT_KAKAO_TALK);
                    }
                }
            }
            log.error("kakaoProvider.sendKakaoMsg() error : {}", e);
            throw new BusinessException(Code.FEIGN_API_ERROR);
        }

        this.userTokenRepository.save(userToken);

        try {
            this.mergeKakaoProfile(oauthResult, meResult, userId);
        } catch (Exception e) {
            log.debug("mergeKakaoProfile()", e);
        }

    }

    @Transactional
    public ResUser kakaoVerify(String token) {

        UserToken userToken = Optional.ofNullable(userTokenRepository.findOneByToken(token))
                .orElseThrow(() -> new BusinessException(Code.TOKEN_INVALID));

        Long userId = userToken.getUserId();
        User user = this.userService.get(userId).orElseThrow(() -> new BusinessException(Code.USER_NOT_EXISTS, "user_id:" + userId));
        UserLevel userLevel = user.getUserLevel();

        if(userLevel.getLevel() > UserLevel.LEVEL1.getLevel()){
            return null;
        }

        user.changeUserLevel(UserLevel.LEVEL2);

        this.userService.addPromotionPony(user, CoreConfig.AUTH_PROMOTION_PONY_BALANCE, null, "카카오톡 인증");
        this.manualTransactionService.changePonyPromotionStatusIsApproval(user);

        return ModelUtils.map(user, ResUser.class);
    }

    private void mergeKakaoProfile(OauthResult oauthResult, KakaoMeResult meResult, Long userId) {

        KakaoProvider kakaoAPIProvider = feignBuilder.target(KakaoProvider.class, "https://kapi.kakao.com");
        KakaoProvider.KakaoTalkResult talkResult = null;
        try {
            talkResult = kakaoAPIProvider.getKakaoTalkProfile(oauthResult.getAccess_token());
        } catch (Exception e) {
            if(e instanceof BusinessException) {
                Object data = BusinessException.class.cast(e).data;
                if(data instanceof Map) {
                    FeignBody body = ModelUtils.map(data, FeignBody.class);
                    // /v1/api/talk/profile -> error code -501
                    // given account is not connected to any talk user.
                    if(body.getCode() == -501) {
                        log.debug(body.getMsg());
                        throw new BusinessException(Code.USER_NOT_KAKAO_TALK);
                    }
                }
            }
            log.error("kakaoProvider.getKakaoTalkProfile() error : {}", e);
            throw new BusinessException(Code.FEIGN_API_ERROR);
        }

        Active isKakaoTalkUser = Active.Y;
        String countryISO = talkResult.getCountryISO();
        if(!StringUtils.equals(countryISO, KOREA_CONTRY_ISO)) {
            isKakaoTalkUser = Active.N;
        }

        KakaoUser kakaoUser = KakaoUser.builder()
                .userId(userId)
                .kakaoUserId(meResult.getId())
                .regIp(StaticUtils.getCurrentIp())
                .regDtm(LocalDateTime.now())
                .isKakaoTalkUser(isKakaoTalkUser)
                .kakaoNickName(talkResult.getNickName())
                .kakaoProfileImageURL(talkResult.getProfileImageURL())
                .kakaoThumbnailURL(talkResult.getThumbnailURL())
                .kakaoCountryISO(talkResult.getCountryISO())
                .build();

        this.kakaoUserRepository.save(kakaoUser);
    }
}

