package io.exchange.core.hibernate.repository.custom;

import java.util.Optional;

import io.exchange.core.dto.EtherScanTransactionDto;

public interface EtherScanTransactionRepositoryCustom {

    Optional<EtherScanTransactionDto.ResEtherScanTransaction> findFailMinimumPage();

}
