package com.example.security.security.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.example.security.domain.entity.Account;

public class CustomUserDetails extends User {

    private final Account account;

    public CustomUserDetails(Account account, Collection<? extends GrantedAuthority> authorities) {
        super(account.getUsername(), account.getPassword(), authorities);
        this.account = account;
    }

    public Account getAccount() {
        return this.account;
    }    
}
