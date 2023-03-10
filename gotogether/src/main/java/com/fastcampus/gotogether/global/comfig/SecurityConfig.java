package com.fastcampus.gotogether.global.comfig;

import com.fastcampus.gotogether.auth.jwt.JwtExceptionFilter;
import com.fastcampus.gotogether.auth.jwt.JwtFilter;
import com.fastcampus.gotogether.auth.jwt.JwtProperties;
import com.fastcampus.gotogether.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {


    private final JwtFilter jwtFilter;
    private final JwtProperties jwtProperties;
    private final JwtProvider jwtProvider;


    private static final String[] PUBLIC_URLS = { //이 URL은 권한 검사안함
            "/user/signup", "/user/login", "/api/logout", "/user/refresh",

            /* swagger v3 */
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };
    private static final String[] ADMIN_URLS = { //이 URL은 ADMIN 권한만 허용
            "/admin/**"
    };


    @Bean //회원 insert 서비스에서 비밀번호 암호화/복호화에 사용됨
    public PasswordEncoder passwordEncoderParser() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //시큐리티 필터는 보호막 같은 놈.

        return http
                .cors() // cors 관련 추가
                .and()
                .authorizeRequests()
                .mvcMatchers(PUBLIC_URLS).permitAll() // 가입 및 인증 주소는 누구나 접근가능
                .and()
                .authorizeRequests()
                .mvcMatchers(ADMIN_URLS).hasRole("ADMIN")// Admin 권한만 접근 가능
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(JwtExceptionFilter.of(jwtProvider, jwtProperties), UsernamePasswordAuthenticationFilter.class)
                .build()
                ;

    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() { //시큐리티 filter 제외, 그러나 OncePerRequestFilter는 시큐리티 필터가 아니라서 로직실행
        return (web) -> web.ignoring().mvcMatchers(PUBLIC_URLS);
    }

    //Cors 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addExposedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}