package com.fastcampus.gotogether.auth.service;


import com.fastcampus.gotogether.auth.dto.UserDTO;
import com.fastcampus.gotogether.global.response.ResponseDTO;

public interface UserService {

    ResponseDTO<?> signup(UserDTO.SignupReqDTO signupReqDTO);

    ResponseDTO<?> login(UserDTO.LoginReqDTO loginReqDTO);

    public ResponseDTO<?> editUser(UserDTO.UserAccessDTO userAccessDTO);

    public ResponseDTO<?> updateUser(UserDTO.UserAccessDTO userAccessDTO, UserDTO.PatchUserReqDTO patchUserReqDTO);

    public ResponseDTO<?> deleteUser(UserDTO.UserAccessDTO userAccessDTO, UserDTO.DeleteUserReqDTO deleteUserReqDTO);
}
