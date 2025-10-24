package com.imagehoo.auth_10.dto;

import com.imagehoo.auth_10.mocks.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterRequest {

    @NotEmpty
    @Email
    private String Email;

    @NotEmpty
    private String password;

    private List<Role> roles = List.of(Role.USER);
}
