package com.rookies4.MiniProject2.exception;

public class EntityNotFoundException extends CustomException {
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}