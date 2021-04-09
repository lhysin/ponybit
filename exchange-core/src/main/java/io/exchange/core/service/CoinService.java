package io.exchange.core.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;

import io.exchange.core.dto.CoinDto;
import io.exchange.core.dto.CoinDto.ResCoin;
import io.exchange.core.hibernate.repository.CoinRepository;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.Active;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.coin.Coin;
import io.exchange.domain.hibernate.coin.QCoin;
import io.exchange.domain.util.EnumUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinService {

    private final CoinRepository coinRepository;

    public Optional<Coin> get(CoinName coinName) {
        return coinRepository.findById(coinName);
    }

    public Coin getCoin(CoinName coinName){
        Coin coin = this.get(coinName).orElseThrow(() -> new BusinessException(Code.COIN_NOT_EXISTS, "coin_name:" + coinName));;
        return coin;
    }

    public Coin getActiveCoin(CoinName coinEnum) {

        Coin coin = coinRepository.findById(coinEnum)
                .orElseThrow(() -> new BusinessException(Code.COIN_NOT_EXISTS));

        if (EnumUtils.isNotEqual(coin.getActive(), Active.Y)) {
            throw new BusinessException(Code.COIN_NOT_ACTIVE);
        }

        return coin;
    }

    public List<Coin> getAll(){
        return this.coinRepository.findAll();
    }

    public List<ResCoin> getAllCoin(){
        QBean<ResCoin> coinBean = Projections.bean(ResCoin.class, QCoin.coin.name, QCoin.coin.hanName, QCoin.coin.mark,
                QCoin.coin.unit, QCoin.coin.displayPriority, QCoin.coin.logoUrl, QCoin.coin.regDtm);
        List<ResCoin> result = this.coinRepository.findAll(coinBean);
        return result;
    }

    public List<CoinDto.ResCoinWalletAdminWallet> getAllRegisteredCoin(Long userId) {

        List<CoinDto.ResCoinWalletAdminWallet> coins = this.coinRepository.getCreatedCoinAndUserWalletByUserId(userId);

        return coins;
    }

    public void createDefaultCoinsIfNotFound() {

        for (CoinName coinName : CoinName.values()) {
            Coin coin = this.get(coinName).orElse(null);
            if(coin == null) {

                String logoUrl = null;
                if(CoinName.BITCOIN.equals(coinName)) {
                    logoUrl = "https://en.bitcoin.it/w/images/en/6/69/Btc-sans.png";
                }
                if(CoinName.ETHEREUM.equals(coinName)) {
                    logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Ethereum_logo_2014.svg/800px-Ethereum_logo_2014.svg.png";
                }
                if (CoinName.KRW.equals(coinName)) {
                    continue;
                }

                coin = Coin.builder()
                        .name(coinName)
                        .active(Active.Y)
                        .regIp(StaticUtils.getCurrentIp())
                        .regDtm(LocalDateTime.now())
                        .logoUrl(logoUrl)
                        .build();
                coinRepository.save(coin);
            }
        }
    }
}
