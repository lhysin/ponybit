package io.exchange.core.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import io.exchange.core.config.CoreConfig;
import io.exchange.domain.enums.Phase;
import io.exchange.domain.enums.Status;
import io.exchange.domain.util.EnumUtils;
import io.exchange.domain.util.NumberCompareUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@SuppressWarnings("unused")
@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    public void enumCompareUtilsTest() {

        if(!CoreConfig.currentPhase.isEquals(Phase.LOCAL, Phase.DEV)) {
            return;
        }

        if(EnumUtils.isEqual(Status.PENDING, Status.PENDING)) {
            log.debug("EnumCompareUtils.isEqual(Status.PENDING, Status.PENDING) true");
        }
        if(!EnumUtils.isEqual(Status.PENDING, Status.CANCEL)) {
            log.debug("!EnumCompareUtils.isEqual(Status.PENDING, Status.CANCEL) true");
        }
        if(EnumUtils.isNotEqual(Status.PENDING, Status.CANCEL)) {
            log.debug("EnumCompareUtils.isNotEqual(Status.PENDING, Status.CANCEL) true");
        }
        if(EnumUtils.hasContain(Status.PENDING, Status.PENDING, Status.FAILED)) {
            log.debug("!EnumCompareUtils.isContain(Status.PENDING, Status.PENDING, Status.FAILED) true");
        }
    }

    public void numberCompareUtilsTest() {

        if(!CoreConfig.currentPhase.isEquals(Phase.LOCAL, Phase.DEV)) {
            return;
        }

        if(NumberCompareUtils.isEqual(new BigDecimal("-1"), new BigDecimal("-1"))) {
            log.debug("CompareUtil.isEqual(new BigDecimal(\"-1\"), new BigDecimal(\"-1\")) true");
        }
        if(NumberCompareUtils.isLT(new BigDecimal("-1"), new BigDecimal("1"))) {
            log.debug("CompareUtil.isEqual(new BigDecimal(\"-1\"), new BigDecimal(\"1\")) true");
        }
        if(NumberCompareUtils.isGT(new BigDecimal("1"), new BigDecimal("-1"))) {
            log.debug("CompareUtil.isEqual(new BigDecimal(\"1\"), new BigDecimal(\"-1\")) true");
        }
        if(NumberCompareUtils.isNotPositiveOrZero(new BigDecimal("-1"))) {
            log.debug("CompareUtil.isNotPositiveOrZero(new BigDecimal(\"-1\") true");
        }
        if(NumberCompareUtils.isPositiveOrZero(new BigDecimal("0"))) {
            log.debug("CompareUtil.isPositiveOrZero(new BigDecimal(\"0\") true");
        }
        if(NumberCompareUtils.isPositiveOrZero(new BigDecimal("1"))) {
            log.debug("CompareUtil.isPositiveOrZero(new BigDecimal(\"1\") true");
        }
        if(!NumberCompareUtils.isPositiveOrZero(new BigDecimal("-1"))) {
            log.debug("!CompareUtil.isPositiveOrZero(new BigDecimal(\"-1\") true");
        }
        if(NumberCompareUtils.isEqual(-1L, -1L)) {
            log.debug("CompareUtil.isEqual(-1L, -1L) true");
        }
        if(NumberCompareUtils.isEqual(-1L, 1L)) {
            log.debug("CompareUtil.isEqual(-1L, 1L) true");
        }
        if(NumberCompareUtils.isEqual(1L, -1L)) {
            log.debug("CompareUtil.isEqual(1L, -1L) true");
        }
        if(NumberCompareUtils.isNotPositiveOrZero(-1L)) {
            log.debug("CompareUtil.isNotPositiveOrZero(-1L) true");
        }
        if(NumberCompareUtils.isPositiveOrZero(0L)) {
            log.debug("CompareUtil.isPositiveOrZero(0L) true");
        }
        if(NumberCompareUtils.isPositiveOrZero(1L)) {
            log.debug("CompareUtil.isPositiveOrZero(1L) true");
        }
        if(!NumberCompareUtils.isPositiveOrZero(-1L)) {
            log.debug("!CompareUtil.isPositiveOrZero(-1L) true");
        }
    }
}
