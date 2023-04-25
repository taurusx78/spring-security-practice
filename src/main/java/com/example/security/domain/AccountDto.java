package com.example.security.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AccountDto {
    
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
