package io.exchange.domain.enums;

import org.apache.commons.lang3.StringUtils;

public enum Phase {

    TEST("test"),
    LOCAL("local"),
    DEV("dev"),
    PRD("prd");

    private final String profile;

    Phase(String profile) {
        this.profile = profile;
    }

    public boolean isEqual(String phase) {
        return StringUtils.equals(this.getProfile(), phase);
    }

    public boolean isEqual(Phase phase) {
        return StringUtils.equals(this.getProfile(), phase.getProfile());
    }

    public boolean isEquals(Phase... phases) {
        boolean isEqual = false;
        for(Phase phase : phases) {
            if(StringUtils.equals(this.getProfile(), phase.getProfile())) {
                isEqual = true;
                break;
            }
        }
        return isEqual;
    }

    public String getProfile() {
        return this.profile;
    }
}