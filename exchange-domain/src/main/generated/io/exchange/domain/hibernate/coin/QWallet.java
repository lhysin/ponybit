package io.exchange.domain.hibernate.coin;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWallet is a Querydsl query type for Wallet
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWallet extends EntityPathBase<Wallet> {

    private static final long serialVersionUID = -340732429L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWallet wallet = new QWallet("wallet");

    public final StringPath address = createString("address");

    public final NumberPath<java.math.BigDecimal> availableBalance = createNumber("availableBalance", java.math.BigDecimal.class);

    public final StringPath bankCode = createString("bankCode");

    public final StringPath bankName = createString("bankName");

    public final QCoin coin;

    public final EnumPath<io.exchange.domain.enums.CoinName> coinName = createEnum("coinName", io.exchange.domain.enums.CoinName.class);

    public final StringPath depositDvcd = createString("depositDvcd");

    public final StringPath recvCorpNm = createString("recvCorpNm");

    public final DateTimePath<java.time.LocalDateTime> regDtm = createDateTime("regDtm", java.time.LocalDateTime.class);

    public final StringPath regIp = createString("regIp");

    public final StringPath tag = createString("tag");

    public final NumberPath<java.math.BigDecimal> todayWithdrawalTotalBalance = createNumber("todayWithdrawalTotalBalance", java.math.BigDecimal.class);

    public final io.exchange.domain.hibernate.user.QUser user;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final NumberPath<java.math.BigDecimal> usingBalance = createNumber("usingBalance", java.math.BigDecimal.class);

    public QWallet(String variable) {
        this(Wallet.class, forVariable(variable), INITS);
    }

    public QWallet(Path<? extends Wallet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWallet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWallet(PathMetadata metadata, PathInits inits) {
        this(Wallet.class, metadata, inits);
    }

    public QWallet(Class<? extends Wallet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coin = inits.isInitialized("coin") ? new QCoin(forProperty("coin")) : null;
        this.user = inits.isInitialized("user") ? new io.exchange.domain.hibernate.user.QUser(forProperty("user")) : null;
    }

}

