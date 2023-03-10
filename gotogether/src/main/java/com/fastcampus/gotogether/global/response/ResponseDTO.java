package com.fastcampus.gotogether.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseDTO<T> {

    @Schema(name = "HttpStatus code", example = "OK")
    private HttpStatus status;
    @Schema(name = "HttpStatus code", example = "200")
    private Integer code;
    @Schema(name = "result message", example = "추가 설명 메세지")
    private String message;

    @Schema(name = "result data", example = "{name = 'data', age = 'name=홍길동'}")
    private T data;

    public ResponseDTO(T data) {
        this.code = HttpStatus.OK.value();
        this.status = HttpStatus.OK;
        this.message = null;
        this.data = data;
    }

    public ResponseDTO(HttpStatus status, T data) {
        this.code = status.value();
        this.status = status;
        this.data = data;
    }

    public ResponseDTO(HttpStatus status, T data, String message) {
        this.code = status.value();
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public ResponseDTO(HttpStatus status, Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public static <T> ResponseDTO<T> empty() {
        return new ResponseDTO<>(null);
    }

}