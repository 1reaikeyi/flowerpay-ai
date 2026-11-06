package com.branch.common.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class VoucherException extends BaseException {
    public VoucherException(String model,String msg) {
        super(model,msg);
    }
    public VoucherException(String msg) {
        super(msg);
    }
}
