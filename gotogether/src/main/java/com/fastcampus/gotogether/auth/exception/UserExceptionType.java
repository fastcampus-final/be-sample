package com.fastcampus.gotogether.auth.exception;


public enum UserExceptionType {
    DUPLICATE_EMAIL(400, "이미 존재하는 `email`입니다."),
    NON_EXISTENT_USER(404, "존재하지 않는 회원입니다."),
    UNMATCHED_PASSWORD(404, "일치하지 않는 패스워드");

    private int errorCode;

    private String errorMsg;

    UserExceptionType(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }


    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }
}
