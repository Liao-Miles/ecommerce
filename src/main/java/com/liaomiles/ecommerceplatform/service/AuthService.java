package com.liaomiles.ecommerceplatform.service;

import com.liaomiles.ecommerceplatform.dto.request.LoginRequest;
import com.liaomiles.ecommerceplatform.dto.response.LoginResponse;
import com.liaomiles.ecommerceplatform.entity.User;
import com.liaomiles.ecommerceplatform.repository.UserRepository;
import com.liaomiles.ecommerceplatform.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CartService cartService;

    public LoginResponse login(LoginRequest request, String sessionId) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.generateToken(request.getEmail());
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (sessionId != null && !sessionId.isEmpty()) {
                cartService.mergeCartOnLogin(user.getId(), sessionId);
            }
            LoginResponse resp = new LoginResponse();
            resp.setToken(token);
            resp.setUserId(user.getId());
            resp.setEmail(user.getEmail());
            return resp;
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
