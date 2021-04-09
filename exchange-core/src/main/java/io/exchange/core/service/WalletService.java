package io.exchange.core.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.exchange.core.dto.CommonDto;
import io.exchange.core.dto.CommonDto.ResPage;
import io.exchange.core.dto.WalletDto;
import io.exchange.core.dto.WalletDto.ResWallet;
import io.exchange.core.hibernate.repository.AdminWalletRepository;
import io.exchange.core.hibernate.repository.CoinRepository;
import io.exchange.core.hibernate.repository.LevelRepository;
import io.exchange.core.hibernate.repository.WalletRepository;
import io.exchange.core.util.CustomPageable;
import io.exchange.core.util.KeyGenUtils;
import io.exchange.core.util.ModelUtils;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.WalletType;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.coin.AdminWallet;
import io.exchange.domain.hibernate.coin.Wallet;
import io.exchange.domain.util.EnumUtils;
import io.exchange.domain.util.NumberCompareUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class WalletService {

    private final CoinService coinService;

    private final WalletRepository walletRepository;
    private final CoinRepository coinRepository;
    private final LevelRepository levelRepository;
    private final AdminWalletRepository adminWalletRepository;
    private final EntityManager entityManager;

    public ResPage<WalletDto.ResWallet> getAllDynamicSearch(WalletDto.ReqWallets req) {

        Integer page = req.getPage();
        Integer size = req.getSize();

        Page<ResWallet> results = this.walletRepository.getAllDynamicSearch(req
                , CustomPageable.of(page, size));

        CommonDto.ResPage<ResWallet> res = CommonDto.ResPage.<ResWallet>builder()
                .page(page)
                .size(size)
                .list(results.getContent())
                .totalPages(results.getTotalPages())
                .build();

        return res;
    }

    public WalletDto.ResWallet getWallet(Long userId, CoinName coinName) {
        Wallet wallet = this.get(userId, coinName).orElse(null);
        if(wallet == null) {
            this.precreateWallet(userId, coinName);
            wallet = this.get(userId, coinName).orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));
        }
        return ModelUtils.map(wallet, WalletDto.ResWallet.class);
    }

    public Optional<Wallet> get(Long userId, CoinName coinName) {
        return walletRepository.findByUserIdAndCoinName(userId, coinName);
    }

//    public List<Wallet> getAll(Long userId) {
//        return walletRepository.findAllByPkUserId(userId);
//    }

    @Transactional
    public AdminWallet decreaseAvailableAdminBalance(CoinName coinName, WalletType walletType, BigDecimal amount) {

        if (NumberCompareUtils.isNotPositiveOrZero(amount)) {
            new BusinessException(Code.AMOUNT_IS_UNDER_ZERO);
        }

        AdminWallet adminWallet = adminWalletRepository.findByCoinNameAndType(coinName, walletType)
                .orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));
        adminWallet.availableBalanceSubtract(amount);
        return adminWallet;
    }

    @Transactional
    public AdminWallet increaseAvailableAdminBalance(CoinName coinName, WalletType walletType, BigDecimal amount) {

        if (NumberCompareUtils.isNotPositiveOrZero(amount)) {
            new BusinessException(Code.AMOUNT_IS_UNDER_ZERO);
        }

        AdminWallet adminWallet = adminWalletRepository.findByCoinNameAndType(coinName, walletType)
                .orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS)
        );
        adminWallet.availableBalanceAdd(amount);
        return adminWallet;
    }

    @Transactional
    public void increaseAvailableBalance(Long userId, CoinName coinName, BigDecimal amount) {

        if (NumberCompareUtils.isNotPositiveOrZero(amount)) {
            new BusinessException(Code.AMOUNT_IS_UNDER_ZERO);
        }

        Wallet wallet = this.get(userId, coinName).orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

        wallet.availableBalanceAdd(amount);
    }

    @Transactional
    public void decreaseAvailableBalance(Long userId, CoinName coinName, BigDecimal amount) {

        if (NumberCompareUtils.isNotPositiveOrZero(amount)) {
            new BusinessException(Code.AMOUNT_IS_UNDER_ZERO);
        }

        Wallet wallet = this.get(userId, coinName).orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

        if (NumberCompareUtils.isNotPositiveOrZero(wallet.getTotalBalance().subtract(amount))) {
            new BusinessException(Code.AMOUNT_IS_UNDER_ZERO);
        }

        wallet.availableBalanceSubtract(amount);
    }

    @Transactional
    public void increaseUsingBalance(Long userId, CoinName coinName, BigDecimal amount) {

        if (NumberCompareUtils.isNotPositiveOrZero(amount)) {
            new BusinessException(Code.AMOUNT_IS_UNDER_ZERO);
        }

        Wallet wallet = this.get(userId, coinName).orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

        wallet.usingBalanceAdd(amount);
    }

    @Transactional
    public void decreaseUsingBalance(Long userId, CoinName coinName, BigDecimal amount) {

        if (NumberCompareUtils.isNotPositiveOrZero(amount)) {
            new BusinessException(Code.AMOUNT_IS_UNDER_ZERO);
        }

        Wallet wallet = this.get(userId, coinName).orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

        if (NumberCompareUtils.isNotPositiveOrZero(wallet.getUsingBalance().subtract(amount))) {
            new BusinessException(Code.AMOUNT_IS_UNDER_ZERO);
        }

        wallet.usingBalanceSubtract(amount);
    }

    @Transactional
    public Wallet precreateWallet(Long loginUserId, CoinName coinName) {

        Wallet wallet = this.get(loginUserId, coinName).orElse(null);

        if (wallet == null) {

            Wallet.WalletBuilder builder = Wallet.builder()
                    .userId(loginUserId)
                    .coinName(coinName)
                    .regIp(StaticUtils.getCurrentIp())
                    .regDtm(LocalDateTime.now())
                    .address(KeyGenUtils.generateAddressIdByEthereum())
                    .availableBalance(BigDecimal.ZERO)
                    .usingBalance(BigDecimal.ZERO)
                    .todayWithdrawalTotalBalance(BigDecimal.ZERO)
                    .regDtm(LocalDateTime.now());
            if(EnumUtils.isEqual(CoinName.BITCOIN, coinName)) {
                builder.address(KeyGenUtils.generateKey(34));
            } else if(EnumUtils.isEqual(CoinName.ETHEREUM, coinName)) {
                builder.address(KeyGenUtils.generateAddressIdByEthereum());
            } else if (EnumUtils.isEqual(CoinName.KRW, coinName)) {
                builder.address(KeyGenUtils.generateKey(15));
                builder.depositDvcd(KeyGenUtils.generateNumericKey(8));
            } else if (EnumUtils.isEqual(CoinName.PONYCOIN, coinName)) {
                builder.address(KeyGenUtils.generateAddressIdByEthereum());
            }
            wallet = builder.build();

            walletRepository.save(wallet);

            /*
             * important!!  why? -> immediately save to DB
             * flush managed entities to the database to populate identifier field
             */
//            entityManager.flush();

            /*
             * remove managed entities from the persistence context
             * so that subsequent SQL queries hit the database
             */
//            entityManager.clear();
        } else {
            throw new BusinessException(Code.WALLET_ALREADY_EXISTS);
        }

        return wallet;
    }

    public BigDecimal getAllSoldPonyBalance() {
        BigDecimal allSoldPonyBalance = this.walletRepository.getAllSoldPonyBalance();
        return allSoldPonyBalance;
    }
}
