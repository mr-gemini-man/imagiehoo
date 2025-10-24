package com.imagehoo.auth_10.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasRole;

@RestController
@RequestMapping("v1")
@Slf4j
public class PingSecuredController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/ping")
    public ResponseEntity<Object> ping(HttpServletRequest request, Authentication authentication){
        String username = authentication.getPrincipal().toString();
        log.info("username = {}", username);
        return ResponseEntity.ok("pong");
    }
}
