package io.exchange.core.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import io.exchange.core.dto.TransactionDto.ReqTransactionAdd;
import io.exchange.core.hibernate.repository.TransactionRepository;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.hibernate.coin.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@SuppressWarnings("unused")
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction createConfirmTransaction(ReqTransactionAdd req) {

        Transaction transaction = Transaction.builder()
                .txId(req.getTxId())
                .userId(req.getUserId())
                .coinName(req.getCoinName())
                .category(req.getCategory())
                .status(req.getStatus())
                .regIp(StaticUtils.getCurrentIp())
                .regDtm(LocalDateTime.now())
                .fromAddress(req.getFromAddress())
                .toAddress(req.getToAddress())
                .amount(req.getRealAmount())
                .build();

        return this.transactionRepository.save(transaction);
    }

}
