package com.fastcampus.gotogether.admin.service.impl;

import com.fastcampus.gotogether.admin.service.AdminService;
import com.fastcampus.gotogether.auth.dto.UserDTO;
import com.fastcampus.gotogether.auth.entity.User;
import com.fastcampus.gotogether.auth.repository.UserRepository;
import com.fastcampus.gotogether.global.response.ErrorResponseDTO;
import com.fastcampus.gotogether.global.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;

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

    @Override
    @Transactional
    public ResponseDTO<?> setAdminToUser(UserDTO.EmailOnly dto) {
        try {
            User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(IllegalArgumentException::new);
            user.setRole("ROLE_USER");
            return new ResponseDTO<>(200,"관리자 권한 박탈 완료",user.getEmail());
        }catch (IllegalArgumentException e){
            return new ErrorResponseDTO(500,"관리자 권한 박탈 실패").toResponse();
        }
    }
}
