package io.exchange.domain.enums;

public enum UserLevel {

    LEVEL0(0, "None Verification"),
    LEVEL1(1, "Email Verification"),
    LEVEL2(2, "KakaoTalk Verification"),
    LEVEL3(3, "OTP Verification");

    private Integer level;
    private String detail;

    UserLevel(Integer level, String detail) {
        this.level = level;
        this.detail = detail;
    }

    public Integer getLevel() {
        return level;
    }
    public String getDetail() {
        return detail;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }

}
