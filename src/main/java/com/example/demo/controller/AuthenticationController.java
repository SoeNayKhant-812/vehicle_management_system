package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtService jwtService;
	private final UserService userService;

	@Autowired
	public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
			JwtService jwtService, UserService userService) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtService = jwtService;
		this.userService = userService;
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		logger.info("Attempting login for user: {}", request.getUsername());
		
//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//		String encodedPassword = encoder.encode(request.getPassword());
//		request.setPassword(encodedPassword);
		// Perform authentication
		Authentication authentication = new UsernamePasswordAuthenticationToken(request.getUsername(),
				request.getPassword());
		authenticationManager.authenticate(authentication);
		
		// 2. After successful authentication, invalidate all old tokens by updating the timestamp.
		//userService.updateTokenValidityTimestamp(request.getUsername());

		// Generate JWT token
		UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
		List<String> roles = userDetails.getAuthorities().stream()
				.map(authority -> authority.getAuthority().replace("ROLE_", "")).toList();

		String token = jwtService.generateToken(userDetails.getUsername(), roles);

		logger.info("Login successful for user: {}", request.getUsername());
		return ResponseEntity.ok(new LoginResponse(token));
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@Valid @RequestBody UserRegisterRequest request) {
		logger.info("Registering new user: {}", request.getUsername());

		userService.addUser(request);

		logger.info("Registration successful for user: {}", request.getUsername());
		return ResponseEntity.ok("User registered successfully.");
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.badRequest().body("No authenticated user to log out.");
		}
		String username = authentication.getName();
		userService.logout(username);
		return ResponseEntity.ok("Logged out successfully.");
	}
}
