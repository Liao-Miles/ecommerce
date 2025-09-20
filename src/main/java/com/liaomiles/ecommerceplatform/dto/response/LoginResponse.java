package com.liaomiles.ecommerceplatform.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String email;

}

