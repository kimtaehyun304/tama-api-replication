package org.example.tamaapi.common.exception;

public class WillCancelPaymentException extends RuntimeException{

    public WillCancelPaymentException(String reason) {
        super(String.format("결제가 취소될 예정입니다. 이유: %s", reason));
    }
}
