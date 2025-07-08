package com.mysite.aimusicsheet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mysite.aimusicsheet.dto.SignUpRequest;
import com.mysite.aimusicsheet.dto.LoginRequest;
import com.mysite.aimusicsheet.dto.RefreshRequest;
import com.mysite.aimusicsheet.dto.ApiResponse;
import com.mysite.aimusicsheet.repository.UserRepository;
import com.mysite.aimusicsheet.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody SignUpRequest request) {
        userService.registerUser(
                request.getUserid(),
                request.getUsername(),
                request.getNickname(),
                request.getPassword(),
                request.getEmail()
        );
        return ResponseEntity.ok(
                new ApiResponse<>("200", "회원가입 성공", "회원가입 성공!")
        );
    }

    //아이디 중복확인
    @GetMapping("/userid-signup-dup")
    public ResponseEntity<ApiResponse<Void>> checkUserIdDuplicate(
            @RequestParam("userid") String userid
    ) {
        userService.checkUserIdDuplicate(userid);
        return ResponseEntity.ok(
                new ApiResponse<>("200", "사용 가능한 아이디입니다.", null)
        );
    }

    //이메일 중복확인
    @GetMapping("/email-signup-dup")
    public ResponseEntity<ApiResponse<Void>> checkEmailDuplicate(
            @RequestParam("email") String email
    ) {
        userService.checkEmailDuplicate(email);
        return ResponseEntity.ok(
                new ApiResponse<>("200", "사용 가능한 이메일입니다.", null)
        );
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        Map<String, String> tokens = userService.loginUser(
                request.getUserid(),
                request.getPassword()
        );
        return ResponseEntity.ok(tokens);
    }

    //토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refresh(@RequestBody RefreshRequest request) {
        Map<String, String> tokens = userService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(
                new ApiResponse<>("200", "토큰 재발급 성공", tokens)
        );
    }
}
