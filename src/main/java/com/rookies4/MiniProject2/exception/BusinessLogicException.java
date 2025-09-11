package com.rookies4.MiniProject2.exception;

public class BusinessLogicException extends CustomException {
    public BusinessLogicException(ErrorCode errorCode) {
        super(errorCode);
    }
}