package com.fastcampus.gotogether.auth.jwt;


import com.fastcampus.gotogether.auth.dto.UserDTO;
import com.fastcampus.gotogether.auth.service.TokenService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Builder
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IllegalArgumentException {

        String accesstoken = request.getHeader(HttpHeaders.AUTHORIZATION);

        UserDTO.UserAccessDTO userAccessDTO = jwtProvider.tokenToUser(accesstoken);

        try {
//           분석이 끝난 유저 객체에 있는 정보를 시큐리티컨텍스트 빈객체에 넘겨준다. (정보와, 권한을 넘겨준다.)
            if (userAccessDTO != null && !tokenService.checkToken(accesstoken)) {
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                        userAccessDTO,
                        "",
                        userAccessDTO.getAuthorities()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        filterChain.doFilter(request, response);

    }
}
