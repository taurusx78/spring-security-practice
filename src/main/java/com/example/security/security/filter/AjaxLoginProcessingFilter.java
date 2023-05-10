package com.example.security.security.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import com.example.security.domain.dto.AccountDto;
import com.example.security.security.token.AjaxAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private ObjectMapper objectMapper = new ObjectMapper();

    public AjaxLoginProcessingFilter() {
        // /api/login 요청 시 해당 필터 작동
        super(new AntPathRequestMatcher("/api/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        System.out.println("AjaxLoginProcessingFilter 호출됨");

        // Ajax 요청 여부 확인
        if (!isAjax(request)) {
            throw new IllegalStateException("Authentication is not supported");
        }

        // HTTP 요청 메세지에 담긴 데이터를 AccountDto 클래스로 변환
        AccountDto accountDto = objectMapper.readValue(request.getReader(), AccountDto.class);

        // 입력된 아이디와 비밀번호가 있는지 확인
        if (!StringUtils.hasText(accountDto.getUsername()) || !StringUtils.hasText(accountDto.getPassword())) {
            throw new IllegalArgumentException("Username or Password is empty");
        }

        // 입력 값을 바탕으로 토큰 생성
        AjaxAuthenticationToken ajaxAuthenticationToken = new AjaxAuthenticationToken(accountDto.getUsername(), accountDto.getPassword());
        
        // AuthenticationManager에게 토큰을 전달하면
        // 인증 성공한 사용자 정보를 담은 토큰 생성함
        return getAuthenticationManager().authenticate(ajaxAuthenticationToken);
    }

    private boolean isAjax(HttpServletRequest request) {
        // X-Requested-With 헤더의 값이 XMLHttpRequest인 경우 Ajax 요청인 것으로 설정
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return true;
        }
        return false;
    }
}
