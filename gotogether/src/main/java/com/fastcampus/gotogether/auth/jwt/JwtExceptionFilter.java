package com.fastcampus.gotogether.auth.jwt;

import com.fastcampus.gotogether.auth.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Getter
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    @Builder
    private JwtExceptionFilter(JwtProvider jwtProvider, JwtProperties jwtProperties) {
        this.jwtProvider = jwtProvider;
        this.jwtProperties = jwtProperties;
    }

    public static JwtExceptionFilter of(JwtProvider jwtProvider, JwtProperties jwtProperties) {
        return JwtExceptionFilter.builder()
                .jwtProvider(jwtProvider)
                .jwtProperties(jwtProperties)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            verificationAccessToken(accessToken);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            //토큰의 유효기간 만료
            setErrorResponse(response, ErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException | SignatureException e) {
            //유효하지 않은 토큰
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);
        } catch (NullPointerException | IllegalArgumentException e) {
            // 토큰이 없습니다.
            setErrorResponse(response, ErrorCode.UNKNOWN_ERROR);
        }
    }

    private void verificationAccessToken(String accessToken) throws MalformedJwtException, ExpiredJwtException, IllegalArgumentException, NullPointerException {
        accessToken = jwtProvider.extractToken(accessToken);
        Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(accessToken)
                .getBody();
    }


    public void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject responseJson = new JSONObject();
        responseJson.put("code", errorCode.getCode());
        responseJson.put("message", errorCode.getMessage());
        response.getWriter().print(responseJson);
    }
}