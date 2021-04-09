package io.exchange.domain.hibernate.coin;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;

import io.exchange.domain.enums.CoinName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@IdClass(CoinMarketCap.Pk.class)
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class CoinMarketCap implements Serializable {

    @Builder
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor(access=AccessLevel.PRIVATE)
    @NoArgsConstructor(access=AccessLevel.PACKAGE)
    public static class Pk implements Serializable {
        private LocalDateTime regDtm;
        private CoinName coinName;
    }

    @Id
    @Column(name="coin_name", nullable=false)
    @Enumerated(EnumType.STRING)
    private CoinName coinName;

    private String symbol;
    private String rank;
    private String percentChange24h;
    private String priceKrw;
    private String priceUsd;
    private String priceBtc;
    private String marketCapUsd;
    private String availableSupply;
    private String totalSupply;
    private String maxSupply;
    private String percentChange1h;
    private String percentChange7d;
    private String lastUpdated;
    private String marketCapKrw;

    @Column(nullable=false)
    private String regIp;

    @Id
    @Column(nullable=false)
    private LocalDateTime regDtm;

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements CoinNameEnum, RegIp, RegDtm {
       CoinMarketCapBuilder builder = CoinMarketCap.privateBuilder();

        @Override
        public RegIp coinName(CoinName coinName) {
            builder.coinName(coinName);
            return this;
        }

        @Override
        public RegDtm regIp(String regIp) {
            builder.regIp(regIp);
            return this;
        }

        @Override
        public CoinMarketCapBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }

    }

    public static CoinNameEnum builder() {
        return new SafeBuilder();
    }
    public interface CoinNameEnum {
        RegIp coinName(CoinName coinName);
    }
    public interface RegIp {
        RegDtm regIp(String regIp);
    }
    public interface RegDtm {
        CoinMarketCapBuilder regDtm(LocalDateTime regDtm);
    }
}
