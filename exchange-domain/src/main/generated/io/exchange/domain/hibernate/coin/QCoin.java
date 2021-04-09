package io.exchange.domain.hibernate.coin;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCoin is a Querydsl query type for Coin
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCoin extends EntityPathBase<Coin> {

    private static final long serialVersionUID = -921606389L;

    public static final QCoin coin = new QCoin("coin");

    public final EnumPath<io.exchange.domain.enums.Active> active = createEnum("active", io.exchange.domain.enums.Active.class);

    public final CollectionPath<AdminWallet, QAdminWallet> adminWallets = this.<AdminWallet, QAdminWallet>createCollection("adminWallets", AdminWallet.class, QAdminWallet.class, PathInits.DIRECT2);

    public final NumberPath<java.math.BigDecimal> autoCollectMinAmount = createNumber("autoCollectMinAmount", java.math.BigDecimal.class);

    public final NumberPath<Long> depositAllowConfirmation = createNumber("depositAllowConfirmation", Long.class);

    public final NumberPath<Integer> depositScanPageOffset = createNumber("depositScanPageOffset", Integer.class);

    public final NumberPath<Integer> depositScanPageSize = createNumber("depositScanPageSize", Integer.class);

    public final NumberPath<Long> displayPriority = createNumber("displayPriority", Long.class);

    public final StringPath hanName = createString("hanName");

    public final StringPath logoUrl = createString("logoUrl");

    public final NumberPath<java.math.BigDecimal> marginTradingFeePercent = createNumber("marginTradingFeePercent", java.math.BigDecimal.class);

    public final StringPath mark = createString("mark");

    public final EnumPath<io.exchange.domain.enums.CoinName> name = createEnum("name", io.exchange.domain.enums.CoinName.class);

    public final DateTimePath<java.time.LocalDateTime> regDtm = createDateTime("regDtm", java.time.LocalDateTime.class);

    public final StringPath regIp = createString("regIp");

    public final NumberPath<java.math.BigDecimal> tradingFeePercent = createNumber("tradingFeePercent", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> tradingMinAmount = createNumber("tradingMinAmount", java.math.BigDecimal.class);

    public final StringPath unit = createString("unit");

    public final CollectionPath<Wallet, QWallet> wallets = this.<Wallet, QWallet>createCollection("wallets", Wallet.class, QWallet.class, PathInits.DIRECT2);

    public final NumberPath<java.math.BigDecimal> withdrawalAutoAllowMaxAmount = createNumber("withdrawalAutoAllowMaxAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> withdrawalFeeAmount = createNumber("withdrawalFeeAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> withdrawalMinAmount = createNumber("withdrawalMinAmount", java.math.BigDecimal.class);

    public QCoin(String variable) {
        super(Coin.class, forVariable(variable));
    }

    public QCoin(Path<? extends Coin> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCoin(PathMetadata metadata) {
        super(Coin.class, metadata);
    }

}

