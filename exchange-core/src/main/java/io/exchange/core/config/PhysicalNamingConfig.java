package io.exchange.core.config;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

@SuppressWarnings("serial")
public class PhysicalNamingConfig extends PhysicalNamingStrategyStandardImpl {

    /**
     * TABLE NAME PATTERN
     *   User.class -> USERS
     *   UserVerify.class -> USER_VERIFY
     * 
     * COLUMN NAME PATTERN
     *   String id -> id
     *   String regDtm; -> reg_dtm
     */
    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {

        String tableName = addUnderscores(name.getText());
        String tableNamePostFix = "s";

        if(!StringUtils.contains(tableName, CoreConfig.DOT)) {
            if(!StringUtils.contains(tableName, CoreConfig.UNDER_SCORE)) {
                tableName += tableNamePostFix;
            }
        }

        return new Identifier(StringUtils.lowerCase(tableName), name.isQuoted());
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        return new Identifier(addUnderscores(name.getText()), name.isQuoted());
    }

    protected static String addUnderscores(String name) {
        final StringBuilder buf = new StringBuilder(
                name.replace(CoreConfig.DOT, CoreConfig.UNDER_SCORE));
        for (int i = 1; i < buf.length() - 1; i++) {
            if (Character.isLowerCase(buf.charAt(i - 1)) && Character.isUpperCase(buf.charAt(i))
                    && Character.isLowerCase(buf.charAt(i + 1))) {
                buf.insert(i++, CoreConfig.UNDER_SCORE);
            }
        }
        return buf.toString().toLowerCase(Locale.ROOT);
    }
}