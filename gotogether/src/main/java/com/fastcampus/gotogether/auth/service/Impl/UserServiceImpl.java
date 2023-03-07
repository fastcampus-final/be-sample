package com.fastcampus.gotogether.auth.service.Impl;

import com.fastcampus.gotogether.auth.dto.TokenDTO;
import com.fastcampus.gotogether.auth.dto.UserDTO;
import com.fastcampus.gotogether.auth.entity.User;
import com.fastcampus.gotogether.auth.jwt.JwtProvider;
import com.fastcampus.gotogether.auth.repository.RedisTemplateRepository;
import com.fastcampus.gotogether.auth.repository.UserRepository;
import com.fastcampus.gotogether.auth.service.UserService;
import com.fastcampus.gotogether.global.response.ErrorResponseDTO;
import com.fastcampus.gotogether.global.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplateRepository redisTemplateRepository;
    @Override
    public ResponseDTO<?> signup(UserDTO.SignupReqDTO signupReqDTO) {
        if (userRepository.findByEmail(signupReqDTO.getEmail()).isEmpty()) {
            String encodingPassword = encodingPassword(signupReqDTO.getPassword());
            signupReqDTO.setPassword(encodingPassword);
            userRepository.save(signupReqDTO.toEntity());
            return new ResponseDTO<>(signupReqDTO.toString());
        } else {
            return new ErrorResponseDTO(500, "이미 존재하는 회원입니다.").toResponse();
        }
    }

    @Override
    public ResponseDTO<?> login(UserDTO.LoginReqDTO loginReqDTO) {
        try {
            User user = userRepository.findByEmail(loginReqDTO.getEmail())
                    .orElseThrow(IllegalArgumentException::new);
            if (withDrawCheck(user)) {
                return new ErrorResponseDTO(500, "탈퇴한 회원입니다.").toResponse();
            }
            passwordMustBeSame(loginReqDTO.getPassword(), user.getPassword());
            TokenDTO tokenDTO = jwtProvider.makeJwtToken(user);
            redisTemplateRepository.setDataExpire(tokenDTO.getRefreshToken(), user.getEmail(), jwtProvider.getExpiration(tokenDTO.getRefreshToken()));

            return new ResponseDTO<>(tokenDTO);

        } catch (NoSuchElementException | IllegalArgumentException e) {
            return new ErrorResponseDTO(500, "로그인에 실패하였습니다.").toResponse();
        }
    }

    @Override
    public ResponseDTO<?> editUser(UserDTO.UserAccessDTO userAccessDTO) {
        try {
            if (userAccessDTO != null) {
                User user = userRepository.findByEmail(userAccessDTO.getEmail())
                        .orElseThrow(IllegalArgumentException::new);
                return new ResponseDTO<>(new UserDTO.PatchUserResDTO(user));
            } else {
                throw new IllegalArgumentException();
            }

        } catch (IllegalArgumentException e) {
            return new ErrorResponseDTO(500, "로그인 정보가 없습니다.").toResponse();
        }
    }

    @Override
    @Transactional
    public ResponseDTO<?> updateUser(UserDTO.UserAccessDTO userAccessDTO, UserDTO.PatchUserReqDTO patchUserReqDTO) {
        try {
            User user = userRepository.findByEmail(userAccessDTO.getEmail())
                    .orElseThrow(IllegalArgumentException::new);

            passwordMustBeSame(patchUserReqDTO.getOldPassword(), user.getPassword());
            patchUserReqDTO.setNewPassword(encodingPassword(patchUserReqDTO.getNewPassword()));

            user.update(patchUserReqDTO.getNewPassword(), patchUserReqDTO.getPhone());

            return new ResponseDTO<>(200, "회원정보 수정 성공하였습니다.", patchUserReqDTO);
        } catch (IllegalArgumentException e) {
            return new ErrorResponseDTO(500, "기존 비밀번호가 일치하지 않습니다.").toResponse();
        }
    }

    @Override
    @Transactional
    public ResponseDTO<?> deleteUser(UserDTO.UserAccessDTO userAccessDTO, UserDTO.DeleteUserReqDTO deleteUserReqDTO) {
        try {
            User user = userRepository.findByEmail(userAccessDTO.getEmail())
                    .orElseThrow(IllegalArgumentException::new);

            passwordMustBeSame(deleteUserReqDTO.getPassword(), user.getPassword());
            user.delete("withdraw");

            return new ResponseDTO<>(200, "회원 탈퇴 성공.", user);
        } catch (IllegalArgumentException e) {
            return new ErrorResponseDTO(500, "기존 비밀번호가 일치하지 않습니다.").toResponse();
        }
    }

    @Override
    @Transactional
    public ResponseDTO<?> setUserToAdmin(UserDTO.EmailOnly dto) {
        try {
            User user =  userRepository.findByEmail(dto.getEmail()).orElseThrow(IllegalArgumentException::new);
            user.setRole("ROLE_ADMIN");
            return new ResponseDTO<>(200,"관리자 권한 부여 완료.",user.getEmail());
        }catch (IllegalArgumentException e){
            return new ErrorResponseDTO(500,"관리자 권한 부여 실패").toResponse();
        }
    }

    private String encodingPassword(String password) {
        return passwordEncoder.encode(password);
    }
    private boolean withDrawCheck(User user) {
        return user.getDeleteCheck() != null;
    }
    private void passwordMustBeSame(String requestPassword, String password) {
        if (!passwordEncoder.matches(requestPassword, password)) {
            throw new IllegalArgumentException();
        }
    }
}
