package com.test.auth.controller;


import com.test.auth.DTO.AuthResponse;
import com.test.auth.DTO.LoginRequestDto;
import com.test.auth.DTO.SignupRequestDto;
import com.test.auth.model.UserEntity;
import com.test.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {


    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody SignupRequestDto signupRequestDto){

        return  new ResponseEntity<>(authService.register(signupRequestDto),HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody LoginRequestDto loginRequestDto){

        return  new ResponseEntity<>(authService.login(loginRequestDto),HttpStatus.OK);
    }

//    @GetMapping("/GetUser/{UserId}")
//    public ResponseEntity<UserEntity> getresponse(@PathVariable long UserId ){
//
//        return  new ResponseEntity<>();
//    }


}
