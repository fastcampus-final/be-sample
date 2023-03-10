package com.fastcampus.gotogether.auth.service.Impl;


import com.fastcampus.gotogether.auth.dto.TokenDTO;
import com.fastcampus.gotogether.auth.jwt.JwtProvider;
import com.fastcampus.gotogether.auth.repository.RedisTemplateRepository;
import com.fastcampus.gotogether.auth.repository.UserRepository;
import com.fastcampus.gotogether.auth.service.TokenService;
import com.fastcampus.gotogether.global.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtProvider jwtProvider;
    private final RedisTemplateRepository redisTemplateRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseDTO<?> logout(String header, TokenDTO.RefreshTokenReqDTO refreshTokenReqDTO) {

        if (checkToken(header)) {
            return new ResponseDTO<>(HttpStatus.UNAUTHORIZED, null, "이미 만료된 토큰입니다.");
        } else {
            try {
                String accessToken = jwtProvider.extractToken(header);
                redisTemplateRepository.setDataExpire(header, "logout", jwtProvider.getExpiration(accessToken));
                jwtProvider.getExpiration(refreshTokenReqDTO.getRefreshToken());
                redisTemplateRepository.deleteData(refreshTokenReqDTO.getRefreshToken());

                return new ResponseDTO<>(HttpStatus.OK, null, "로그아웃 성공");
            } catch (Exception e) {
                return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, null, "로그아웃 시도중 에러가 발생 했습니다.");
            }
        }
    }

    @Override
    public ResponseDTO<?> validateRefreshToken(String refreshToken) {
        try {
            String userEmail = redisTemplateRepository.getData(refreshToken);
            if (jwtProvider.getExpiration(refreshToken) > 0 && checkToken(refreshToken)) {
                //Role 확인
                String userRole = userRepository.findByEmail(userEmail).orElseThrow(IllegalArgumentException::new).getRole();

                String updateAccessToken = jwtProvider.recreationAccessToken(userEmail, userRole);
                return new ResponseDTO<>(HttpStatus.OK,
                        TokenDTO.builder().accessToken(updateAccessToken).refreshToken(refreshToken).build(),
                        "Refresh 토큰을 통한 Access Token 생성이 완료되었습니다");
            } else {
                throw new IllegalArgumentException();
            }

        } catch (Exception e) {
            return new ResponseDTO<>(HttpStatus.UNAUTHORIZED, null, "Refresh 토큰이 유효하지 않습니다. 로그인이 필요합니다.");
        }
    }

    @Override
    public boolean checkToken(String token) {
        return redisTemplateRepository.hasKey(token);
    }

}