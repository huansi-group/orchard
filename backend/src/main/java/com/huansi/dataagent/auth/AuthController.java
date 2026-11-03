package com.huansi.dataagent.auth;

import com.huansi.dataagent.auth.dto.AuthResponse;
import com.huansi.dataagent.auth.dto.LoginRequest;
import com.huansi.dataagent.auth.dto.RegisterRequest;
import com.huansi.dataagent.auth.dto.UserProfile;
import com.huansi.dataagent.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("登录成功", authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok("注册成功", authService.register(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserProfile> me(Authentication authentication) {
        return ApiResponse.ok(authService.currentUser(authentication.getName()));
    }
}
