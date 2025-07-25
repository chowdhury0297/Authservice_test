package com.test.auth.service.impl;

import com.test.auth.DTO.AuthResponse;
import com.test.auth.DTO.LoginRequestDto;
import com.test.auth.DTO.SignupRequestDto;
import com.test.auth.exception.DuplicateEntityException;
import com.test.auth.exception.UserAlreadyExistsException;
import com.test.auth.exception.WrongPasswordException;
import com.test.auth.model.Role;
import com.test.auth.model.UserEntity;
import com.test.auth.repository.UserRepository;
import com.test.auth.service.AuthService;
import com.test.auth.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    Logger log = LogManager.getLogger(AuthServiceImpl.class);

    @Override
    public AuthResponse register(SignupRequestDto registerRequest) {

        log.info("SignUp Request || AuthServiceImpl || register(SignupRequestDto registerRequest) ");
        // mapper to convert SignupRequestDto --> UserEntity
//        UserEntity userEntity= mapper.toEntity(registerRequest);

        try{
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                throw new UserAlreadyExistsException("A user with this email already exists");
            }
            if(userRepository.existsByUsername(registerRequest.getUsername())){
                throw new UserAlreadyExistsException("A user with this Username already exists");
            }
            UserEntity user =new UserEntity();
            user.setEmail(registerRequest.getEmail());
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRole(Role.ROLE_USER);
            userRepository.save(user);
        } catch (UserAlreadyExistsException e) {
            if (e.getMessage().contains(registerRequest.getUsername())) {
                throw new DuplicateEntityException("Duplicate username!");
            } else if (e.getMessage().contains(registerRequest.getEmail())) {
                throw new DuplicateEntityException("Duplicate email!");
            } else if (e.getMessage().contains(registerRequest.getPassword())) {
                throw new DuplicateEntityException("Duplicate password!");
            } else {
                System.out.println(e.getMessage());
                throw e;
            }
        }

        return new AuthResponse("User registered successfully!", registerRequest.getUsername(), "");


    }

    @Override
    public AuthResponse login(LoginRequestDto loginRequest) {

        try{
            UserEntity user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            boolean matches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            if (!matches) {
                throw new WrongPasswordException("Wrong password");
            }
            Map<String, Object> dataInJwt = new HashMap<>();
            dataInJwt.put("roles", user.getRole());
            dataInJwt.put("email", user.getEmail());
        String jwt = jwtUtil.generateToken(user.getUsername(), dataInJwt);
            return new AuthResponse("User logged in successfully!", loginRequest.getUsername(), jwt);
        }catch (Exception e){
            throw new EntityNotFoundException("User name or password is invalid !");
        }
    }



    public Boolean loginLimitReached(Long userId) {
//        List<Session> getAllSessions = sessionRepository.getAllByUser_Id(userId);
//        int activeSessions = 0;
//        for (Session session : getAllSessions) {
//            if (session.getSessionStatus().equals(SessionStatus.ACTIVE)) {
//                activeSessions++;
//            }
//        }
//        if (activeSessions == 3) {
//            return true;
//        }

        return false;
    }

    
}
