package io.exchange.core.service.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import feign.Feign.Builder;
import io.exchange.core.config.CoreConfig;
import io.exchange.core.dto.CoinMarketCapDto.ResCoinMarketCap;
import io.exchange.core.dto.EtherScanTransactionDto.ResEtherScanTransaction;
import io.exchange.core.dto.TransactionDto;
import io.exchange.core.hibernate.repository.EtherScanTransactionRepository;
import io.exchange.core.hibernate.repository.ManualTransactionRepository;
import io.exchange.core.provider.feign.EtherScanProvider;
import io.exchange.core.service.AdminWalletService;
import io.exchange.core.service.CoinMarketCapService;
import io.exchange.core.service.ManualTransactionService;
import io.exchange.core.service.TransactionService;
import io.exchange.core.service.WalletService;
import io.exchange.core.util.KeyGenUtils;
import io.exchange.core.util.ModelUtils;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.Category;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.Phase;
import io.exchange.domain.enums.Status;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.coin.AdminWallet;
import io.exchange.domain.hibernate.coin.EtherscanTransaction;
import io.exchange.domain.hibernate.coin.ManualTransaction;
import io.exchange.domain.hibernate.coin.Wallet;
import io.exchange.domain.util.NumberCompareUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class EtherScanApiService {

    private static final int MAX_CNT = 9999;
    private static final int SEARCH_OFFSET = 500;

    private final Builder feignBuilder;

    private final WalletService walletService;
    private final CoinMarketCapService coinMarketCapService;
    private final AdminWalletService adminWalletService;
    private final TransactionService transactionService;
    private final ManualTransactionService manualTransactionService;

    private final ManualTransactionRepository manualTransactionRepository;
    private final EtherScanTransactionRepository etherscanTransactionRepository;
    private final EntityManager em;

    private EtherScanProvider etherScanProvider;

    @PostConstruct
    public void bindObject() {
        if(CoreConfig.currentPhase.isEquals(Phase.PRD)){
            etherScanProvider = feignBuilder.target(EtherScanProvider.class, "https://api.etherscan.io");
        } else {
            etherScanProvider = feignBuilder.target(EtherScanProvider.class, "https://api-ropsten.etherscan.io");
        }
    }

    public EtherscanTransaction getNullable(String hash, CoinName coinName) {
        return this.etherscanTransactionRepository.findByHashAndCoinName(hash, coinName).orElse(null);
    }

    public EtherscanTransaction get(String hash, CoinName coinName) {
        return Optional.ofNullable(this.getNullable(hash, coinName))
                .orElseThrow(() -> new BusinessException(Code.TRANSACTION_NOT_EXISTS));
    }

    @Transactional
    public void etherScanSearchAndMerge(AdminWallet etherAdminWallet) {

        String targetAddress = etherAdminWallet.getAddress();

        Integer page = 1;
        Integer offset = SEARCH_OFFSET;

        ResEtherScanTransaction resEtherScanTransaction = etherscanTransactionRepository.findFailMinimumPage().orElse(null);
        ResCoinMarketCap etherMarketCap = null;
        BigDecimal allSoldPonyBalance = null;
        BigDecimal saleAvailPonyBalance = null;

        if(resEtherScanTransaction != null) {
            page = resEtherScanTransaction.getSearchPage();
        }

        for(;page < MAX_CNT; page++) {

            LocalDateTime statrtTime = LocalDateTime.now();

            // sleep 1 sec for 5count
            // etherscan api limit 5count/1sec
            if(page%5 == 0) {
                LocalDateTime fiveCntAfter = LocalDateTime.now();
                Long elpasedSec = statrtTime.until(fiveCntAfter, ChronoUnit.SECONDS);

                if(elpasedSec > 1) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        log.error("TimeUnit.SECONDS.sleep(1) InterruptedException : {}", e);
                    }
                }
            }

            EtherScanProvider.Result result = etherScanProvider.listTransactionByAddress(targetAddress, page, offset);

            if(result.getResult().isEmpty()) {
                break;
            }

            if(etherMarketCap == null) {
                etherMarketCap = this.coinMarketCapService.getCoinMarketCapEtherByRegDtmEqualMidnight();
                allSoldPonyBalance = this.walletService.getAllSoldPonyBalance();
                saleAvailPonyBalance = CoreConfig.MAX_SALE_PONY_BALANCE.subtract(allSoldPonyBalance);
                if(!NumberCompareUtils.isPositive(saleAvailPonyBalance)) {
                    saleAvailPonyBalance = BigDecimal.ZERO;
                }
            }

            List<EtherscanTransaction> etherscanTransactionList = Lists.newArrayList();

            for(Map<String, String> tx : result.getResult()) {

                try {

                    EtherscanTransaction etherscanTransaction = EtherscanTransaction.builder()
                            .hash(tx.get("hash"))
                            .coinName(etherAdminWallet.getCoinName())
                            .regIp(StaticUtils.getCurrentIp())
                            .regDtm(LocalDateTime.now())
                            .blockNumber(Long.valueOf(tx.get("blockNumber")))
                            .timeStampDtm(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(tx.get("timeStamp")) * 1000), TimeZone.getDefault().toZoneId()))
                            .fromAddress(tx.get("from"))
                            .toAddress(tx.get("to"))
                            .value(new BigDecimal(tx.get("value")).setScale(18).divide(new BigDecimal(Math.pow(10, 18))))
                            .contractAddress(tx.get("contractAddress"))
                            .gas(new BigDecimal(tx.get("gas")))
                            .gasUsed(new BigDecimal(tx.get("gasUsed")))
                            .nonce(Long.valueOf(tx.get("nonce")))
                            .blockHash(tx.get("blockHash"))
                            .transactionIndex(Long.valueOf(tx.get("transactionIndex")))
                            .gasPrice(new BigDecimal(tx.get("gasPrice")))
                            .cumulativeGasUsed(new BigDecimal(tx.get("cumulativeGasUsed")))
                            .confirmations(Long.valueOf(tx.get("confirmations")))
                            .isError(tx.get("isError"))
                            .txreceiptStatus(tx.get("txreceipt_status"))
                            .searchPage(page)
                            .searchOffset(offset)
                            .build();

                    this.etherscanTransactionRepository.save(etherscanTransaction);

                    // 
                    if(!StringUtils.equals(etherscanTransaction.getIsError(), "1") || NumberCompareUtils.isPositive(etherscanTransaction.getValue())) {
                        etherscanTransactionList.add(etherscanTransaction);
                    }

                } catch (Exception e) {
                    log.error("etherscanTransactionRepository.save(etherscanTransaction); error : {}", e);
                    continue;
                }
            }

            int pagingSize = 100;
            for (List<EtherscanTransaction> chunkEtherscanTransactionList : Lists.partition(etherscanTransactionList, pagingSize)) {

                List<String> txIds = chunkEtherscanTransactionList.stream().map(ether -> ether.getHash()).collect(Collectors.toList());
                List<ManualTransaction> originManualTransactionList = this.manualTransactionService.getByCoinNameAndTxidsAndStatusIsPending(CoinName.ETHEREUM, txIds);

                for(ManualTransaction manualTransaction : originManualTransactionList) {

                    EtherscanTransaction etherscanTransaction = chunkEtherscanTransactionList.stream()
                            .filter(ether -> StringUtils.equals(ether.getHash(), manualTransaction.getTxId()))
                            .findFirst()
                            .orElse(null);

                    if(etherscanTransaction == null) {
                        continue;
                    }

                    BigDecimal realAmount = etherscanTransaction.getValue();
                    BigDecimal reqAmount = manualTransaction.getReqAmount();

                    manualTransaction.injectRealAmount(realAmount);

                    if(NumberCompareUtils.isGT(realAmount, reqAmount)) {
                        manualTransaction.setReason("구매신청 수량이 실제수량보다 많습니다. 구매신청수량 : " + reqAmount + ", 실제수량 : " + realAmount);
//                        manualTransaction.changeStatus(Status.CANCEL);
//                        continue;
                    }
                    if(NumberCompareUtils.isLT(realAmount, reqAmount)) {
                        manualTransaction.setReason("구매신청 수량이 실제수량보다 적습니다. 구매신청수량 : " + reqAmount + ", 실제수량 : " + realAmount);
//                        manualTransaction.changeStatus(Status.CANCEL);
//                        continue;
                    }
                    if(NumberCompareUtils.isGT(reqAmount, saleAvailPonyBalance)) {
                        manualTransaction.setReason("구매신청 수량이 포니코인 사전 판매 남은 수량보다 큽니다. 구매신청수량 : " + reqAmount + ", 사전 판매 남은 수량 : " + saleAvailPonyBalance);
//                        manualTransaction.changeStatus(Status.CANCEL);
//                        continue;
                    }

                    // 1. user request for deposit ethereum.
                    // 2. etherscan api and database synchronized.
                    // 3. user ethereum manual transaction approval.
                    // 4. send ponybit.

                    manualTransaction.changeStatus(Status.APPROVAL);

                    TransactionDto.ReqTransactionAdd transationReq = ModelUtils.map(manualTransaction, TransactionDto.ReqTransactionAdd.class);
                    transactionService.createConfirmTransaction(transationReq);

                    Wallet ponyWallet = this.walletService.get(manualTransaction.getUserId(), CoinName.PONYCOIN).orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

                    // 1ponycoin = 0.1krw
                    // value is ether value
                    BigDecimal priceKrw = new BigDecimal(etherMarketCap.getPriceKrw());
                    BigDecimal newAmount = realAmount.multiply(priceKrw).divide(CoreConfig.PONY_KRW_PRICE);

                    ManualTransaction newManualTransaction = ManualTransaction.builder()
                            .userId(manualTransaction.getUserId())
                            .coinName(CoinName.PONYCOIN)
                            .category(Category.DEPOSIT)
                            .status(Status.APPROVAL)
                            .regIp(StaticUtils.getCurrentIp())
                            .regDtm(LocalDateTime.now())
                            .txId(KeyGenUtils.generateAddressIdByEthereum())
                            .fromAddress(etherAdminWallet.getAddress())
                            .toAddress(ponyWallet.getAddress())
                            .reqAmount(newAmount)
                            .realAmount(newAmount)
                            .build();

                    this.manualTransactionRepository.save(newManualTransaction);
                    transationReq = ModelUtils.map(newManualTransaction, TransactionDto.ReqTransactionAdd.class);
                    transactionService.createConfirmTransaction(transationReq);

                    ponyWallet.availableBalanceAdd(newAmount);
                }
            }

            em.flush();
            em.clear();
        }
    }

}

