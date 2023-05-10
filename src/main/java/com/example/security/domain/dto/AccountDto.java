package com.example.security.domain.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AccountDto implements Serializable {
    
    private String username;
    private String password;
    private String email;
    private int age;
    private String role;

    @Builder
    public AccountDto(String username, String password, String email, int age, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.role = role;
    }
}
