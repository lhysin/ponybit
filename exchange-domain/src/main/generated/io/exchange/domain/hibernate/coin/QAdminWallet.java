package io.exchange.domain.hibernate.coin;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAdminWallet is a Querydsl query type for AdminWallet
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAdminWallet extends EntityPathBase<AdminWallet> {

    private static final long serialVersionUID = 389084782L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAdminWallet adminWallet = new QAdminWallet("adminWallet");

    public final StringPath address = createString("address");

    public final NumberPath<java.math.BigDecimal> availableBalance = createNumber("availableBalance", java.math.BigDecimal.class);

    public final StringPath bankCode = createString("bankCode");

    public final StringPath bankName = createString("bankName");

    public final QCoin coin;

    public final EnumPath<io.exchange.domain.enums.CoinName> coinName = createEnum("coinName", io.exchange.domain.enums.CoinName.class);

    public final StringPath recvCorpNm = createString("recvCorpNm");

    public final DateTimePath<java.time.LocalDateTime> regDtm = createDateTime("regDtm", java.time.LocalDateTime.class);

    public final StringPath regIp = createString("regIp");

    public final StringPath tag = createString("tag");

    public final EnumPath<io.exchange.domain.enums.WalletType> type = createEnum("type", io.exchange.domain.enums.WalletType.class);

    public final NumberPath<java.math.BigDecimal> usingBalance = createNumber("usingBalance", java.math.BigDecimal.class);

    public QAdminWallet(String variable) {
        this(AdminWallet.class, forVariable(variable), INITS);
    }

    public QAdminWallet(Path<? extends AdminWallet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAdminWallet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAdminWallet(PathMetadata metadata, PathInits inits) {
        this(AdminWallet.class, metadata, inits);
    }

    public QAdminWallet(Class<? extends AdminWallet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coin = inits.isInitialized("coin") ? new QCoin(forProperty("coin")) : null;
    }

}

