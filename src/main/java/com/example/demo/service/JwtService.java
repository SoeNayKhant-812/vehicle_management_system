package com.example.demo.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String rawSecret;

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(rawSecret.getBytes());
	}

	public String generateToken(String username, List<String> roles) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", roles);

		return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	public String extractUsername(String token) {
		return getClaims(token).getSubject();
	}

	public List<String> extractRoles(String token) {
		Claims claims = getClaims(token);
		Object rolesObj = claims.get("roles");
		if (rolesObj instanceof List<?> list) {
			return list.stream().map(String::valueOf).collect(Collectors.toList());
		}
		return List.of();
	}

	public boolean isTokenValid(String token, String username) {
		final String extractedUsername = extractUsername(token);
		return extractedUsername.equals(username) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return getClaims(token).getExpiration().before(new Date());
	}

	private Claims getClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}
}