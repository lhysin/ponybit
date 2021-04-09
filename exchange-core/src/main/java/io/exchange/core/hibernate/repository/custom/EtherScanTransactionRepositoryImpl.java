package io.exchange.core.hibernate.repository.custom;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import javax.persistence.EntityManager;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.JPQLQuery;

import io.exchange.core.dto.EtherScanTransactionDto;
import io.exchange.core.dto.EtherScanTransactionDto.ResEtherScanTransaction;
import io.exchange.core.hibernate.executor.CustomJpaSupport;
import io.exchange.domain.hibernate.coin.EtherscanTransaction;
import io.exchange.domain.hibernate.coin.QEtherscanTransaction;

public class EtherScanTransactionRepositoryImpl extends CustomJpaSupport implements EtherScanTransactionRepositoryCustom {

    public EtherScanTransactionRepositoryImpl(EntityManager em) {
        super(em, EtherscanTransaction.class);
    }

    @Override
    public Optional<ResEtherScanTransaction> findFailMinimumPage() {

        QEtherscanTransaction qEtherScan = QEtherscanTransaction.etherscanTransaction;

        QBean<ResEtherScanTransaction> etherScanBean = 
                Projections.bean(ResEtherScanTransaction.class, qEtherScan.searchPage);

        JPQLQuery<EtherScanTransactionDto.ResEtherScanTransaction> jpqlQuery = query().select(etherScanBean)
                .from(qEtherScan)
                .where(qEtherScan.timeStampDtm.after(LocalDateTime.now().minus(1, ChronoUnit.DAYS)))
                .where(qEtherScan.isError.notEqualsIgnoreCase("1")
                        .or(qEtherScan.txreceiptStatus.notEqualsIgnoreCase("0")))
                .orderBy(qEtherScan.searchPage.asc());

        return Optional.ofNullable(jpqlQuery.fetchFirst());
    }

}