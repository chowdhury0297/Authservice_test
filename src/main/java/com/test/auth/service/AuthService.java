package com.test.auth.service;

import com.test.auth.DTO.AuthResponse;
import com.test.auth.DTO.LoginRequestDto;
import com.test.auth.DTO.SignupRequestDto;
import org.springframework.stereotype.Service;


public interface AuthService {


    AuthResponse register(SignupRequestDto registerRequest);

    AuthResponse login(LoginRequestDto loginRequest);

    Boolean loginLimitReached(Long userId);
}
