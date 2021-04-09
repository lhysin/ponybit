package io.exchange.core.provider.feign;

import java.util.List;
import java.util.Map;

import feign.Param;
import feign.RequestLine;
import lombok.Data;

public interface EtherScanProvider {

    @Data
    public static class Result {
        private String status;
        private String message;
        private List<Map<String, String>> result;
    }

    /*
     *  @RequestLine("GET /domains/{domainId}/records?name={name}&{extra}")
     *	Response recordsByNameAndType(@Param("domainId") int id, @Param("name") String nameFilter,
                                  @Param("extra") Map<String, String> options);
     * */

    @RequestLine("GET /api?module=account&action=txlist&startblock=0&endblock=9999999999999&sort=desc&apikey=7M857NH86IV4W97SWRR75BERWCV5YWUABS"
        + "&address={address}&page={page}&offset={offset}")
    EtherScanProvider.Result listTransactionByAddress(@Param("address") String address, @Param("page") Integer page, @Param("offset") Integer offset);
}