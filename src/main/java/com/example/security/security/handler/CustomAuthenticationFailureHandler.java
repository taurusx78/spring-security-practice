package com.example.security.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String errorMessage = "Authentication Failure";
		
		if (exception instanceof BadCredentialsException) {
			errorMessage = "Bad Credentials Exception";
		} else if (exception instanceof InsufficientAuthenticationException) {
			errorMessage = "Insufficient Authentication Exception";
		}
		
		setDefaultFailureUrl("/login?error=true&exception=" + errorMessage);
		
		super.onAuthenticationFailure(request, response, exception);
	}
}
