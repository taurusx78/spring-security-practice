package com.example.security.controller.user;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.security.domain.Account;
import com.example.security.domain.AccountDto;
import com.example.security.service.UserService;

@Controller
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;
    
    @GetMapping("/mypage")
    public String myPage() {
        return "user/mypage";
    }

    @GetMapping("/user")
    public String createUser() {
        return "user/login/register";
    }

    @PostMapping("/user")
    public String createUser(AccountDto accountDto) {
        ModelMapper modelMapper = new ModelMapper();
        // AccountDto 객체의 필드 값을 Account 객체로 매핑함
        Account account = modelMapper.map(accountDto, Account.class);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        userService.createUser(account);

        return "redirect:/";
    }
}
