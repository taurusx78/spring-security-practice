package com.example.security.security.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.security.security.common.AjaxLoginAuthenticationEntryPoint;
import com.example.security.security.filter.AjaxLoginProcessingFilter;
import com.example.security.security.handler.AjaxAccessDeniedHandler;
import com.example.security.security.handler.AjaxAuthenticationFailureHandler;
import com.example.security.security.handler.AjaxAuthenticationSuccessHandler;
import com.example.security.security.handler.FormAccessDeniedHandler;
import com.example.security.security.handler.FormAuthenticationFailureHandler;
import com.example.security.security.handler.FormAuthenticationSuccessHandler;
import com.example.security.security.provider.AjaxAuthenticationProvider;
import com.example.security.security.provider.FormAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    private FormAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private FormAuthenticationFailureHandler customAuthenticationFailureHandler;

    // 비밀번호를 암호화하는 PasswordEncoder 빈 생성
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // AuthenticationManager 빈 생성 시 UserSecurityService와 PasswordEncoder가 자동 설정됨
    // AuthenticationManager은 우리가 직접 구현한 CustomUserDetailsService를 이용해 사용자 인증 진행해줌
    @Bean
    AuthenticationManager authenticationManager() {
        List<AuthenticationProvider> authProviderList = new ArrayList<>();
        // 직접 구현한 FormAuthenticationProvider 클래스를 이용해 인증을 진행하도록 Provider 추가
        authProviderList.add(formAuthenticationProvider());
        ProviderManager providerManager = new ProviderManager(authProviderList);
        return providerManager;
    }

    @Bean
    AuthenticationManager ajaxAuthenticationManager() {
        List<AuthenticationProvider> authProviderList = new ArrayList<>();
        // 직접 구현한 AjaxAuthenticationProvider 클래스를 이용해 인증을 진행하도록 Provider 추가
        authProviderList.add(ajaxAuthenticationProvider());
        ProviderManager providerManager = new ProviderManager(authProviderList);
        return providerManager;
    }

    @Bean
    FormAuthenticationProvider formAuthenticationProvider() {
        return new FormAuthenticationProvider();
    }

    @Bean
    AjaxAuthenticationProvider ajaxAuthenticationProvider() {
        return new AjaxAuthenticationProvider();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.antMatcher("/api/**").authorizeRequests()
                .antMatchers("/", "/user", "/login*").permitAll()
                .antMatchers("/mypage").hasRole("USER")
                .antMatchers("/api/messages").hasRole("USER")
                .antMatchers("/messages").hasRole("MANAGER")
                .antMatchers("/config").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_proc")
                .authenticationDetailsSource(authenticationDetailsSource)
                .defaultSuccessUrl("/")
                .successHandler(customAuthenticationSuccessHandler) // (주의) defaultSuccessUrl()보다 뒤쪽에 위치해야 함
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll(); // 로그인 관련 URL에 인증되지 않은 사용자도 접근 가능하도록 설정

        http.exceptionHandling()
                .authenticationEntryPoint(new AjaxLoginAuthenticationEntryPoint())
                .accessDeniedHandler(ajaxAccessDeniedHandler()) // Ajax 인가 예외 발생 시 작동
                .accessDeniedHandler(accessDeniedHandler()) // 인가 예외 발생 시 작동
                .and()
                // UsernamePasswordAuthenticationFilter 필터 앞에 AjaxLoginProcessingFilter 필터 설정
                .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

        http.csrf().disable();

        return http.build();
    }

    // 인가 예외 발생 시 이동할 에러페이지 설정
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        FormAccessDeniedHandler accessDeniedHandler = new FormAccessDeniedHandler();
        accessDeniedHandler.setErrorPage("/denied");
        return accessDeniedHandler;
    }

    // 아래 요청에 대해선 시큐리티 보안이 적용되지 않도록 설정 (WebIgnore 설정)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/css/**", "/images/**", "/js/**");
    }

    // Ajax 요청을 처리할 필터 생성
    @Bean
    public AjaxLoginProcessingFilter ajaxLoginProcessingFilter() {
        AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter();
        filter.setAuthenticationManager(ajaxAuthenticationManager());
        filter.setAuthenticationSuccessHandler(ajaxAuthenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(ajaxAuthenticationFailureHandler());
        return filter;
    }

    // Ajax 요청에 대한 인증 성공 시 호출할 핸들러
    @Bean
    public AuthenticationSuccessHandler ajaxAuthenticationSuccessHandler() {
        return new AjaxAuthenticationSuccessHandler();
    }

    // Ajax 요청에 대한 인증 실패 시 호출할 핸들러
    @Bean
    public AuthenticationFailureHandler ajaxAuthenticationFailureHandler() {
        return new AjaxAuthenticationFailureHandler();
    }

    // Ajax 요청에 대한 인증 실패 시 호출할 핸들러
    @Bean
    public AccessDeniedHandler ajaxAccessDeniedHandler() {
        return new AjaxAccessDeniedHandler();
    }
}
