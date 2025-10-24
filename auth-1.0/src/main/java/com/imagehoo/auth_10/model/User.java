package com.imagehoo.auth_10.model;


import com.imagehoo.auth_10.mocks.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Version
    private Long version;

    @Id
    @Column(unique = true, nullable = false, updatable = false)
    private String id = UUID.randomUUID().toString();

    @Column(unique = true)
    private String username;

    @NotNull
    @Column(unique = true, nullable = false)
    private String password_hashed;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    private List<Role> roles;

    public User(String username, String password_hashed, String email, List<Role> roles) {
        this.username = username;
        this.password_hashed = password_hashed;
        this.email = email;
        this.roles = roles;
    }
}
