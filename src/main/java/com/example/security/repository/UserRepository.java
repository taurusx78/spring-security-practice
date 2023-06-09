package com.example.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security.domain.entity.Account;

public interface UserRepository extends JpaRepository<Account, Long> {
    
    Account findByUsername(String username);
}
