package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		Authentication auth = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
		authManager.authenticate(auth);

		UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

		List<String> roles = userDetails.getAuthorities().stream().map(a -> a.getAuthority().replace("ROLE_", ""))
				.toList();

		String token = jwtService.generateToken(userDetails.getUsername(), roles);
		return ResponseEntity.ok(new LoginResponse(token));
	}
}
