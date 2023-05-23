package com.openccos.framework.core.exception;

public class UnauthenticatedException extends CcException {
    public UnauthenticatedException(String message) {
        super(message);
    }

    public UnauthenticatedException(Throwable e) {
        super(e);
    }
}
