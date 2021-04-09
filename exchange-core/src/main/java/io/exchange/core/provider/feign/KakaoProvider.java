package io.exchange.core.provider.feign;

import java.util.Map;

import feign.Body;
import feign.HeaderMap;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import lombok.Data;

public interface KakaoProvider {

    @Data
    public static class OauthResult {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private String expires_in;
        private String scope;
    }

    @Data
    public static class KakaoTalkResult {
        private String nickName;
        private String profileImageURL;
        private String thumbnailURL;
        private String countryISO;
    }

    @Data
    public static class KakaoMeResult {
        private String id;
    }

    /*
     *  @RequestLine("GET /domains/{domainId}/records?name={name}&{extra}")
     *	Response recordsByNameAndType(@Param("domainId") int id, @Param("name") String nameFilter,
                                  @Param("extra") Map<String, String> options);
     * */


    // kakao access token
    @RequestLine("POST /oauth/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("client_id=b0deaef9cc7c745e1b222d33d8adbfbd&redirect_uri={baseUrl}/continue&grant_type=authorization_code&code={code}")
    KakaoProvider.OauthResult getKakaoOuthToken(@Param("baseUrl") String baseUrl, @Param("code") String code);

    // check kakao talk member
    @RequestLine("GET /v1/api/talk/profile")
    @Headers("Authorization: Bearer {accessToken}")
    KakaoProvider.KakaoTalkResult getKakaoTalkProfile(@Param("accessToken") String accessToken);

    // check kakao user logout
    @RequestLine("POST /v1/user/logout")
    @Headers("Authorization: Bearer {accessToken}")
    void logoutKakao(@Param("accessToken") String accessToken);

    // define unique app user id
    @RequestLine("GET /v2/user/me")
    @Headers("Authorization: Bearer {accessToken}")
    KakaoProvider.KakaoMeResult getKakaoAppUserInfo(@Param("accessToken") String accessToken);

    @RequestLine("POST /v2/api/talk/memo/default/send")
    @Body("template_object={templateObjectJsonStr}")
    void sendKakaoMsg(@HeaderMap Map<String, Object> headerMap, @Param("templateObjectJsonStr") String templateObjectJsonStr);

}