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
import javax.persistence.OneToOne;

import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.WalletType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@IdClass(AdminWallet.Pk.class)
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class AdminWallet implements Serializable {

    @Builder
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor(access=AccessLevel.PRIVATE)
    @NoArgsConstructor(access=AccessLevel.PACKAGE)
    public static class Pk implements Serializable {
        private CoinName coinName;
        private WalletType type;
    }

    @Id
    @Column(name="coin_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private CoinName coinName;

    @Id
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WalletType type;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="coin_name", insertable=false, updatable=false, referencedColumnName="name")
    private Coin coin;

    private String address;
    private String tag;

    private String bankName;
    private String bankCode;
    private String recvCorpNm;

    @Column(precision=27, scale=8)
    private BigDecimal usingBalance;

    @Column(precision=27, scale=8)
    private BigDecimal availableBalance;

    public void usingBalanceAdd(BigDecimal amount) {
        this.usingBalance.add(amount);
    }

    public void usingBalanceSubtract(BigDecimal amount) {
        this.usingBalance.subtract(amount);
    }

    public void availableBalanceAdd(BigDecimal amount) {
        this.availableBalance.add(amount);
    }

    public void availableBalanceSubtract(BigDecimal amount) {
        this.availableBalance.subtract(amount);
    }

    @Column(nullable=false)
    private String regIp;

    @Column(nullable=false)
    private LocalDateTime regDtm;

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements CoinNameEnum, WalletTypeEnum, RegIp, RegDtm {
        AdminWalletBuilder builder = AdminWallet.privateBuilder();

        @Override
        public WalletTypeEnum coinName(CoinName coinName) {
            builder.coinName(coinName);
            return this;
        }

        @Override
        public RegIp type(WalletType type) {
            builder.type(type);
            return this;
        }

        @Override
        public RegDtm regIp(String regIp) {
            builder.regIp(regIp);
            return this;
        }

        @Override
        public AdminWalletBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }
    }

    public static CoinNameEnum builder() {
        return new SafeBuilder();
    }

    public interface CoinNameEnum {
        WalletTypeEnum coinName(CoinName coinName);
    }
    public interface WalletTypeEnum {
        RegIp type(WalletType type);
    }
    public interface RegIp {
        RegDtm regIp(String regIp);
    }
    public interface RegDtm {
        AdminWalletBuilder regDtm(LocalDateTime regDtm);
    }
}
