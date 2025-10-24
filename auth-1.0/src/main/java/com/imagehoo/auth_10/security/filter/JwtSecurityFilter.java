package com.imagehoo.auth_10.security.filter;

import com.imagehoo.auth_10.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Slf4j
@Component
public class JwtSecurityFilter/* extends OncePerRequestFilter */{

    private final JwtTokenProvider jwtTokenProvider;

    public JwtSecurityFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

 //   @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Request received from user: {}", request.getRemoteAddr());
        log.info("Token auth checking...");

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)){
                String userId = jwtTokenProvider.getSubject(token);
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                userId, "", List.of(new SimpleGrantedAuthority("USER"))
                        )
                );
            }
            log.info("Authentication Successful");
        }
        log.info("Authentication Failed");
        log.info("Forwarding request to {}", request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
