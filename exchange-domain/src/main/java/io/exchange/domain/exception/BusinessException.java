package io.exchange.domain.exception;

import io.exchange.domain.enums.Code;

@SuppressWarnings("serial")
public class BusinessException extends RuntimeException {

    public final Code code;

    public final Object data;

    public BusinessException(Code code) {
        super(code.getMessage());
        this.code = code;
        this.data = null;
    }

    public BusinessException(Code code, Object data) {
        super(code.getMessage());
        this.code = code;
        this.data = data;
    }

    public BusinessException(Code code, Object data, Throwable cause) {
        super(code.getMessage(), cause);
        this.code = code;
        this.data = data;
    }

    public BusinessException(Code code, Object data, String message) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public BusinessException(Code code, Object data, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.data = data;
    }

    public Code getCodeEnum() {
        return code;
    }
}
