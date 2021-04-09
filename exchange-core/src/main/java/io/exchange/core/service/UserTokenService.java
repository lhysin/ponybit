package io.exchange.core.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import io.exchange.core.hibernate.repository.UserTokenRepository;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.UserTokenType;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.user.UserToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class UserTokenService {

    private final UserTokenRepository userTokenRepository;

    @Transactional
    public void checkKakaoApiCount(Long userId) {

        UserToken userToken = this.userTokenRepository.findByUserIdAndType(userId, UserTokenType.KAKAO_API_REQ_CNT).orElse(null);
        if(userToken == null) {
            userToken = UserToken.builder()
                    .userId(userId)
                    .type(UserTokenType.KAKAO_API_REQ_CNT)
                    .regIp(StaticUtils.getCurrentIp())
                    .regDtm(LocalDateTime.now())
                    .sendDtm(LocalDateTime.now())
                    .token("1")
                    .build();
            this.userTokenRepository.save(userToken);
        } else {
            int currentCnt = Integer.valueOf(userToken.getToken());

            String newToken = "1";
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowMidnight = now.toLocalDate().atStartOfDay();
            LocalDateTime sendDtm = userToken.getSendDtm();
            if(sendDtm.isBefore(nowMidnight)) {
                currentCnt = 0;
            }

            if(currentCnt > 2) {
                LocalDateTime tomorrowMidnight = now.plus(1, ChronoUnit.DAYS).toLocalDate().atStartOfDay();
                if(userToken.getSendDtm().isBefore(tomorrowMidnight)) {
                    throw new BusinessException(Code.KAKAO_API_REQ_CNT_LIMIT);
                }
            } else {
                newToken = String.valueOf(currentCnt + 1);
            }
            userToken.changeToken(newToken);
            userToken.setSendDtm(LocalDateTime.now());
        }

    }
}
