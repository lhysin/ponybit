package io.exchange.domain.enums;

public enum Code {

    // common code
    SUCCESS(0000, "success"),
    FAIL(0001, "fail"),
    ALREADY(0002, "already"),
    TOO_MANY_REQUEST(0003, "too many request"),
    LOGIN_REQUIED(0004, "login requied"),

    EMAIL_SEND_FAIL(0010, "email send failure"),
    FEIGN_API_ERROR(0011, "feign api error"),

    // untouchable exception
    BAD_REQUEST(9999, "bad request"),

    // user code
    USER_LOGIN_FAIL(2000, "user login fail"),
    USER_ALREADY_EXISTS(2001, "user already exists"),
    USER_NOT_EXISTS(2002, "user not exist"),
    USER_ALREADY_EMAIL_VERIFICATION(2003, "user already eamil verification"),
    USER_NOT_EMAIL_VERIFICATION(2004, "user not eamil verification"),
    USER_NOT_KAKAO_TALK(2005, "user not kakao talk"),
    USER_NOT_KOREAN_KAKAO_TALK(2006, "user not korean kakao talk"),
    KAKAO_USER_ALREADY_EXISTS(2007, "kakao user already exists"),
    USER_ALREADY_KAKAO_VERIFICATION(2008, "user already kakao verification"),
    KAKAO_USER_NOT_KR(2009, "kakao user not kr"),
    TOKEN_INVALID(2010, "token is invalid"),
    TOKEN_EXPIRED(2011, "token is expired"),
    REF_NOT_EXISTS(2012, "refCd not exists"),
    REF_ALREADY_EXISTS(2013, "refCd alreay exists"),
    REF_CD_EQUALS(2014, "refCd is equals"),
    KAKAO_API_REQ_CNT_LIMIT(2015, "kakao api req count limit"),

    POST_NOT_EXISTS(2100, "post not exist"),

    COIN_ALREADY_EXISTS(3001, "coin already exist"),
    COIN_NOT_EXISTS(3002, "coin not exist"),
    COIN_ALREADY_ACTIVE(3003, "coin already active"),
    COIN_NOT_ACTIVE(3004, "coin not active"),

    WALLET_ALREADY_EXISTS(3011, "wallet already exist"),
    WALLET_NOT_EXISTS(3012, "wallet not exist"),
    WALLET_ALREADY_ACTIVE(3013, "wallet already active"),
    WALLET_NOT_ACTIVE(3014, "wallet not active"),
    TRANSACTION_ALREADY_EXISTS(3020, "transaction already exist"),
    TRANSACTION_NOT_EXISTS(3021, "transaction not exist"),
    TRANSACTION_IS_INVALID(3022, "transaction is invalid"),
    NOT_ENOUGH_ADMIN_WALLET(3023, "not enough admin wallet available balance."),

    CONSTANT_VALUE_IS_NULL(1003, "constant value is null"),


    USER_SETTING_NOT_EXISTS(5002, "user setting not exist"),

    AMOUNT_IS_UNDER_ZERO(4006, "amount is under 0"),
    ORDER_TYPE_INVALID(4007, "OrderType invalid."),
    NOT_ENOUGH_BALANCE(4008, "avaliable balance not enough"),
    INVALID_CONFIRM_CODE(4009, "invalid confirm code"),
    INVALID_EMAIL(4010, "invalid email"),
    ORDER_CANCEL_FAIL(4011, "order cancel fail"),
    INVALID_ORDER_TYPE(4012, "invalid order type"),
    ORDER_NOT_EXISTS(4013, "order not exist"),
    ORDER_STATUS_IS_NOT_PLACED(4014, "order status is not placed"),
    MIN_AMOUNT(4015, "order amount is under min amount"),
    NOT_SUPPORTED(4016, "not supported"),
    USER_FDS_LOCK(4017, "user fds lock"),
    ADMIN_WALLET_BALANCE_IS_UNDER_ZERO(4018, "admin wallet balance is under zero"),
    WALLET_UNLOCK_IS_FAIL(4019, "wallet unlock is fail"),
    DO_NOT_ALLOW_INNER_TRANSFER_WALLET(4020, "do not allow inner transfer wallet"),
    AMOUNT_IS_UNDER_MIN_AMOUNT(4021, "amount is under min amount"),
    ALREADY_SEND_PROCESS_RUNNING(4022, "already send process is running"),
    MANUAL_TRANSACTION_NOT_EXISTS(4023, "manual transaction not exist"),
    ADMIN_WALLET_NOT_EXISTS(4023, "admin wallet not exist"),
    ONLY_KRW_RECEIVED_REQUEST(4024, "only krw received request"),
    ONLY_KRW_SEND_REQUEST(4025, "only krw send request"),
    ALREADY_STATUS_IS_NOT_PENDING(4026, "already status is not pending"),
    NOT_ENOUGH_VIRTUAL_ACCOUNT(4027, "not enough vitrual account"),
    ALREADY_MANUAL_TRANSACTION_EXISTS(4028, "already manual transaction exist"),
    ALREADY_TRANSACTION_EXISTS(4029, "already transaction exist"),
    AVAILABLE_BALANCE_NOT_ENOUGH(4031, "Available balance not enough"),
    INVALID_PASSWORD(4032, "Invalid password"),

    UNDER_PRICE_ZERO(4033, "Under price zero"),
    UNDER_AMOUNT_ZERO(4034, "Under amount zero"),

    EQUAL_USER(502, "equal_user");


    private Integer code;
    private String message;

    Code(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
