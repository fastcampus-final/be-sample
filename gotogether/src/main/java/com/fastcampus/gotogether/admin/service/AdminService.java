package com.fastcampus.gotogether.admin.service;

import com.fastcampus.gotogether.auth.dto.UserDTO;
import com.fastcampus.gotogether.global.response.ResponseDTO;

public interface AdminService {

    ResponseDTO<?> setUserToAdmin(UserDTO.EmailOnly user);
}
