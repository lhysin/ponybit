package io.exchange.core.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.exchange.core.config.CoreConfig;
import io.exchange.core.dto.CoinDto.ResCoin;
import io.exchange.core.hibernate.repository.AdminWalletRepository;
import io.exchange.core.util.KeyGenUtils;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.Phase;
import io.exchange.domain.enums.WalletType;
import io.exchange.domain.hibernate.coin.AdminWallet;
import io.exchange.domain.util.EnumUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class AdminWalletService {

    private final CoinService coinService;

    private final AdminWalletRepository adminWalletRepository;

    public Optional<AdminWallet> get(CoinName coinName, WalletType type) {
        return this.adminWalletRepository.findByCoinNameAndType(coinName, type);
    }

    @Transactional
    public void createDefaultAdminWalletIfNotFound() {
        List<ResCoin> resCoinList = this.coinService.getAllCoin();
        for (ResCoin resCoin : resCoinList) {
            CoinName coinName = resCoin.getName();
            for (WalletType type : WalletType.values()) {
                AdminWallet adminWallet = this.get(coinName, type).orElse(null);
                if(adminWallet == null) {

                    String address = null;
                    if(EnumUtils.isEqual(CoinName.BITCOIN, coinName)) {
                        if(CoreConfig.currentPhase.isEquals(Phase.PRD)){
                            address = CoreConfig.PRD_ADMIN_BITCOIN_ADDRESS;
                        } else {
                            address = KeyGenUtils.generateKey(34);
                        }
                    }
                    if(EnumUtils.isEqual(CoinName.ETHEREUM, coinName)) {
                        if(EnumUtils.isEqual(WalletType.HOT, type)) {
                            if(CoreConfig.currentPhase.isEquals(Phase.PRD)){
                                // mainnet ethereum binance1 wallet address for test
                                address = CoreConfig.PRD_ADMIN_ETHEREUM_ADDRESS;
                            } else {
                                // ropsten ethereum transaction 18k..
                                address = "0x81a69786E0776443B391BF1f2EC5C0ab3da4Ff02";
                                // ropsten ethereum transaction 0.1k..
                                address = "0x8A503A162E58B179C4aEB0375F0e9160cfA818b2 ";
                            }
                        } else {
                            address = KeyGenUtils.generateKey(40);
                        }
                    }
                    if(EnumUtils.isEqual(CoinName.PONYCOIN, coinName)) {
                        address = KeyGenUtils.generateKey(40);
                    }

                    adminWallet = AdminWallet.builder()
                            .coinName(coinName)
                            .type(type)
                            .regIp(StaticUtils.getCurrentIp())
                            .regDtm(LocalDateTime.now())
                            .address(address)
                            .availableBalance(new BigDecimal(KeyGenUtils.generateNumericKey(7)))
                            .build();
                    this.adminWalletRepository.save(adminWallet);
                }
            }
        }
    }
}
