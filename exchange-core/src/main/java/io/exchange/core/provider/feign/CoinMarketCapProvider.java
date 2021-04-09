package io.exchange.core.provider.feign;

import java.util.List;

import feign.RequestLine;
import lombok.Data;

public interface CoinMarketCapProvider {

    @Data
    public static class Ticker {
        private String id;
        private String name;
        private String symbol;
        private String rank;
        private String percent_change_24h;
        private String price_krw;
        private String price_usd;
        private String price_btc;
        private String market_cap_usd;
        private String available_supply;
        private String total_supply;
        private String max_supply;
        private String percent_change_1h;
        private String percent_change_7d;
        private String last_updated;
        private String market_cap_krw;
    }

    /*
     *  @RequestLine("GET /domains/{domainId}/records?name={name}&{extra}")
     *	Response recordsByNameAndType(@Param("domainId") int id, @Param("name") String nameFilter,
                                  @Param("extra") Map<String, String> options);
     * */

    @RequestLine("GET /v1/ticker/?convert=KRW&limit=10")
    List<Ticker> getTop10Tiker();

    @RequestLine("GET /v1/ticker/ethereum?convert=KRW&limit=10")
    List<Ticker> getEthereumTiker();
}