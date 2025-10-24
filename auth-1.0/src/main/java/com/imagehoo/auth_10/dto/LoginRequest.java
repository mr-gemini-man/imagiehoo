package com.imagehoo.auth_10.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginRequest {

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String password;
}
