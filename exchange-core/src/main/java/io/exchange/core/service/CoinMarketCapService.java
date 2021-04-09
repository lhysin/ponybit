package io.exchange.core.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;

import io.exchange.core.dto.CoinMarketCapDto.ResCoinMarketCap;
import io.exchange.core.hibernate.repository.CoinMarketCapRepository;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.coin.QCoinMarketCap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinMarketCapService {

    private final CoinMarketCapRepository coinMarketCapRepository;

    public ResCoinMarketCap getCoinMarketCapEtherByRegDtmEqualMidnight() {

        QCoinMarketCap qcoinMarketCap = QCoinMarketCap.coinMarketCap;

        QBean<ResCoinMarketCap> qcoinMarketCapBean = Projections.bean(
                ResCoinMarketCap.class, 
                QCoinMarketCap.coinMarketCap.coinName,
                QCoinMarketCap.coinMarketCap.regDtm,
                QCoinMarketCap.coinMarketCap.priceKrw);

        BooleanBuilder builder = new BooleanBuilder();
        // regDtm >= today 00hour 00sec order by regDtm asc limit 1
        builder.and(qcoinMarketCap.regDtm.goe(LocalDate.now().atTime(0, 0)));
        builder.and(qcoinMarketCap.coinName.eq(CoinName.ETHEREUM));

        ResCoinMarketCap resCoinMarketCap = coinMarketCapRepository.findOneFirst(qcoinMarketCapBean, builder.getValue(), qcoinMarketCap.regDtm.asc())
                .orElse(null);

        if(resCoinMarketCap == null) {
            builder = new BooleanBuilder();
            builder.and(qcoinMarketCap.coinName.eq(CoinName.ETHEREUM));
            resCoinMarketCap = this.coinMarketCapRepository.findOneFirst(qcoinMarketCapBean, builder.getValue(), qcoinMarketCap.regDtm.asc())
                    .orElseThrow(() -> new BusinessException(Code.BAD_REQUEST));
        }

        return resCoinMarketCap;
    }
}
