package com.fastcampus.gotogether.auth.controller;

import com.fastcampus.gotogether.admin.service.AdminService;
import com.fastcampus.gotogether.auth.dto.UserDTO;
import com.fastcampus.gotogether.global.response.ResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"테스트 서비스"}, description = "유저, 관리자 관련 테스트 (배포시 삭제)")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/test")
public class TestController {

    private final AdminService adminService;

    @GetMapping("/test/user")
    @PreAuthorize("hasAnyRole('USER')")
    /**유저 권한만 허용 **/
    @ApiOperation(value = "User 권한 확인", notes = "ROLE_USER 인 경우만 작동")
    public ResponseDTO<?> checkUser(@AuthenticationPrincipal UserDTO.UserAccessDTO userAccessDTO) {
        return new ResponseDTO<>(userAccessDTO.getEmail());
    }

    @GetMapping("/test/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "ADMIN 권한 확인", notes = "ROLE_ADMIN 인 경우만 작동")
    public ResponseDTO<?> checkAdmin(@AuthenticationPrincipal UserDTO.UserAccessDTO userAccessDTO) {
        return new ResponseDTO<>(userAccessDTO.getEmail());
    }

    @GetMapping("/test/role")
    @ApiOperation(value = "권한 확인", notes = "모든 권한에서 작동")
    public String checkRole(@AuthenticationPrincipal UserDTO.UserAccessDTO userAccessDTO) {
        return userAccessDTO.getRole();
    }

    @GetMapping("/test/selfAdmin")
    @ApiOperation(value = "개발 테스트용 관리자 승격",notes = "현재 사용자 관리자로 승격 (main 올릴땐 삭제)")
    public ResponseDTO<?> selfAdmin(@AuthenticationPrincipal UserDTO.UserAccessDTO userAccessDTO){
        return adminService.setUserToAdmin(new UserDTO.EmailOnly(userAccessDTO.getEmail()));
    }
}
