package io.exchange.domain.exception;

import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;

@SuppressWarnings("serial")
public class BadParameterException extends RuntimeException {

    private final BindingResult bindingResult;

    public BadParameterException(BindingResult bindingResult) {
        Assert.notNull(bindingResult, "BindingResult must not be null");
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return this.bindingResult;
    }
}
