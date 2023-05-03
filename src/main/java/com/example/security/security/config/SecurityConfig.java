package com.example.security.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.example.security.security.handler.CustomAccessDeniedHandler;
import com.example.security.security.handler.CustomAuthenticationFailureHandler;
import com.example.security.security.handler.CustomAuthenticationSuccessHandler;
import com.example.security.security.provider.CustomAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    
    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

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
        ProviderManager authenticationManager = (ProviderManager) authenticationConfiguration
                .getAuthenticationManager();
        // 직접 구현한 CustomAuthenticationProvider 클래스를 이용해 인증을 진행하도록 Provider 추가
        authenticationManager.getProviders().add(customAuthenticationProvider());
        return authenticationManager;
    }

    @Bean
    CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/user", "/login*").permitAll()
                .antMatchers("/mypage").hasRole("USER")
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
        		.accessDeniedHandler(accessDeniedHandler()); // 인가 예외 발생 시 작동

        return http.build();
    }

    // 인가 예외 발생 시 이동할 에러페이지 설정
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
    	CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
    	accessDeniedHandler.setErrorPage("/denied");
		return accessDeniedHandler;
	}

	// 아래 요청에 대해선 시큐리티 보안이 적용되지 않도록 설정 (WebIgnore 설정)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/css/**", "/images/**", "/js/**");
    }
}
