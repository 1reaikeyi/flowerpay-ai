package com.branch.exception;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public class ModelFailedException extends BaseException {
    public ModelFailedException(String msg) {
        super(msg);
    }
    public ModelFailedException(String module, String defaultMessage) {
        super(module, defaultMessage);
    }
    public ModelFailedException(String module,String code, String defaultMessage) {
        super(module, code, defaultMessage);
    }
}
