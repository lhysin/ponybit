package io.exchange.core.config;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.exchange.domain.enums.Phase;


@Configuration("coreConfig")
public class CoreConfig implements EnvironmentAware {

    public static final String SERVER_DEFAULT_IP = "0.0.0.0";
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String DOT = ".";
    public static final String UNDER_SCORE = "_";
    public static final String AMPERSAND = "&";
    public static final String EQUALS = "=";

    public static final BigDecimal MAX_SALE_PONY_BALANCE = new BigDecimal("2500000000");
    public static final BigDecimal PONY_KRW_PRICE = new BigDecimal("0.5");

    public static final BigDecimal AUTH_PROMOTION_PONY_BALANCE = new BigDecimal("10000");
    public static final BigDecimal REF_PROMOTION_PONY_BALANCE = new BigDecimal("1000");

    public static final String PRD_ADMIN_BITCOIN_ADDRESS = "16t8fJadir7bftRCPdcu4syK8Lroykre4C";
    public static final String PRD_ADMIN_ETHEREUM_ADDRESS = "0x86d97b3763ee745009d72ed08f77bc92a28fa162";

    public static final TimeZone DEFAILT_TIME_ZONE = TimeZone.getTimeZone("Asia/Seoul");

    // client key
    public static final String REMEMBER_ME_KEY = "remember_me_key";
    public static final String HOME_REF_MODAL_NOT_OPEN_KEY = "home_ref_modal_not_open_key";

    /**
     * application load after available.
     */
    public static Environment env;
    public static Phase currentPhase;
//    public static int httpPort;
//    public static int httpsPort;
    public static String emailNoreply;
    public static String emailAdmin;
    public static boolean isH2File;

    @SuppressWarnings(value = "static-access")
    @Override
    public void setEnvironment(Environment env) {

        this.env = env;

        this.currentPhase = Arrays.stream(Phase.values())
                .filter(phase -> phase.isEqual(env.getProperty("spring.profiles.active")))
                .findFirst()
                .orElse(Phase.LOCAL);

        this.emailNoreply = Optional.ofNullable(env.getProperty("email.noreplay"))
                .orElse("noreplay@ponybit.io");

        this.emailAdmin = Optional.ofNullable(env.getProperty("email.admin"))
                .orElse("admin@ponybit.io");

        // file DB available
        // vm arguments -Dh2.isFileDb=true
        if(StringUtils.equals(env.getProperty("h2.isFileDb"), "true")) {
            this.isH2File = true;
        } else {
            this.isH2File = false;
        }
    }
}
