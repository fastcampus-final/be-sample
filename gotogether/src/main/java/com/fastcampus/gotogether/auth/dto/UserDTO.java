package com.fastcampus.gotogether.auth.dto;

import com.fastcampus.gotogether.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class UserDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ApiModel(value = "로그인")
    public static class LoginReqDTO {

        @ApiModelProperty(value = "이메일 ", required = true)
        private String email;

        @ApiModelProperty(value = "비밀번호 ", required = true)
        private String password;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ApiModel(value = "토큰에 담길 정보")
    public static class UserAccessDTO {
        @ApiModelProperty(value = "이메일 ", required = true)
        private String email;

        @ApiModelProperty(value = "권한 ", required = true)
        private String role;

        public UserAccessDTO(Claims claims) {
            this.email = claims.get("email", String.class);
            this.role = claims.get("role", String.class);
        }

        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singleton(new SimpleGrantedAuthority(this.role));
        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    @ApiModel(value = "회원가입")
    public static class SignupReqDTO {

        @ApiModelProperty(value = "이메일", required = true)
        private String email;
        @ApiModelProperty(value = "비밀번호", required = true)
        private String password;
        @ApiModelProperty(value = "이름", required = true)
        private String name;
        @ApiModelProperty(value = "생년월일", required = true)
        private String birth;
        @ApiModelProperty(value = "전화번호", required = true)
        private String phone;

        public User toEntity() {

            return User.builder()
                    .email(this.email)
                    .password(this.password)
                    .name(this.name)
                    .birth(this.birth)
                    .phone(this.phone)
                    .role("ROLE_USER")
                    .build();
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @ApiModel(value = "회원정보수정 입력")
    public static class PatchUserReqDTO {

        @ApiModelProperty(value = "기존 비밀번호", required = true)
        private String oldPassword;
        @ApiModelProperty(value = "새로운 비밀번호")
        private String newPassword;
        @ApiModelProperty(value = "전화번호")
        private String phone;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @ApiModel(value = "회원정보수정 출력")
    public static class PatchUserResDTO {

        @ApiModelProperty(value = "이메일")
        private String email;
        @ApiModelProperty(value = "비밀번호")
        private String password;
        @ApiModelProperty(value = "이름")
        private String name;
        @ApiModelProperty(value = "생년월일")
        private String birth;
        @ApiModelProperty(value = "전화번호")
        private String phone;


        public PatchUserResDTO(User user) {
            this.email = user.getEmail();
            this.password = user.getPassword();
            this.name = user.getName();
            this.birth = user.getBirth();
            this.phone = user.getPhone();
        }
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @ApiModel(value = "회원 탈퇴")
    public static class DeleteUserReqDTO {

        @ApiModelProperty(value = "비밀번호 ", required = true)
        private String password;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @ApiModel(value = "이메일만 사용하는 dto")
    public static class EmailOnly {

        @ApiModelProperty(value = "이메일 ", required = true)
        private String email;
    }


}

