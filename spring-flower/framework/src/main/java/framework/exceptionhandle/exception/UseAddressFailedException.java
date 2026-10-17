package framework.exceptionhandle.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UseAddressFailedException extends BaseException {

    public UseAddressFailedException(String msg) {
        super(msg);
    }

}
