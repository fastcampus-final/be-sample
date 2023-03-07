package com.fastcampus.gotogether.admin.controller;

import com.fastcampus.gotogether.admin.service.AdminService;
import com.fastcampus.gotogether.auth.dto.UserDTO;
import com.fastcampus.gotogether.global.response.ResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"관리자 서비스"}, description = "관리자 권한 부여,")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/admin/setAdmin")
    @ApiOperation(value = "회원탈퇴 버튼 (토큰 O)", notes = "입력받은 email 을 통해 관리자 권한 부여")
    public ResponseDTO<?> setAdmin(@RequestBody UserDTO.EmailOnly user){
        return new ResponseDTO<>(adminService.setUserToAdmin(user));
    }


}
