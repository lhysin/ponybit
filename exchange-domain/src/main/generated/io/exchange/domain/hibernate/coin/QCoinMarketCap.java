package io.exchange.domain.hibernate.coin;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCoinMarketCap is a Querydsl query type for CoinMarketCap
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCoinMarketCap extends EntityPathBase<CoinMarketCap> {

    private static final long serialVersionUID = 629387051L;

    public static final QCoinMarketCap coinMarketCap = new QCoinMarketCap("coinMarketCap");

    public final StringPath availableSupply = createString("availableSupply");

    public final EnumPath<io.exchange.domain.enums.CoinName> coinName = createEnum("coinName", io.exchange.domain.enums.CoinName.class);

    public final StringPath lastUpdated = createString("lastUpdated");

    public final StringPath marketCapKrw = createString("marketCapKrw");

    public final StringPath marketCapUsd = createString("marketCapUsd");

    public final StringPath maxSupply = createString("maxSupply");

    public final StringPath percentChange1h = createString("percentChange1h");

    public final StringPath percentChange24h = createString("percentChange24h");

    public final StringPath percentChange7d = createString("percentChange7d");

    public final StringPath priceBtc = createString("priceBtc");

    public final StringPath priceKrw = createString("priceKrw");

    public final StringPath priceUsd = createString("priceUsd");

    public final StringPath rank = createString("rank");

    public final DateTimePath<java.time.LocalDateTime> regDtm = createDateTime("regDtm", java.time.LocalDateTime.class);

    public final StringPath regIp = createString("regIp");

    public final StringPath symbol = createString("symbol");

    public final StringPath totalSupply = createString("totalSupply");

    public QCoinMarketCap(String variable) {
        super(CoinMarketCap.class, forVariable(variable));
    }

    public QCoinMarketCap(Path<? extends CoinMarketCap> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCoinMarketCap(PathMetadata metadata) {
        super(CoinMarketCap.class, metadata);
    }

}

