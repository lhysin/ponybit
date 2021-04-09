package io.exchange.domain.hibernate.coin;

import java.io.Serializable;
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
import javax.persistence.Transient;

import io.exchange.domain.enums.CoinName;
import io.exchange.domain.hibernate.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@IdClass(Wallet.Pk.class)
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class Wallet implements Serializable {

    @Builder
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor(access=AccessLevel.PRIVATE)
    @NoArgsConstructor(access=AccessLevel.PACKAGE)
    public static class Pk implements Serializable {
        private Long userId;
        private CoinName coinName;
    }

    @Id
    @Column(name="user_id", nullable=false)
    private Long userId;

    @Id
    @Column(name="coin_name", nullable=false)
    @Enumerated(EnumType.STRING)
    private CoinName coinName;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", insertable=false, updatable=false, referencedColumnName="id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="coin_name", insertable=false, updatable=false, referencedColumnName="name")
    private Coin coin;

    private String address;
    private String bankName;
    private String bankCode;
    private String recvCorpNm;
    private String tag;
    private String depositDvcd;

    @Column(precision=27, scale=8)
    private BigDecimal usingBalance;

    @Column(precision=27, scale=8)
    private BigDecimal availableBalance;

    @Column(precision=27, scale=8)
    private BigDecimal todayWithdrawalTotalBalance;

    @Transient
    private String realBalance;

    @Column(nullable=false)
    private String regIp;

    @Column(nullable=false)
    private LocalDateTime regDtm;

    @Transient
    public BigDecimal getTotalBalance() {
        return usingBalance.add(availableBalance);
    }

    public void usingBalanceAdd(BigDecimal amount) {
        this.usingBalance = this.usingBalance.add(amount);
    }

    public void usingBalanceSubtract(BigDecimal amount) {
        this.usingBalance = this.usingBalance.subtract(amount);
    }

    public void availableBalanceAdd(BigDecimal amount) {
        this.availableBalance = this.availableBalance.add(amount);
    }

    public void availableBalanceSubtract(BigDecimal amount) {
        this.availableBalance = this.availableBalance.subtract(amount);
    }

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements UserId, CoinNameEnum, RegIp, RegDtm {
        WalletBuilder builder = Wallet.privateBuilder();

        @Override
        public CoinNameEnum userId(Long userId) {
            builder.userId(userId);
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
        public WalletBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }
    }

    public static UserId builder() {
        return new SafeBuilder();
    }

    public interface UserId {
        CoinNameEnum userId(Long userId);
    }
    public interface CoinNameEnum {
        RegIp coinName(CoinName coinName);
    }
    public interface RegIp {
        RegDtm regIp(String regIp);
    }
    public interface RegDtm {
        WalletBuilder regDtm(LocalDateTime regDtm);
    }
}
