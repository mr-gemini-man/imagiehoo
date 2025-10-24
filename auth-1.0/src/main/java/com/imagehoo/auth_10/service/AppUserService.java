package com.imagehoo.auth_10.service;

import com.imagehoo.auth_10.dto.LoginRequest;
import com.imagehoo.auth_10.dto.LoginResponse;
import com.imagehoo.auth_10.dto.RegisterRequest;
import com.imagehoo.auth_10.dto.RegisterResponse;
import com.imagehoo.auth_10.mocks.LoginAndRegisterMessage;
import com.imagehoo.auth_10.mocks.Role;
import com.imagehoo.auth_10.repo.UserRepo;
import com.imagehoo.auth_10.security.jwt.JwtTokenProvider;
import com.imagehoo.auth_10.util.RetryOnOptimisticLock;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AppUserService implements UserDetailsService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepo userRepo;
    private final PasswordEncoder bCryptPasswordEncoder;

    public AppUserService(JwtTokenProvider jwtTokenProvider, UserRepo userRepo, PasswordEncoder bCryptPasswordEncoder) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepo = userRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //--
        log.info("Loading user by username: {}", username);
        //--
        Optional<com.imagehoo.auth_10.model.User> userOptional = userRepo.findByUsername(username);
        com.imagehoo.auth_10.model.User user = userOptional.orElseThrow(
                () -> new UsernameNotFoundException(username));

        // Note: The returned User object contains the HASHED password for Spring Security to check.
        return User.builder().username(user.getUsername())
                .password(user.getPassword_hashed())
                .roles(user.getRoles().toString())
                .build();
    }

    @RetryOnOptimisticLock(maxRetries = 3, delayInMillis = 100)
    @Transactional
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        //--
        log.info("Registering user: {}", registerRequest);
        //--

        // --- FIX: Check for existing username before attempting registration ---
        if (userRepo.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Username '" + registerRequest.getEmail() + "' is already taken.");
        }

        com.imagehoo.auth_10.model.User user = new com.imagehoo.auth_10.model.User(
                null,
                bCryptPasswordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getEmail(),
                Collections.singletonList(Role.USER)
        );
        Optional<com.imagehoo.auth_10.model.User> optionalUser = Optional.of(userRepo.save(user));
        com.imagehoo.auth_10.model.User savedUser = optionalUser.orElseThrow(() ->
                new UsernameNotFoundException(registerRequest.getEmail()));

        return RegisterResponse.builder()
                .email(savedUser.getEmail())
                .roles(savedUser.getRoles())
                .version(savedUser.getVersion())
                .message(LoginAndRegisterMessage.REGISTER_SUCCESS_MESSAGE)
                .timestamp(LocalDateTime.now())
                .shortId(savedUser.getId().substring(0, 10))
                .build();

    }

    public LoginResponse login(LoginRequest loginRequest) {
        //--
        log.info("Login request: {}", loginRequest);
        //--
        Optional<com.imagehoo.auth_10.model.User> userOptional = userRepo.findByEmail(loginRequest.getEmail());
        com.imagehoo.auth_10.model.User user = userOptional.orElseThrow(
                () -> new UsernameNotFoundException(loginRequest.getEmail()));

        // --- CRITICAL SECURITY FIX: Verify Password ---
        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword_hashed())) {
            // Throwing BadCredentialsException is standard for failed login attempts
            throw new BadCredentialsException("Invalid email or password.");
        }

        // Only generate tokens if the username and password match
        String token = jwtTokenProvider.token(loginRequest.getEmail());
        String refToken = jwtTokenProvider.token(loginRequest.getEmail());

        return new LoginResponse(token, refToken);
    }
}
