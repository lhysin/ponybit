package io.exchange.core.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.exchange.core.hibernate.repository.FingerPrintRepository;
import io.exchange.domain.enums.Active;
import io.exchange.domain.hibernate.user.FingerPrint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class FingerPrintService {

    private final FingerPrintRepository fingerPrintRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean avaliableDeviceAccess(Long userId, String fingerprint) {
        List<FingerPrint> fingerPrints = fingerPrintRepository.findAllByUserIdAndDelDtmIsNullAndActive(userId, Active.Y);
        if (fingerPrints == null || fingerPrints.size() <= 0) {
            return false;
        }

        for (FingerPrint fingerPrint : fingerPrints) {
            if (bCryptPasswordEncoder.matches(fingerprint, fingerPrint.getHashKey())) {
                return true;
            }
        }
        return false;
    }
}
