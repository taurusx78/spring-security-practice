package com.example.security.security.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.security.security.common.FormWebAuthenticationDetails;
import com.example.security.security.service.CustomUserDetails;

// 강의자료 p.110 참고

public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 사용자 인증 진행
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {  
        System.out.println("CustomAuthenticationProvider 클래스의 authenticate() 실행됨");

        String username = authentication.getName(); // 로그인 시 입력된 아이디
        String password = (String) authentication.getCredentials(); // 로그인 시 입력된 비밀번호

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, customUserDetails.getAccount().getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        FormWebAuthenticationDetails formWebAuthenticationDetails = (FormWebAuthenticationDetails) authentication.getDetails();
        String secretKey = formWebAuthenticationDetails.getSecretKey();
        System.out.println("Secret Key: " + secretKey);

        if (secretKey == null || !secretKey.equals("secret")) {
            throw new InsufficientAuthenticationException("Secret Key가 일치하지 않습니다.");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                customUserDetails.getAccount(), null, customUserDetails.getAuthorities());

        return authenticationToken;
    }

    // 파라미터로 주어진 authentication 클래스의 타입과 CustomAuthenticationProvider 클래스가 사용하고자 하는
    // 토큰의 타입이 일치할 때,
    // CustomAuthenticationProvider 클래스를 이용해 사용자 인증을 진행하도록 설정
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
