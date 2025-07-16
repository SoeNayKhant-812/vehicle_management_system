package com.example.demo.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	private static final Logger logger = LoggerFactory.getLogger(JwtAccessDeniedHandler.class);

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		logger.warn("Access denied - {}", accessDeniedException.getMessage());

		response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
		response.setContentType("application/json");

		String body = String.format("""
				{
				    "timestamp": "%s",
				    "status": 403,
				    "error": "Forbidden",
				    "message": "%s",
				    "path": "%s"
				}
				""", LocalDateTime.now(), accessDeniedException.getMessage(), request.getRequestURI());

		response.getWriter().write(body);
	}
}
