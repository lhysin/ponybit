package io.exchange.domain.hibernate.coin;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEtherscanTransaction is a Querydsl query type for EtherscanTransaction
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QEtherscanTransaction extends EntityPathBase<EtherscanTransaction> {

    private static final long serialVersionUID = 1242322901L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEtherscanTransaction etherscanTransaction = new QEtherscanTransaction("etherscanTransaction");

    public final StringPath blockHash = createString("blockHash");

    public final NumberPath<Long> blockNumber = createNumber("blockNumber", Long.class);

    public final QCoin coin;

    public final EnumPath<io.exchange.domain.enums.CoinName> coinName = createEnum("coinName", io.exchange.domain.enums.CoinName.class);

    public final NumberPath<Long> confirmations = createNumber("confirmations", Long.class);

    public final StringPath contractAddress = createString("contractAddress");

    public final NumberPath<java.math.BigDecimal> cumulativeGasUsed = createNumber("cumulativeGasUsed", java.math.BigDecimal.class);

    public final StringPath fromAddress = createString("fromAddress");

    public final NumberPath<java.math.BigDecimal> gas = createNumber("gas", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> gasPrice = createNumber("gasPrice", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> gasUsed = createNumber("gasUsed", java.math.BigDecimal.class);

    public final StringPath hash = createString("hash");

    public final StringPath isError = createString("isError");

    public final NumberPath<Long> nonce = createNumber("nonce", Long.class);

    public final DateTimePath<java.time.LocalDateTime> regDtm = createDateTime("regDtm", java.time.LocalDateTime.class);

    public final StringPath regIp = createString("regIp");

    public final NumberPath<Integer> searchOffset = createNumber("searchOffset", Integer.class);

    public final NumberPath<Integer> searchPage = createNumber("searchPage", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> timeStampDtm = createDateTime("timeStampDtm", java.time.LocalDateTime.class);

    public final StringPath toAddress = createString("toAddress");

    public final NumberPath<Long> transactionIndex = createNumber("transactionIndex", Long.class);

    public final StringPath txreceiptStatus = createString("txreceiptStatus");

    public final NumberPath<java.math.BigDecimal> value = createNumber("value", java.math.BigDecimal.class);

    public QEtherscanTransaction(String variable) {
        this(EtherscanTransaction.class, forVariable(variable), INITS);
    }

    public QEtherscanTransaction(Path<? extends EtherscanTransaction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEtherscanTransaction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEtherscanTransaction(PathMetadata metadata, PathInits inits) {
        this(EtherscanTransaction.class, metadata, inits);
    }

    public QEtherscanTransaction(Class<? extends EtherscanTransaction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coin = inits.isInitialized("coin") ? new QCoin(forProperty("coin")) : null;
    }

}

