package io.exchange.domain.hibernate.coin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import io.exchange.domain.enums.Active;
import io.exchange.domain.enums.CoinName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class Coin implements Serializable {

    @Id
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CoinName name;

    private String hanName;

    private String mark;
    private String unit;
    private Long displayPriority;

    private String logoUrl;

    @Column(nullable=false, columnDefinition="char(1)")
    @Enumerated(EnumType.STRING)
    private Active active;

    private BigDecimal withdrawalMinAmount;
    private BigDecimal withdrawalAutoAllowMaxAmount;
    private BigDecimal withdrawalFeeAmount;

    private BigDecimal autoCollectMinAmount;

    private BigDecimal tradingFeePercent;
    private BigDecimal tradingMinAmount;

    private BigDecimal marginTradingFeePercent;

    private Integer depositScanPageOffset;
    private Integer depositScanPageSize;

    private Long depositAllowConfirmation;

    @OneToMany(mappedBy="coin", fetch=FetchType.LAZY)
    private Collection<Wallet> wallets;

    @OneToMany(mappedBy="coin", fetch=FetchType.LAZY)
    private Collection<AdminWallet> adminWallets;

    @Column(nullable=false)
    private String regIp;

    @Column(nullable=false)
    private LocalDateTime regDtm;

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements CoinNameEnum, ActiveEnum, RegIp, RegDtm {
        CoinBuilder builder = Coin.privateBuilder();

        @Override
        public ActiveEnum name(CoinName name) {
            builder.name(name);
            return this;
        }

        @Override
        public RegIp active(Active active) {
            builder.active(active);
            return this;
        }

        @Override
        public RegDtm regIp(String regIp) {
            builder.regIp(regIp);
            return this;
        }

        @Override
        public CoinBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }
    }

    public static CoinNameEnum builder() {
        return new SafeBuilder();
    }

    public interface CoinNameEnum {
        ActiveEnum name(CoinName name);
    }
    public interface ActiveEnum {
        RegIp active(Active active);
    }
    public interface RegIp {
        RegDtm regIp(String regIp);
    }
    public interface RegDtm {
        CoinBuilder regDtm(LocalDateTime regDtm);
    }
}
