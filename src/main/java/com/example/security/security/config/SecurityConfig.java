package com.example.security.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 비밀번호를 암호화하는 PasswordEncoder 빈 생성
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // AuthenticationManager 빈 생성 시 UserSecurityService와 PasswordEncoder가 자동 설정됨
    // AuthenticationManager은 우리가 직접 구현한 CustomUserDetailsService를 이용해 사용자 인증 진행해줌
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 개발 테스트용으로 인메모리에 사용자 생성
    // (참고) https://covenant.tistory.com/277
    // @Bean
    // public InMemoryUserDetailsManager userDetailsService() {
    // String password = passwordEncoder().encode("1111");

    // UserDetails user = User.withUsername("user")
    // .password(password)
    // .roles("USER")
    // .build();

    // UserDetails manager = User.withUsername("manager")
    // .password(password)
    // .roles("MANAGER", "USER")
    // .build();

    // UserDetails admin = User.withUsername("admin")
    // .password(password)
    // .roles("ADMIN", "MANAGER", "USER")
    // .build();

    // return new InMemoryUserDetailsManager(user, manager, admin);
    // }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/user").permitAll()
                .antMatchers("/mypage").hasRole("USER")
                .antMatchers("/messages").hasRole("MANAGER")
                .antMatchers("/config").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin();

        return http.build();
    }

    // 아래 요청에 대해선 시큐리티 보안이 적용되지 않도록 설정 (WebIgnore 설정)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/css/**", "/images/**", "/js/**");
    }
}
