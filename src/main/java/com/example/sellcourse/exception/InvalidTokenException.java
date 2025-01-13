package com.example.sellcourse.exception;

public class InvalidTokenException extends AppException {
    public InvalidTokenException() {
        super(ErrorCode.UNAUTHENTICATED);
    }
}
