package io.exchange.domain.hibernate.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QLevel is a Querydsl query type for Level
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLevel extends EntityPathBase<Level> {

    private static final long serialVersionUID = 199613444L;

    public static final QLevel level = new QLevel("level");

    public final EnumPath<io.exchange.domain.enums.CoinName> coinName = createEnum("coinName", io.exchange.domain.enums.CoinName.class);

    public final EnumPath<io.exchange.domain.enums.LevelEnum> levelEnum = createEnum("levelEnum", io.exchange.domain.enums.LevelEnum.class);

    public final NumberPath<java.math.BigDecimal> onceAmount = createNumber("onceAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> onedayAmount = createNumber("onedayAmount", java.math.BigDecimal.class);

    public final DateTimePath<java.time.LocalDateTime> regDtm = createDateTime("regDtm", java.time.LocalDateTime.class);

    public final StringPath regIp = createString("regIp");

    public QLevel(String variable) {
        super(Level.class, forVariable(variable));
    }

    public QLevel(Path<? extends Level> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLevel(PathMetadata metadata) {
        super(Level.class, metadata);
    }

}

