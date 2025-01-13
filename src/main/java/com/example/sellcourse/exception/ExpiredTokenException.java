package com.example.sellcourse.exception;

public class ExpiredTokenException extends AppException{
    public ExpiredTokenException() {
        super(ErrorCode.EXPIRED_TOKEN);
    }
}
