package com.imagehoo.auth_10.api.controller;

import com.imagehoo.auth_10.dto.LoginRequest;
import com.imagehoo.auth_10.dto.RegisterRequest;
import com.imagehoo.auth_10.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/auth")
public class AuthController {

    private final AppUserService userService;

    public AuthController(UserDetailsService userService) {
        this.userService = (AppUserService) userService;
    }



    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> loginTo(@Valid @RequestBody(required = true) LoginRequest loginRequest){
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> register(/*@Valid */@RequestBody(required = true) RegisterRequest registerRequest){
        return ResponseEntity.ok(userService.registerUser(registerRequest));
    }
}
