package com.imagehoo.auth_10.dto;

import com.imagehoo.auth_10.mocks.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private Long version;
    private String shortId;
    private String email;
    private List<Role> roles;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;
}
