package com.huansi.dataagent.auth;

import com.huansi.dataagent.auth.dto.AuthResponse;
import com.huansi.dataagent.auth.dto.LoginRequest;
import com.huansi.dataagent.auth.dto.RegisterRequest;
import com.huansi.dataagent.auth.dto.UserProfile;
import com.huansi.dataagent.common.ApiException;
import com.huansi.dataagent.security.AppUserDetailsService;
import com.huansi.dataagent.security.JwtService;
import com.huansi.dataagent.user.Role;
import com.huansi.dataagent.user.User;
import com.huansi.dataagent.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AppUserDetailsService userDetailsService,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username().trim(), request.password()));
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User user = findUser(principal.getUsername());
        return buildResponse(user, principal);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByUsername(username)) {
            throw new ApiException(HttpStatus.CONFLICT, "用户名已被占用");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "邮箱已被注册");
        }
        User user = new User(
                username,
                email,
                request.displayName().trim(),
                passwordEncoder.encode(request.password()),
                Role.USER);
        User saved = userRepository.save(user);
        UserDetails principal = userDetailsService.loadUserByUsername(saved.getUsername());
        return buildResponse(saved, principal);
    }

    public UserProfile currentUser(String username) {
        return UserProfile.from(findUser(username));
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "用户不存在"));
    }

    private AuthResponse buildResponse(User user, UserDetails principal) {
        String token = jwtService.generateToken(principal);
        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                UserProfile.from(user));
    }
}
