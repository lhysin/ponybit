package io.exchange.domain.hibernate.coin;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTransaction is a Querydsl query type for Transaction
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTransaction extends EntityPathBase<Transaction> {

    private static final long serialVersionUID = -1374782172L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTransaction transaction = new QTransaction("transaction");

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final StringPath bankNm = createString("bankNm");

    public final EnumPath<io.exchange.domain.enums.Category> category = createEnum("category", io.exchange.domain.enums.Category.class);

    public final QCoin coin;

    public final EnumPath<io.exchange.domain.enums.CoinName> coinName = createEnum("coinName", io.exchange.domain.enums.CoinName.class);

    public final DateTimePath<java.time.LocalDateTime> completeDtm = createDateTime("completeDtm", java.time.LocalDateTime.class);

    public final NumberPath<Long> confirmation = createNumber("confirmation", Long.class);

    public final StringPath fromAddress = createString("fromAddress");

    public final StringPath reason = createString("reason");

    public final StringPath recvNm = createString("recvNm");

    public final DateTimePath<java.time.LocalDateTime> regDtm = createDateTime("regDtm", java.time.LocalDateTime.class);

    public final StringPath regIp = createString("regIp");

    public final EnumPath<io.exchange.domain.enums.Status> status = createEnum("status", io.exchange.domain.enums.Status.class);

    public final StringPath tag = createString("tag");

    public final StringPath toAddress = createString("toAddress");

    public final StringPath txId = createString("txId");

    public final io.exchange.domain.hibernate.user.QUser user;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QTransaction(String variable) {
        this(Transaction.class, forVariable(variable), INITS);
    }

    public QTransaction(Path<? extends Transaction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTransaction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTransaction(PathMetadata metadata, PathInits inits) {
        this(Transaction.class, metadata, inits);
    }

    public QTransaction(Class<? extends Transaction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coin = inits.isInitialized("coin") ? new QCoin(forProperty("coin")) : null;
        this.user = inits.isInitialized("user") ? new io.exchange.domain.hibernate.user.QUser(forProperty("user")) : null;
    }

}

