package com.fastcampus.gotogether.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseStatusCustomCode {

    ADMIN(300);
    private final int code;
}
