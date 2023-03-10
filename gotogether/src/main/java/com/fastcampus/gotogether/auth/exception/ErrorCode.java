package com.fastcampus.gotogether.auth.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_TOKEN(403, BAD_REQUEST, "유효하지 않은 토큰입니다."),
    UNKNOWN_ERROR(401, BAD_REQUEST, "토큰이 존재하지 않습니다."),
    EXPIRED_TOKEN(401, UNAUTHORIZED, "만료된 토큰입니다.");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}
