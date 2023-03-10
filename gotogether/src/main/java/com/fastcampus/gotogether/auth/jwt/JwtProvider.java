package com.fastcampus.gotogether.auth.jwt;

import com.fastcampus.gotogether.auth.dto.TokenDTO;
import com.fastcampus.gotogether.auth.dto.UserDTO;
import com.fastcampus.gotogether.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    public TokenDTO makeJwtToken(User user) { //토큰 발급 시기를 생각해보면 토큰은 로그인하고나서 (findByEmailAndPassword) 실행후이므로 실행 결과값은 Optional<entity> 나옴
        // 그것을 넣어야 하기 때문에 entity 인 Member객체가 매개변수로 들어간다.
        // 토큰을 문자열이므로 반환값은 String 이 된다.
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer()) // 누가 발급했나.?
                .setIssuedAt(now) //토큰 발급시간
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(30).toMillis())) // 토큰 발급 시간 기준 얼마나 유지 시킬건지.
                .claim("email", user.getEmail()) // 페이로드에 현재 엔티티의 정보
                .claim("role", user.getRole())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer()) // 누가 발급했나.?
                .setIssuedAt(now) //토큰 발급시간
                .setExpiration(new Date(now.getTime() + Duration.ofDays(15).toMillis())) // 토큰 발급 시간 기준 얼마나 유지 시킬건지.
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();

        return TokenDTO.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }


    public UserDTO.UserAccessDTO tokenToUser(String accessToken) {

        try {
            accessToken = extractToken(accessToken);
            Claims claims = null;
            claims = tokenToClaims(accessToken);
            return new UserDTO.UserAccessDTO(claims);
        } catch (Exception e) {
            return null;
        }
    }

    public Claims tokenToClaims(String accessToken) {
        //토큰을 받아와서 Claims 로 바꿔주는 녀석
        //jwt필터에서 시큐리티 필터로 넘어가기전에 토큰을 시큐리티 필터가 알수 있게 바꿔준다고 생각하면됨.
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(accessToken)
                .getBody();

    }

    public String extractToken(String authorizationHeader) { //토큰 (Bearer) 떼고 토큰값만 가져오는 메서드
        return authorizationHeader.substring(jwtProperties.getTokenPrefix().length());
    }

    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration =
                Jwts.parser()
                        .setSigningKey(jwtProperties.getSecretKey())
                        .parseClaimsJws(accessToken)
                        .getBody().getExpiration();
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    public String recreationAccessToken(String userEmail, String userRole) {

        Date now = new Date();
        //Access Token
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer()) // 누가 발급했나.?
                .setIssuedAt(now) //토큰 발급시간
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(30).toMillis())) // 토큰 발급 시간 기준 얼마나 유지 시킬건지.
                .claim("email", userEmail)
                .claim("role", userRole)// 페이로드에 현재 엔티티의 정보
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }


}
