package com.branch.common.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LoginFailedException extends BaseException {

    public LoginFailedException(String msg) {
        super(msg);
    }
    public LoginFailedException(String module, String defaultMessage) {
        super(module, defaultMessage);
    }
}
