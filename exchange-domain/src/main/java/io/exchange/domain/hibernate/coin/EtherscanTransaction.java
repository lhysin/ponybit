package io.exchange.domain.hibernate.coin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.exchange.domain.enums.CoinName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@IdClass(EtherscanTransaction.Pk.class)
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class EtherscanTransaction implements Serializable {

    @Builder
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor(access=AccessLevel.PRIVATE)
    @NoArgsConstructor(access=AccessLevel.PACKAGE)
    public static class Pk implements Serializable {
        private String hash;
        private CoinName coinName;
    }

    @Id
    @Column(name="hash", nullable = false)
    private String hash;

    @Id
    @Column(name="coin_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private CoinName coinName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="coin_name", insertable=false, updatable=false, referencedColumnName="name")
    private Coin coin;

    @Column(precision=37, scale=18)
    private BigDecimal value;

    private Long blockNumber;
    private LocalDateTime timeStampDtm;
    private String fromAddress;
    private String toAddress;
    private String contractAddress;
    private BigDecimal gas;
    private BigDecimal gasUsed;
    private Long nonce;
    private String blockHash;
    private Long transactionIndex;
    private BigDecimal gasPrice;
    private BigDecimal cumulativeGasUsed;
    private Long confirmations;
    private String isError;
    private String txreceiptStatus;
    private Integer searchPage;
    private Integer searchOffset;

    @Column(nullable=false)
    private String regIp;

    @Column(nullable=false)
    private LocalDateTime regDtm;

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements Hash, CoinNameEnum, RegIp, RegDtm {
        EtherscanTransactionBuilder builder = EtherscanTransaction.privateBuilder();

        @Override
        public CoinNameEnum hash(String hash) {
            builder.hash(hash);
            return this;
        }

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
        public EtherscanTransactionBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }
    }

    public static Hash builder() {
        return new SafeBuilder();
    }

    public interface Hash {
        CoinNameEnum hash(String hash);
    }
    public interface CoinNameEnum {
    	RegIp coinName(CoinName coinName);
    }
    public interface RegIp {
        RegDtm regIp(String regIp);
    }
    public interface RegDtm {
        EtherscanTransactionBuilder regDtm(LocalDateTime regDtm);
    }
}
