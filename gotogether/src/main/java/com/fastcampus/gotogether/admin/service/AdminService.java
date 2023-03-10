package com.fastcampus.gotogether.admin.service;

import com.fastcampus.gotogether.auth.dto.UserDTO;
import com.fastcampus.gotogether.global.response.ResponseDTO;
import org.springframework.data.domain.Pageable;

public interface AdminService {

    ResponseDTO<?> setUserToAdmin(UserDTO.EmailOnly dto);

    ResponseDTO<?> setAdminToUser(UserDTO.EmailOnly dto);

    ResponseDTO<?> findUserList(Pageable pageable);
}
