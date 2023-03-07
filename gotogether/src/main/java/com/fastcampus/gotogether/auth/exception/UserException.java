package com.fastcampus.gotogether.auth.exception;

public class UserException extends RuntimeException {
    private final UserExceptionType userExceptionType;

    public UserException(UserExceptionType exceptionType) {
        this.userExceptionType = exceptionType;
    }

    public UserExceptionType getUserExceptionType() {
        return this.userExceptionType;
    }
}
