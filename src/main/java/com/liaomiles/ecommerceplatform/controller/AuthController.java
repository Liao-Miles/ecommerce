package com.liaomiles.ecommerceplatform.controller;

import com.liaomiles.ecommerceplatform.dto.request.LoginRequest;
import com.liaomiles.ecommerceplatform.dto.request.RegisterRequest;
import com.liaomiles.ecommerceplatform.dto.response.LoginResponse;
import com.liaomiles.ecommerceplatform.entity.User;
import com.liaomiles.ecommerceplatform.repository.UserRepository;
import com.liaomiles.ecommerceplatform.security.JwtUtil;
import com.liaomiles.ecommerceplatform.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.liaomiles.ecommerceplatform.service.CartService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CartService cartService;
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email 已被註冊");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok("註冊成功");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request,
                                   @RequestParam(value = "sessionId", required = false) String sessionId) {
        LoginResponse response = authService.login(request, sessionId);
        return ResponseEntity.ok(response);
    }
}
