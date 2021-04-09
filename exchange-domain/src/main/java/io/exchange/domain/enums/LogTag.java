package io.exchange.domain.enums;

public enum LogTag {

    // default login
    LOGIN,

    // default login process with remember me
    LOGIN_AND_REMEMBER_ME,

    // login by remember me
    LOGIN_BY_REMEMBER_ME,

    // default logout
    LOGOUT,

    // session expired logout
    LOGOUT_BY_SESSION,

    BATCH_EXCEPTION,

    ERROR
}
