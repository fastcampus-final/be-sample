package com.fastcampus.gotogether.auth.service;


import com.fastcampus.gotogether.auth.dto.TokenDTO;
import com.fastcampus.gotogether.global.response.ResponseDTO;

public interface TokenService {

    ResponseDTO<?> logout(String header, TokenDTO.RefreshTokenReqDTO refreshTokenReqDTO);

    ResponseDTO<?> validateRefreshToken(String refreshToken);

    boolean checkToken(String token);
}
